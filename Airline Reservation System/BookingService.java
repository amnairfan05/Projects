import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private Connection con;

    public BookingService(Connection con) {
        this.con = con;
    }

    public String getBookingPreview(String customerId,
                                    int depInstanceId,
                                    Integer retInstanceId,
                                    String seatClass,
                                    int quantity) throws SQLException {

        StringBuilder sb = new StringBuilder();

        sb.append("Customer ID: ").append(customerId).append("\n");
        sb.append("Seat Class: ").append(seatClass).append("\n");
        sb.append("Number of Tickets: ").append(quantity).append("\n\n");

        sb.append("DEPARTING FLIGHT\n");
        sb.append(getFlightDetails(depInstanceId)).append("\n");

        double oneTicketFare = getFlightPrice(depInstanceId, seatClass);

        if (retInstanceId != null) {
            sb.append("\nRETURN FLIGHT\n");
            sb.append(getFlightDetails(retInstanceId)).append("\n");
            oneTicketFare += getFlightPrice(retInstanceId, seatClass);
        }

        double bookingFeePerTicket = 25.00;
        double totalFare = oneTicketFare * quantity;
        double totalBookingFee = bookingFeePerTicket * quantity;
        double finalTotal = totalFare + totalBookingFee;

        sb.append("\n-----------------------------\n");
        sb.append(String.format("One Ticket Fare:     $%.2f\n", oneTicketFare));
        sb.append(String.format("Booking Fee Each:    $%.2f\n", bookingFeePerTicket));
        sb.append(String.format("Quantity:            %d\n", quantity));
        sb.append(String.format("Total Flight Fare:   $%.2f\n", totalFare));
        sb.append(String.format("Total Booking Fees:  $%.2f\n", totalBookingFee));
        sb.append(String.format("Final Total:         $%.2f\n", finalTotal));
        sb.append("-----------------------------\n");

        return sb.toString();
    }

    public String getBookingPreview(String customerId,
                                    int depInstanceId,
                                    Integer retInstanceId,
                                    String seatClass) throws SQLException {
        return getBookingPreview(customerId, depInstanceId, retInstanceId, seatClass, 1);
    }

    public String buyFlight(String customerId,
                            int depInstanceId,
                            Integer retInstanceId,
                            String seatClass) throws SQLException {

        return createTicket(customerId, depInstanceId, retInstanceId, "booked", true, seatClass);
    }

    public String reserveFlight(String customerId,
                                int depInstanceId,
                                Integer retInstanceId,
                                String seatClass) throws SQLException {

        if (getSeatsAvailable(depInstanceId) <= 0) {
            throw new SQLException("No seats available. Please join the waitlist.");
        }

        if (retInstanceId != null && getSeatsAvailable(retInstanceId) <= 0) {
            throw new SQLException("No seats available on return flight. Please join the waitlist.");
        }

        return createTicket(customerId, depInstanceId, retInstanceId, "reserved", true, seatClass);
    }

    public String waitlistFlight(String customerId,
                                 int depInstanceId,
                                 Integer retInstanceId,
                                 String seatClass) throws SQLException {

        if (getSeatsAvailable(depInstanceId) > 0) {
            throw new SQLException("Seats are still available. You cannot join the waitlist.");
        }

        if (retInstanceId != null && getSeatsAvailable(retInstanceId) > 0) {
            throw new SQLException("Return flight still has seats available.");
        }

        return createTicket(customerId, depInstanceId, retInstanceId, "waitlisted", false, seatClass);
    }

    public List<String> buyMultipleFlights(String customerId,
                                           int depInstanceId,
                                           Integer retInstanceId,
                                           String seatClass,
                                           int quantity) throws SQLException {

        validateQuantity(quantity);

        if (getSeatsAvailable(depInstanceId) < quantity) {
            throw new SQLException("Not enough seats available for departing flight.");
        }

        if (retInstanceId != null && getSeatsAvailable(retInstanceId) < quantity) {
            throw new SQLException("Not enough seats available for return flight.");
        }

        List<String> ticketIds = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            ticketIds.add(buyFlight(customerId, depInstanceId, retInstanceId, seatClass));
        }

        return ticketIds;
    }

    public List<String> reserveMultipleFlights(String customerId,
                                               int depInstanceId,
                                               Integer retInstanceId,
                                               String seatClass,
                                               int quantity) throws SQLException {

        validateQuantity(quantity);

        if (getSeatsAvailable(depInstanceId) < quantity) {
            throw new SQLException("Not enough seats available for departing flight.");
        }

        if (retInstanceId != null && getSeatsAvailable(retInstanceId) < quantity) {
            throw new SQLException("Not enough seats available for return flight.");
        }

        List<String> ticketIds = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            ticketIds.add(reserveFlight(customerId, depInstanceId, retInstanceId, seatClass));
        }

        return ticketIds;
    }

    public List<String> waitlistMultipleFlights(String customerId,
                                                int depInstanceId,
                                                Integer retInstanceId,
                                                String seatClass,
                                                int quantity) throws SQLException {

        validateQuantity(quantity);

        List<String> ticketIds = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            ticketIds.add(waitlistFlight(customerId, depInstanceId, retInstanceId, seatClass));
        }

        return ticketIds;
    }

    public boolean isFlightFull(int instanceId) throws SQLException {
        return getSeatsAvailable(instanceId) == 0;
    }

    public boolean hasEnoughSeats(int instanceId, int quantity) throws SQLException {
        return getSeatsAvailable(instanceId) >= quantity;
    }

    private void validateQuantity(int quantity) throws SQLException {
        if (quantity < 1) {
            throw new SQLException("Quantity must be at least 1.");
        }
    }

    private int getSeatsAvailable(int instanceId) throws SQLException {
        String query = "SELECT seats_available FROM Flight_Instances WHERE instance_id = ?";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, instanceId);

        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            rs.close();
            ps.close();
            throw new SQLException("Flight instance not found.");
        }

        int seats = rs.getInt("seats_available");

        rs.close();
        ps.close();

        return seats;
    }

    private String createTicket(String customerId,
                                int depInstanceId,
                                Integer retInstanceId,
                                String status,
                                boolean decreaseSeats,
                                String seatClass) throws SQLException {

        String ticketType = (retInstanceId == null) ? "one-way" : "round-trip";
        String ticketId = generateTicketId();

        try {
            con.setAutoCommit(false);

            double totalFare = getFlightPrice(depInstanceId, seatClass);

            if (retInstanceId != null) {
                totalFare += getFlightPrice(retInstanceId, seatClass);
            }

            double bookingFee = 25.00;

            String insertTicket =
                    "INSERT INTO Tickets " +
                    "(tid, cid, ticket_type, total_fare, booking_fee, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(insertTicket);
            ps.setString(1, ticketId);
            ps.setString(2, customerId);
            ps.setString(3, ticketType);
            ps.setDouble(4, totalFare);
            ps.setDouble(5, bookingFee);
            ps.setString(6, status);
            ps.executeUpdate();
            ps.close();

            insertTicketFlight(ticketId, depInstanceId, 1, seatClass);

            if (decreaseSeats) {
                decreaseSeats(depInstanceId);
            } else {
                addToWaitingList(customerId, depInstanceId);
            }

            if (retInstanceId != null) {
                insertTicketFlight(ticketId, retInstanceId, 2, seatClass);

                if (decreaseSeats) {
                    decreaseSeats(retInstanceId);
                } else {
                    addToWaitingList(customerId, retInstanceId);
                }
            }

            con.commit();
            return ticketId;

        } catch (Exception ex) {
            con.rollback();
            throw ex;

        } finally {
            con.setAutoCommit(true);
        }
    }

    private String generateTicketId() {
        long timePart = System.currentTimeMillis() % 10000000000L;
        int randomPart = (int)(Math.random() * 1000);

        return String.format("T%010d%03d", timePart, randomPart);
    }

    private String getFlightDetails(int instanceId) throws SQLException {
        String query =
                "SELECT fi.instance_id, f.flight_number, al.name AS airline, " +
                "f.d_apid, f.a_apid, fi.flight_date, f.depart_time, f.arrival_time, " +
                "f.base_price, fi.seats_available " +
                "FROM Flight_Instances fi " +
                "JOIN Flights f ON fi.fid = f.fid AND fi.aid = f.aid " +
                "JOIN Airlines al ON f.aid = al.aid " +
                "WHERE fi.instance_id = ?";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, instanceId);

        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            rs.close();
            ps.close();
            throw new SQLException("Flight not found.");
        }

        String details =
                "Instance ID:     " + rs.getInt("instance_id") + "\n" +
                "Flight:          " + rs.getString("flight_number") + "\n" +
                "Airline:         " + rs.getString("airline") + "\n" +
                "Route:           " + rs.getString("d_apid") + " to " + rs.getString("a_apid") + "\n" +
                "Date:            " + rs.getDate("flight_date") + "\n" +
                "Depart:          " + rs.getTime("depart_time") + "\n" +
                "Arrive:          " + rs.getTime("arrival_time") + "\n" +
                "Base Price:      $" + rs.getDouble("base_price") + "\n" +
                "Seats Available: " + rs.getInt("seats_available") + "\n";

        rs.close();
        ps.close();

        return details;
    }

    private double getFlightPrice(int instanceId, String seatClass) throws SQLException {
        String query =
                "SELECT f.base_price FROM Flight_Instances fi " +
                "JOIN Flights f ON fi.fid = f.fid AND fi.aid = f.aid " +
                "WHERE fi.instance_id = ?";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, instanceId);

        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            rs.close();
            ps.close();
            throw new SQLException("Flight price not found.");
        }

        double basePrice = rs.getDouble("base_price");

        rs.close();
        ps.close();

        if (seatClass.equalsIgnoreCase("business")) {
            return basePrice * 1.75;
        } else if (seatClass.equalsIgnoreCase("first")) {
            return basePrice * 2.50;
        } else {
            return basePrice;
        }
    }

    private void insertTicketFlight(String ticketId,
                                    int instanceId,
                                    int segmentOrder,
                                    String seatClass) throws SQLException {

        String query =
                "INSERT INTO Ticket_Flights " +
                "(tid, instance_id, seat_number, seat_class, meal, segment_order) " +
                "VALUES (?, ?, NULL, ?, NULL, ?)";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, ticketId);
        ps.setInt(2, instanceId);
        ps.setString(3, seatClass);
        ps.setInt(4, segmentOrder);
        ps.executeUpdate();
        ps.close();
    }

    private void decreaseSeats(int instanceId) throws SQLException {
        String query =
                "UPDATE Flight_Instances " +
                "SET seats_available = seats_available - 1 " +
                "WHERE instance_id = ? AND seats_available >= 1";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, instanceId);

        int rows = ps.executeUpdate();

        ps.close();

        if (rows == 0) {
            throw new SQLException("Not enough seats available.");
        }
    }

    private void addToWaitingList(String customerId, int instanceId) throws SQLException {
        String query =
                "INSERT IGNORE INTO Waiting_List (cid, instance_id) VALUES (?, ?)";

        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, customerId);
        ps.setInt(2, instanceId);
        ps.executeUpdate();
        ps.close();
    }
}