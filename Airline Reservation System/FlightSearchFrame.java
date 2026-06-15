import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class FlightSearchFrame extends JFrame {
    private Connection con;
    private String customerId;

    public FlightSearchFrame(Connection con, String customerId) {
        this.con = con;
        this.customerId = customerId;

        setTitle("Search Flights");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextField fromField = new JTextField(20);
        JTextField toField = new JTextField(20);
        JTextField departDateField = new JTextField(20);
        JTextField returnDateField = new JTextField(20);
        JTextField maxPriceField = new JTextField(10);
        JTextField airlineField = new JTextField(10);
        JTextField earliestDepartField = new JTextField(10);
        JTextField latestDepartField = new JTextField(10);
        JTextField earliestArrivalField = new JTextField(10);
        JTextField latestArrivalField = new JTextField(10);

        JRadioButton oneWayBtn = new JRadioButton("One Way", true);
        JRadioButton roundTripBtn = new JRadioButton("Round Trip");

        ButtonGroup tripGroup = new ButtonGroup();
        tripGroup.add(oneWayBtn);
        tripGroup.add(roundTripBtn);

        JButton searchBtn = new JButton("Search");

        JLabel returnLabel = new JLabel("Return Date (YYYY-MM-DD):");
        returnLabel.setVisible(false);
        returnDateField.setVisible(false);

        JPanel tripPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tripPanel.add(oneWayBtn);
        tripPanel.add(roundTripBtn);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("From Airport Code or Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(fromField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        inputPanel.add(new JLabel("To Airport Code or Name:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(toField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Trip Type:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(tripPanel, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        inputPanel.add(new JLabel("Max Price:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(maxPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Depart Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(departDateField, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        inputPanel.add(returnLabel, gbc);
        gbc.gridx = 3;
        inputPanel.add(returnDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Airline:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(airlineField, gbc);

        gbc.gridx = 2; gbc.gridy = 3;
        inputPanel.add(new JLabel("Stops:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(new JLabel("Direct only (0 stops)"), gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Earliest Takeoff (HH:MM):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(earliestDepartField, gbc);

        gbc.gridx = 2; gbc.gridy = 4;
        inputPanel.add(new JLabel("Latest Takeoff (HH:MM):"), gbc);
        gbc.gridx = 3;
        inputPanel.add(latestDepartField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        inputPanel.add(new JLabel("Earliest Landing (HH:MM):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(earliestArrivalField, gbc);

        gbc.gridx = 2; gbc.gridy = 5;
        inputPanel.add(new JLabel("Latest Landing (HH:MM):"), gbc);
        gbc.gridx = 3;
        inputPanel.add(latestArrivalField, gbc);

        gbc.gridx = 3; gbc.gridy = 6;
        inputPanel.add(searchBtn, gbc);

        String[] columns = {
            "Instance ID", "Flight", "Airline",
            "From", "To", "Date", "Depart", "Arrive",
            "Duration", "Price", "Availability"
        };

        DefaultTableModel departModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        DefaultTableModel returnModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable departTable = new JTable(departModel);
        JTable returnTable = new JTable(returnModel);
        departTable.setAutoCreateRowSorter(true);
        returnTable.setAutoCreateRowSorter(true);

        JPanel departPanel = new JPanel(new BorderLayout());
        departPanel.add(new JLabel("  Departing Flights"), BorderLayout.NORTH);
        departPanel.add(new JScrollPane(departTable), BorderLayout.CENTER);

        JPanel returnPanel = new JPanel(new BorderLayout());
        returnPanel.add(new JLabel("  Return Flights"), BorderLayout.NORTH);
        returnPanel.add(new JScrollPane(returnTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, departPanel, returnPanel);
        splitPane.setDividerLocation(600);
        returnPanel.setVisible(false);

        JLabel summaryLabel = new JLabel("Select a flight to book.");
        JButton bookBtn = new JButton("Book Selected Flight(s)");
        bookBtn.setEnabled(false);

        JPanel summaryPanel = new JPanel(new BorderLayout(10, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        summaryPanel.add(summaryLabel, BorderLayout.CENTER);
        summaryPanel.add(bookBtn, BorderLayout.EAST);

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.add(splitPane, BorderLayout.CENTER);
        resultsPanel.add(summaryPanel, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);

        oneWayBtn.addActionListener(e -> {
            returnLabel.setVisible(false);
            returnDateField.setVisible(false);
            returnPanel.setVisible(false);
            splitPane.setDividerLocation(600);
            summaryLabel.setText("Select a flight to book.");
            bookBtn.setEnabled(false);
            inputPanel.revalidate();
            inputPanel.repaint();
            resultsPanel.revalidate();
            resultsPanel.repaint();
        });

        roundTripBtn.addActionListener(e -> {
            returnLabel.setVisible(true);
            returnDateField.setVisible(true);
            returnPanel.setVisible(true);
            splitPane.setDividerLocation(300);
            summaryLabel.setText("Select a departing and returning flight.");
            bookBtn.setEnabled(false);
            inputPanel.revalidate();
            inputPanel.repaint();
            resultsPanel.revalidate();
            resultsPanel.repaint();
        });

        ListSelectionListener selectionListener = e -> {
            if (e.getValueIsAdjusting()) return;

            int depRow = departTable.getSelectedRow();
            int retRow = returnTable.getSelectedRow();

            if (roundTripBtn.isSelected()) {
                if (depRow >= 0 && retRow >= 0) {
                    double depPrice = (double) departModel.getValueAt(
                        departTable.convertRowIndexToModel(depRow), 9);
                    double retPrice = (double) returnModel.getValueAt(
                        returnTable.convertRowIndexToModel(retRow), 9);
                    double total = depPrice + retPrice;
                    summaryLabel.setText(String.format(
                        "Departing: $%.2f | Returning: $%.2f | Total: $%.2f",
                        depPrice, retPrice, total));
                    bookBtn.setEnabled(true);
                } else if (depRow >= 0) {
                    double depPrice = (double) departModel.getValueAt(
                        departTable.convertRowIndexToModel(depRow), 9);
                    summaryLabel.setText(String.format(
                        "Departing: $%.2f | Select a return flight", depPrice));
                    bookBtn.setEnabled(false);
                } else {
                    summaryLabel.setText("Select a departing and returning flight.");
                    bookBtn.setEnabled(false);
                }
            } else {
                if (depRow >= 0) {
                    double price = (double) departModel.getValueAt(
                        departTable.convertRowIndexToModel(depRow), 9);
                    summaryLabel.setText(String.format("Selected flight: $%.2f", price));
                    bookBtn.setEnabled(true);
                } else {
                    summaryLabel.setText("Select a flight to book.");
                    bookBtn.setEnabled(false);
                }
            }
        };

        departTable.getSelectionModel().addListSelectionListener(selectionListener);
        returnTable.getSelectionModel().addListSelectionListener(selectionListener);

        searchBtn.addActionListener(e -> {
            departModel.setRowCount(0);
            returnModel.setRowCount(0);
            bookBtn.setEnabled(false);

            String fromInput = fromField.getText().trim();
            String toInput = toField.getText().trim();

            if (fromInput.isEmpty() || toInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter both airports.");
                return;
            }

            LocalDate departDate;
            try {
                departDate = LocalDate.parse(departDateField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid depart date. Use YYYY-MM-DD.");
                return;
            }

            Double maxPrice = null;
            if (!maxPriceField.getText().trim().isEmpty()) {
                try {
                    maxPrice = Double.parseDouble(maxPriceField.getText().trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid max price.");
                    return;
                }
            }

            LocalTime earliestDepart;
            LocalTime latestDepart;
            LocalTime earliestArrival;
            LocalTime latestArrival;

            try {
                earliestDepart = parseOptionalTime(earliestDepartField.getText().trim());
                latestDepart = parseOptionalTime(latestDepartField.getText().trim());
                earliestArrival = parseOptionalTime(earliestArrivalField.getText().trim());
                latestArrival = parseOptionalTime(latestArrivalField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid time. Use HH:MM.");
                return;
            }

            String airlineFilter = airlineField.getText().trim();
            LocalDate departStart = departDate.minusDays(3);
            LocalDate departEnd = departDate.plusDays(3);

            try {
                searchFlights(
                    fromInput, toInput,
                    departStart, departEnd, departDate,
                    maxPrice, airlineFilter,
                    earliestDepart, latestDepart,
                    earliestArrival, latestArrival,
                    departModel
                );

                if (roundTripBtn.isSelected()) {
                    LocalDate returnDate;
                    try {
                        returnDate = LocalDate.parse(returnDateField.getText().trim());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid return date. Use YYYY-MM-DD.");
                        return;
                    }

                    LocalDate returnStart = returnDate.minusDays(3);
                    LocalDate returnEnd = returnDate.plusDays(3);

                    searchFlights(
                        toInput, fromInput,
                        returnStart, returnEnd, returnDate,
                        maxPrice, airlineFilter,
                        earliestDepart, latestDepart,
                        earliestArrival, latestArrival,
                        returnModel
                    );

                    returnPanel.setVisible(true);
                    splitPane.setDividerLocation(300);
                    summaryLabel.setText("Select a departing and returning flight.");
                } else {
                    returnPanel.setVisible(false);
                    splitPane.setDividerLocation(600);
                    summaryLabel.setText("Select a flight to book.");
                }

                resultsPanel.revalidate();
                resultsPanel.repaint();

                if (departModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No departing flights found.");
                }

                if (roundTripBtn.isSelected() && returnModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No return flights found.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error searching flights.");
                ex.printStackTrace();
            }
        });

        bookBtn.addActionListener(e -> {
            int depRow = departTable.getSelectedRow();
            if (depRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a departing flight.");
                return;
            }

            int depModelRow = departTable.convertRowIndexToModel(depRow);
            int depInstanceId = (int) departModel.getValueAt(depModelRow, 0);
            Integer retInstanceId = null;

            if (roundTripBtn.isSelected()) {
                int retRow = returnTable.getSelectedRow();
                if (retRow < 0) {
                    JOptionPane.showMessageDialog(this, "Please select a return flight.");
                    return;
                }
                int retModelRow = returnTable.convertRowIndexToModel(retRow);
                retInstanceId = (int) returnModel.getValueAt(retModelRow, 0);
            }

            new BookingReviewFrame(con, customerId, depInstanceId, retInstanceId);
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private LocalTime parseOptionalTime(String text) {
        if (text == null || text.isEmpty()) return null;
        return LocalTime.parse(text);
    }

    private void searchFlights(
        String fromInput,
        String toInput,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate targetDate,
        Double maxPrice,
        String airlineFilter,
        LocalTime earliestDepart,
        LocalTime latestDepart,
        LocalTime earliestArrival,
        LocalTime latestArrival,
        DefaultTableModel tableModel
    ) throws SQLException {

        StringBuilder query = new StringBuilder(
            "SELECT fi.instance_id, f.flight_number, al.name AS airline, " +
            "f.d_apid, f.a_apid, fi.flight_date, f.depart_time, f.arrival_time, " +
            "TIMEDIFF(f.arrival_time, f.depart_time) AS duration, " +
            "f.base_price, fi.seats_available " +
            "FROM Flight_Instances fi " +
            "JOIN Flights f ON fi.fid = f.fid AND fi.aid = f.aid " +
            "JOIN Airlines al ON f.aid = al.aid " +
            "JOIN Airports da ON f.d_apid = da.apid " +
            "JOIN Airports aa ON f.a_apid = aa.apid " +
            "WHERE (f.d_apid = ? OR da.name LIKE ?) " +
            "AND (f.a_apid = ? OR aa.name LIKE ?) " +
            "AND fi.flight_date BETWEEN ? AND ? "
        );

        if (maxPrice != null) {
            query.append("AND f.base_price <= ? ");
        }
        if (!airlineFilter.isEmpty()) {
            query.append("AND al.name LIKE ? ");
        }
        if (earliestDepart != null) {
            query.append("AND f.depart_time >= ? ");
        }
        if (latestDepart != null) {
            query.append("AND f.depart_time <= ? ");
        }
        if (earliestArrival != null) {
            query.append("AND f.arrival_time >= ? ");
        }
        if (latestArrival != null) {
            query.append("AND f.arrival_time <= ? ");
        }

        query.append(
            "ORDER BY " +
            "CASE WHEN fi.flight_date = ? THEN 0 ELSE 1 END, " +
            "ABS(DATEDIFF(fi.flight_date, ?)), " +
            "fi.flight_date, " +
            "f.base_price"
        );

        PreparedStatement ps = con.prepareStatement(query.toString());

        int i = 1;
        ps.setString(i++, fromInput.toUpperCase());
        ps.setString(i++, "%" + fromInput + "%");
        ps.setString(i++, toInput.toUpperCase());
        ps.setString(i++, "%" + toInput + "%");
        ps.setDate(i++, java.sql.Date.valueOf(startDate));
        ps.setDate(i++, java.sql.Date.valueOf(endDate));

        if (maxPrice != null) {
            ps.setDouble(i++, maxPrice);
        }
        if (!airlineFilter.isEmpty()) {
            ps.setString(i++, "%" + airlineFilter + "%");
        }
        if (earliestDepart != null) {
            ps.setTime(i++, Time.valueOf(earliestDepart));
        }
        if (latestDepart != null) {
            ps.setTime(i++, Time.valueOf(latestDepart));
        }
        if (earliestArrival != null) {
            ps.setTime(i++, Time.valueOf(earliestArrival));
        }
        if (latestArrival != null) {
            ps.setTime(i++, Time.valueOf(latestArrival));
        }

        ps.setDate(i++, java.sql.Date.valueOf(targetDate));
        ps.setDate(i++, java.sql.Date.valueOf(targetDate));

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int seats = rs.getInt("seats_available");
            String status = seats > 0 ? "Available" : "Full (Waitlist)";

            tableModel.addRow(new Object[]{
                rs.getInt("instance_id"),
                rs.getString("flight_number"),
                rs.getString("airline"),
                rs.getString("d_apid"),
                rs.getString("a_apid"),
                rs.getDate("flight_date"),
                rs.getTime("depart_time"),
                rs.getTime("arrival_time"),
                rs.getString("duration"),
                rs.getDouble("base_price"),
                status
            });
        }

        rs.close();
        ps.close();
    }
}