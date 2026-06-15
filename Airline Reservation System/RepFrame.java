import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class RepFrame extends JFrame {
   private static final long serialVersionUID = 1L;
   private Connection con;
   public RepFrame(Connection con) {
       this.con = con;
       initialize();
   }
   public void initialize() {
       // ===== TITLE =====
       JLabel label = new JLabel("Customer Representative Menu");
       label.setFont(new Font("Lucida Sans", Font.BOLD, 22));
       label.setHorizontalAlignment(SwingConstants.CENTER);
       // ===== BUTTONS =====
       JButton makeReservationBtn =
               new JButton("Make Reservation");
       JButton editReservationBtn =
               new JButton("Edit Reservation");
       JButton addAirportBtn =
               new JButton("Add Airport");
       JButton editAirportBtn =
               new JButton("Edit Airport");
       JButton deleteAirportBtn =
               new JButton("Delete Airport");
       JButton addAircraftBtn =
               new JButton("Add Aircraft");
       JButton editAircraftBtn =
               new JButton("Edit Aircraft");
       JButton deleteAircraftBtn =
               new JButton("Delete Aircraft");
       JButton addFlightBtn =
               new JButton("Add Flight");
       JButton editFlightBtn =
               new JButton("Edit Flight");
       JButton deleteFlightBtn =
               new JButton("Delete Flight");
       JButton waitingListBtn =
               new JButton("Waiting List");
       JButton airportFlightsBtn =
               new JButton("Flights By Airport");
       JButton replyQuestionBtn =
               new JButton("Reply To Customer");
       // ===== BUTTON PANEL =====
       JPanel buttonPanel = new JPanel();
       buttonPanel.setLayout(new GridLayout(7, 2, 15, 15));
       buttonPanel.setBackground(new Color(144, 238, 144));
       buttonPanel.add(makeReservationBtn);
       buttonPanel.add(editReservationBtn);
       buttonPanel.add(addAirportBtn);
       buttonPanel.add(editAirportBtn);
       buttonPanel.add(deleteAirportBtn);
       buttonPanel.add(addAircraftBtn);
       buttonPanel.add(editAircraftBtn);
       buttonPanel.add(deleteAircraftBtn);
       buttonPanel.add(addFlightBtn);
       buttonPanel.add(editFlightBtn);
       buttonPanel.add(deleteFlightBtn);
       buttonPanel.add(waitingListBtn);
       buttonPanel.add(airportFlightsBtn);
       buttonPanel.add(replyQuestionBtn);
       // ===== MAIN PANEL =====
       JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
       mainPanel.setBackground(new Color(144, 238, 144));
       mainPanel.setBorder(
               BorderFactory.createEmptyBorder(20, 40, 20, 40));
       mainPanel.add(label, BorderLayout.NORTH);
       mainPanel.add(buttonPanel, BorderLayout.CENTER);
       this.add(mainPanel);
       this.setTitle("Customer Representative");
       this.setSize(750, 550);
       this.setLocationRelativeTo(null);
       this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       this.setVisible(true);
       // ===== ACTIONS =====
       makeReservationBtn.addActionListener(
               e -> makeReservation());
       editReservationBtn.addActionListener(
               e -> editReservation());
       addAirportBtn.addActionListener(
               e -> addAirport());
       editAirportBtn.addActionListener(
               e -> editAirport());
       deleteAirportBtn.addActionListener(
               e -> deleteAirport());
       addAircraftBtn.addActionListener(
               e -> addAircraft());
       editAircraftBtn.addActionListener(
               e -> editAircraft());
       deleteAircraftBtn.addActionListener(
               e -> deleteAircraft());
       addFlightBtn.addActionListener(
               e -> addFlight());
       editFlightBtn.addActionListener(
               e -> editFlight());
       deleteFlightBtn.addActionListener(
               e -> deleteFlight());
       waitingListBtn.addActionListener(
       		e -> waitingList());
      
       airportFlightsBtn.addActionListener(
               e -> flightsByAirport());
       replyQuestionBtn.addActionListener(
               e -> replyQuestion());
   }
   // ========================================================
   // MAKE RESERVATION
   // ========================================================
   private void makeReservation() {
       JTextField tidField = new JTextField();
       JTextField cidField = new JTextField();
       JTextField fareField = new JTextField();
       JTextField feeField = new JTextField();
       Object[] fields = {
               "Ticket ID:", tidField,
               "Customer ID:", cidField,
               "Total Fare:", fareField,
               "Booking Fee:", feeField
       };
       int result = JOptionPane.showConfirmDialog(
               this,
               fields,
               "Make Reservation",
               JOptionPane.OK_CANCEL_OPTION
       );
       if (result == JOptionPane.OK_OPTION) {
           try {
               String sql =
                       "INSERT INTO Tickets " +
                       "(tid, cid, ticket_type, total_fare, booking_fee, purchase_time, status) " +
                       "VALUES (?, ?, 'one-way', ?, ?, NOW(), 'booked')";
               PreparedStatement ps =
                       con.prepareStatement(sql);
               ps.setString(1, tidField.getText());
               ps.setString(2, cidField.getText());
               ps.setDouble(3,
                       Double.parseDouble(fareField.getText()));
               ps.setDouble(4,
                       Double.parseDouble(feeField.getText()));
               ps.executeUpdate();
               JOptionPane.showMessageDialog(
                       this,
                       "Reservation Created"
               );
           } catch (Exception ex) {
               JOptionPane.showMessageDialog(
                       this,
                       ex.getMessage()
               );
           }
       }
   }
   // ========================================================
   // EDIT RESERVATION
   // ========================================================
   private void editReservation() {
       String tid = JOptionPane.showInputDialog(
               this,
               "Enter Ticket ID"
       );
       if (tid == null || tid.isEmpty()) {
           return;
       }
       try {
           String sql =
                   "SELECT * FROM Tickets WHERE tid = ?";
           PreparedStatement ps =
                   con.prepareStatement(sql);
           ps.setString(1, tid);
           ResultSet rs = ps.executeQuery();
           if (rs.next()) {
               JTextField fareField =
                       new JTextField(
                               rs.getString("total_fare"));
               JTextField feeField =
                       new JTextField(
                               rs.getString("booking_fee"));
               JTextField statusField =
                       new JTextField(
                               rs.getString("status"));
               Object[] fields = {
                       "Total Fare:", fareField,
                       "Booking Fee:", feeField,
                       "Status:", statusField
               };
               int result =
                       JOptionPane.showConfirmDialog(
                               this,
                               fields,
                               "Edit Reservation",
                               JOptionPane.OK_CANCEL_OPTION
                       );
               if (result == JOptionPane.OK_OPTION) {
                   String update =
                           "UPDATE Tickets " +
                           "SET total_fare=?, booking_fee=?, status=? " +
                           "WHERE tid=?";
                   PreparedStatement ups =
                           con.prepareStatement(update);
                   ups.setDouble(1,
                           Double.parseDouble(
                                   fareField.getText()));
                   ups.setDouble(2,
                           Double.parseDouble(
                                   feeField.getText()));
                   ups.setString(3,
                           statusField.getText());
                   ups.setString(4, tid);
                   ups.executeUpdate();
                   JOptionPane.showMessageDialog(
                           this,
                           "Reservation Updated"
                   );
               }
           } else {
               JOptionPane.showMessageDialog(
                       this,
                       "Ticket Not Found"
               );
           }
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(
                   this,
                   ex.getMessage()
           );
       }
   }
   // ========================================================
   // AIRPORTS
   // ========================================================
   private void addAirport() {
       JTextField apid = new JTextField();
       JTextField name = new JTextField();
       JTextField street = new JTextField();
       JTextField city = new JTextField();
       JTextField state = new JTextField();
       JTextField country = new JTextField();
       Object[] fields = {
               "Airport ID:", apid,
               "Name:", name,
               "Street:", street,
               "City:", city,
               "State:", state,
               "Country:", country
       };
       int result = JOptionPane.showConfirmDialog(
               this,
               fields,
               "Add Airport",
               JOptionPane.OK_CANCEL_OPTION
       );
       if (result != JOptionPane.OK_OPTION) return;
       try {
           String sql =
                   "INSERT INTO Airports (apid, name, street, city, state, country) " +
                   "VALUES (?, ?, ?, ?, ?, ?)";
           PreparedStatement ps = con.prepareStatement(sql);
           ps.setString(1, apid.getText());
           ps.setString(2, name.getText());
           ps.setString(3, street.getText());
           ps.setString(4, city.getText());
           ps.setString(5, state.getText());
           ps.setString(6, country.getText());
           ps.executeUpdate();
           JOptionPane.showMessageDialog(this, "Airport Added");
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(this, ex.getMessage());
       }
   }
  
   private void editAirport() {
       String apid = JOptionPane.showInputDialog(
               this,
               "Enter Airport ID"
       );
       if (apid == null || apid.isEmpty()) {
           return;
       }
       try {
           String sql =
                   "SELECT * FROM Airports WHERE apid=?";
           PreparedStatement ps =
                   con.prepareStatement(sql);
           ps.setString(1, apid);
           ResultSet rs = ps.executeQuery();
           if (rs.next()) {
               JTextField nameField =
                       new JTextField(rs.getString("name"));
               JTextField streetField =
                       new JTextField(rs.getString("street"));
               JTextField cityField =
                       new JTextField(rs.getString("city"));
               JTextField stateField =
                       new JTextField(rs.getString("state"));
               JTextField countryField =
                       new JTextField(rs.getString("country"));
               Object[] fields = {
                       "Name:", nameField,
                       "Street:", streetField,
                       "City:", cityField,
                       "State:", stateField,
                       "Country:", countryField
               };
               int result = JOptionPane.showConfirmDialog(
                       this,
                       fields,
                       "Edit Airport",
                       JOptionPane.OK_CANCEL_OPTION
               );
               if (result == JOptionPane.OK_OPTION) {
                   String update =
                           "UPDATE Airports " +
                           "SET name=?, street=?, city=?, state=?, country=? " +
                           "WHERE apid=?";
                   PreparedStatement ups =
                           con.prepareStatement(update);
                   ups.setString(1, nameField.getText());
                   ups.setString(2, streetField.getText());
                   ups.setString(3, cityField.getText());
                   ups.setString(4, stateField.getText());
                   ups.setString(5, countryField.getText());
                   ups.setString(6, apid);
                   ups.executeUpdate();
                   JOptionPane.showMessageDialog(
                           this,
                           "Airport Updated"
                   );
               }
           } else {
               JOptionPane.showMessageDialog(
                       this,
                       "Airport Not Found"
               );
           }
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(
                   this,
                   ex.getMessage()
           );
       }
   }
  
   private void deleteAirport() {
       String apid = JOptionPane.showInputDialog(
               this,
               "Enter Airport ID"
       );
       try {
           String sql =
                   "DELETE FROM Airports WHERE apid=?";
           PreparedStatement ps =
                   con.prepareStatement(sql);
           ps.setString(1, apid);
           int rows = ps.executeUpdate();
           if (rows > 0) {
               JOptionPane.showMessageDialog(
                       this,
                       "Airport Deleted"
               );
           } else {
               JOptionPane.showMessageDialog(
                       this,
                       "Airport Not Found"
               );
           }
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(
                   this,
                   ex.getMessage()
           );
       }
   }
   // ========================================================
   // AIRCRAFTS
   // ========================================================
   private void addAircraft() {
       JTextField acid = new JTextField();
       JTextField aid = new JTextField();
       JTextField model = new JTextField();
       JTextField econ = new JTextField();
       JTextField bus = new JTextField();
       JTextField first = new JTextField();
       Object[] fields = {
               "Aircraft ID:", acid,
               "Airline ID:", aid,
               "Model:", model,
               "Economy Seats:", econ,
               "Business Seats:", bus,
               "First Seats:", first
       };
       int result = JOptionPane.showConfirmDialog(
               this,
               fields,
               "Add Aircraft",
               JOptionPane.OK_CANCEL_OPTION
       );
       if (result != JOptionPane.OK_OPTION) return;
       try {
           String sql =
                   "INSERT INTO Aircrafts (acid, aid, model, economy_seats, business_seats, first_seats) " +
                   "VALUES (?, ?, ?, ?, ?, ?)";
           PreparedStatement ps = con.prepareStatement(sql);
           ps.setString(1, acid.getText());
           ps.setString(2, aid.getText());
           ps.setString(3, model.getText());
           ps.setInt(4, Integer.parseInt(econ.getText()));
           ps.setInt(5, Integer.parseInt(bus.getText()));
           ps.setInt(6, Integer.parseInt(first.getText()));
           ps.executeUpdate();
           JOptionPane.showMessageDialog(this, "Aircraft Added");
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(this, ex.getMessage());
       }
   }
   private void editAircraft() {
       String acid = JOptionPane.showInputDialog(
               this,
               "Enter Aircraft ID"
       );
       if (acid == null || acid.isEmpty()) {
           return;
       }
       try {
           String sql =
                   "SELECT * FROM Aircrafts WHERE acid=?";
           PreparedStatement ps =
                   con.prepareStatement(sql);
           ps.setString(1, acid);
           ResultSet rs = ps.executeQuery();
           if (rs.next()) {
               JTextField aidField =
                       new JTextField(rs.getString("aid"));
               JTextField modelField =
                       new JTextField(rs.getString("model"));
               JTextField ecoField =
                       new JTextField(rs.getString("economy_seats"));
               JTextField busField =
                       new JTextField(rs.getString("business_seats"));
               JTextField firstField =
                       new JTextField(rs.getString("first_seats"));
               Object[] fields = {
                       "Airline ID:", aidField,
                       "Model:", modelField,
                       "Economy Seats:", ecoField,
                       "Business Seats:", busField,
                       "First Seats:", firstField
               };
               int result = JOptionPane.showConfirmDialog(
                       this,
                       fields,
                       "Edit Aircraft",
                       JOptionPane.OK_CANCEL_OPTION
               );
               if (result == JOptionPane.OK_OPTION) {
                   String update =
                           "UPDATE Aircrafts " +
                           "SET aid=?, model=?, economy_seats=?, business_seats=?, first_seats=? " +
                           "WHERE acid=?";
                   PreparedStatement ups =
                           con.prepareStatement(update);
                   ups.setString(1, aidField.getText());
                   ups.setString(2, modelField.getText());
                   ups.setInt(3,
                           Integer.parseInt(ecoField.getText()));
                   ups.setInt(4,
                           Integer.parseInt(busField.getText()));
                   ups.setInt(5,
                           Integer.parseInt(firstField.getText()));
                   ups.setString(6, acid);
                   ups.executeUpdate();
                   JOptionPane.showMessageDialog(
                           this,
                           "Aircraft Updated"
                   );
               }
           } else {
               JOptionPane.showMessageDialog(
                       this,
                       "Aircraft Not Found"
               );
           }
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(
                   this,
                   ex.getMessage()
           );
       }
   }
   private void deleteAircraft() {
       String acid = JOptionPane.showInputDialog(
               this,
               "Enter Aircraft ID"
       );
       try {
           String sql =
                   "DELETE FROM Aircrafts WHERE acid=?";
           PreparedStatement ps =
                   con.prepareStatement(sql);
           ps.setString(1, acid);
           int rows = ps.executeUpdate();
           if (rows > 0) {
               JOptionPane.showMessageDialog(
                       this,
                       "Aircraft Deleted"
               );
           } else {
               JOptionPane.showMessageDialog(
                       this,
                       "Aircraft Not Found"
               );
           }
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(
                   this,
                   ex.getMessage()
           );
       }
   }
   // ========================================================
   // FLIGHTS
   // ========================================================
   private void addFlight() {

        JTextField fid = new JTextField();
        JTextField aid = new JTextField();
        JTextField acid = new JTextField();
        JTextField flightNum = new JTextField();
        JTextField dApid = new JTextField();
        JTextField aApid = new JTextField();
        JTextField depart = new JTextField();
        JTextField arrival = new JTextField();

        String[] types = {"domestic", "international"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        JTextField price = new JTextField();

        Object[] fields = {
                "Flight ID:", fid,
                "Airline ID:", aid,
                "Aircraft ID:", acid,
                "Flight Number:", flightNum,
                "Departure Airport:", dApid,
                "Arrival Airport:", aApid,
                "Depart Time (HH:MM:SS):", depart,
                "Arrival Time (HH:MM:SS):", arrival,
                "Flight Type:", typeBox,
                "Base Price:", price
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add Flight",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String flightId = fid.getText().trim();
            String airlineId = aid.getText().trim().toUpperCase();
            String aircraftId = acid.getText().trim();
            String flightNumber = flightNum.getText().trim().toUpperCase();
            String depAirport = dApid.getText().trim().toUpperCase();
            String arrAirport = aApid.getText().trim().toUpperCase();
            String departTime = depart.getText().trim();
            String arrivalTime = arrival.getText().trim();
            String flightType = typeBox.getSelectedItem().toString();
            double basePrice = Double.parseDouble(price.getText().trim());

            if (flightId.isEmpty() || airlineId.isEmpty() || aircraftId.isEmpty()
                    || flightNumber.isEmpty() || depAirport.isEmpty() || arrAirport.isEmpty()
                    || departTime.isEmpty() || arrivalTime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            if (depAirport.equals(arrAirport)) {
                JOptionPane.showMessageDialog(this, "Departure and arrival airports cannot be the same.");
                return;
            }

            Time depTime = Time.valueOf(departTime);
            Time arrTime = Time.valueOf(arrivalTime);

            String sql =
                    "INSERT INTO Flights " +
                    "(fid, aid, acid, flight_number, d_apid, a_apid, depart_time, arrival_time, flight_type, base_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, flightId);
            ps.setString(2, airlineId);
            ps.setString(3, aircraftId);
            ps.setString(4, flightNumber);
            ps.setString(5, depAirport);
            ps.setString(6, arrAirport);
            ps.setTime(7, depTime);
            ps.setTime(8, arrTime);
            ps.setString(9, flightType);
            ps.setDouble(10, basePrice);

            ps.executeUpdate();
            ps.close();

            JOptionPane.showMessageDialog(this, "Flight Added Successfully");

        
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not add flight.\n\n" +
                    "Check that:\n" +
                    "- Flight ID is not already used\n" +
                    "- Flight number is not already used for that airline\n" +
                    "- Airline ID exists\n" +
                    "- Aircraft ID belongs to that airline\n" +
                    "- Airport codes exist"
            );
            ex.printStackTrace();

        }catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid price.\nEnter a number like 300 or 300.00."
            );
        }
        catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid time format.\nUse HH:MM:SS, like 08:00:00."
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
   
   private void editFlight() {
       String fid = JOptionPane.showInputDialog(
               this,
               "Enter Flight ID"
       );
       if (fid == null || fid.isEmpty()) {
           return;
       }
       try {
           String sql =
                   "SELECT * FROM Flights WHERE fid=?";
           PreparedStatement ps =
                   con.prepareStatement(sql);
           ps.setString(1, fid);
           ResultSet rs = ps.executeQuery();
           if (rs.next()) {
               JTextField flightNumField =
                       new JTextField(rs.getString("flight_number"));
               JTextField departField =
                       new JTextField(rs.getString("d_apid"));
               JTextField arriveField =
                       new JTextField(rs.getString("a_apid"));
               JTextField departTimeField =
                       new JTextField(rs.getString("depart_time"));
               JTextField arrivalTimeField =
                       new JTextField(rs.getString("arrival_time"));
               JTextField priceField =
                       new JTextField(rs.getString("base_price"));
               Object[] fields = {
                       "Flight Number:", flightNumField,
                       "Departure Airport:", departField,
                       "Arrival Airport:", arriveField,
                       "Departure Time:", departTimeField,
                       "Arrival Time:", arrivalTimeField,
                       "Base Price:", priceField
               };
               int result = JOptionPane.showConfirmDialog(
                       this,
                       fields,
                       "Edit Flight",
                       JOptionPane.OK_CANCEL_OPTION
               );
               if (result == JOptionPane.OK_OPTION) {
                   String update =
                           "UPDATE Flights " +
                           "SET flight_number=?, d_apid=?, a_apid=?, " +
                           "depart_time=?, arrival_time=?, base_price=? " +
                           "WHERE fid=?";
                   PreparedStatement ups =
                           con.prepareStatement(update);
                   ups.setString(1, flightNumField.getText());
                   ups.setString(2, departField.getText());
                   ups.setString(3, arriveField.getText());
                   ups.setTime(4,
                           Time.valueOf(departTimeField.getText()));
                   ups.setTime(5,
                           Time.valueOf(arrivalTimeField.getText()));
                   ups.setDouble(6,
                           Double.parseDouble(priceField.getText()));
                   ups.setString(7, fid);
                   ups.executeUpdate();
                   JOptionPane.showMessageDialog(
                           this,
                           "Flight Updated"
                   );
               }
           } else {
               JOptionPane.showMessageDialog(
                       this,
                       "Flight Not Found"
               );
           }
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(
                   this,
                   ex.getMessage()
           );
       }
   }
  
   private void deleteFlight() {
       String fid = JOptionPane.showInputDialog(
               this,
               "Enter Flight ID"
       );
       try {
           String sql =
                   "DELETE FROM Flights WHERE fid=?";
           PreparedStatement ps =
                   con.prepareStatement(sql);
           ps.setString(1, fid);
           int rows = ps.executeUpdate();
           if (rows > 0) {
               JOptionPane.showMessageDialog(
                       this,
                       "Flight Deleted"
               );
           } else {
               JOptionPane.showMessageDialog(
                       this,
                       "Flight Not Found"
               );
           }
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(
                   this,
                   ex.getMessage()
           );
       }
   }
   // ========================================================
   // WAITING LIST
   // ========================================================
   private void waitingList() {
       String flightID = JOptionPane.showInputDialog(
               this,
               "Enter Flight ID"
       );
       try {
           String sql =
                   "SELECT w.waitlist_id, w.cid,c.first_name, c.last_name, w.instance_id, w.request_time " +
                   "FROM Waiting_List w " +
                   "JOIN Customers c ON w.cid = c.cid " +
                   "JOIN Flight_Instances fi ON w.instance_id = fi.instance_id " +
                   "WHERE fi.fid = ?";
           PreparedStatement ps =
                   con.prepareStatement(sql);
           ps.setString(1, flightID);
           ResultSet rs = ps.executeQuery();
           displayResultSet(rs,
                   "Waiting List");
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(
                   this,
                   ex.getMessage()
           );
       }
   }
   // ========================================================
   // FLIGHTS BY AIRPORT
   // ========================================================
   private void flightsByAirport() {
       String airport = JOptionPane.showInputDialog(
               this,
               "Enter Airport ID"
       );
       try {
           String sql =
                   "SELECT flight_number, d_apid, a_apid " +
                   "FROM Flights " +
                   "WHERE d_apid=? OR a_apid=?";
           PreparedStatement ps =
                   con.prepareStatement(sql);
           ps.setString(1, airport);
           ps.setString(2, airport);
           ResultSet rs = ps.executeQuery();
           displayResultSet(rs,
                   "Flights For Airport");
       } catch (Exception ex) {
           JOptionPane.showMessageDialog(
                   this,
                   ex.getMessage()
           );
       }
   }
   // ========================================================
   // REPLY TO QUESTIONS
   // ========================================================
       private void replyQuestion() {
       JFrame frame = new JFrame("Customer Questions");
       frame.setSize(700, 400);
       frame.setLocationRelativeTo(null);
       frame.setLayout(new BorderLayout());
       DefaultListModel<String> model = new DefaultListModel<>();
       // load pending questions
       for (int i = 0; i < Question.questions.size(); i++) {
           Question q = Question.questions.get(i);
           if (!q.answered) {
               model.addElement(
                   "CID: " + q.customerId + " | " + q.question
               );
           }
       }
       JList<String> list = new JList<>(model);
       JButton answerBtn = new JButton("Question Selected");
       answerBtn.addActionListener(e -> {
           int index = list.getSelectedIndex();
           if (index < 0) {
               JOptionPane.showMessageDialog(frame, "Select a question.");
               return;
           }
           // map to actual question
           int realIndex = -1;
           int count = -1;
           for (int i = 0; i < Question.questions.size(); i++) {
               if (!Question.questions.get(i).answered) {
                   count++;
                   if (count == index) {
                       realIndex = i;
                       break;
                   }
               }
           }
           Question q = Question.questions.get(realIndex);
           String answer = JOptionPane.showInputDialog(
                   frame,
                   "Answer:\n\n" + q.question
           );
           if (answer == null || answer.trim().isEmpty()) return;
           q.answer = answer.trim();
           q.answered = true;
           JOptionPane.showMessageDialog(frame, "Answer submitted.");
           frame.dispose();
           replyQuestion(); // refresh
       });
       frame.add(new JScrollPane(list), BorderLayout.CENTER);
       frame.add(answerBtn, BorderLayout.SOUTH);
       frame.setVisible(true);
   }
   // ========================================================
   // DISPLAY TABLE
   // ========================================================
   private void displayResultSet(ResultSet rs, String title)
           throws SQLException {
       ResultSetMetaData meta =
               rs.getMetaData();
       int columns =
               meta.getColumnCount();
       DefaultTableModel model =
               new DefaultTableModel();
       for (int i = 1; i <= columns; i++) {
           model.addColumn(
                   meta.getColumnName(i)
           );
       }
       while (rs.next()) {
           Object[] row =
                   new Object[columns];
           for (int i = 1; i <= columns; i++) {
               row[i - 1] =
                       rs.getObject(i);
           }
           model.addRow(row);
       }
       JTable table =
               new JTable(model);
       JScrollPane scrollPane =
               new JScrollPane(table);
       JFrame frame =
               new JFrame(title);
       frame.add(scrollPane);
       frame.setSize(700, 400);
       frame.setLocationRelativeTo(null);
       frame.setVisible(true);
   } }
