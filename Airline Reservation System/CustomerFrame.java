import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class CustomerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private Connection con;
    private String customerID = "C001";

    public CustomerFrame(Connection con) {
        this.con = con;
        setTitle("Customer Menu");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initialize();
    }

    public void initialize() {
        JLabel title = new JLabel("Customer Menu");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JButton searchBtn = new JButton("Search Flights");
        JButton myTicketsBtn = new JButton("My Tickets");
        JButton questionBtn = new JButton("Ask Representative");
        JButton qnaBtn = new JButton("Q & A");

        searchBtn.addActionListener(e -> new FlightSearchFrame(con, customerID));
        myTicketsBtn.addActionListener(e -> openMyTickets());
        questionBtn.addActionListener(e -> askQuestion());
        qnaBtn.addActionListener(e -> openQnA());

        JPanel panel = new JPanel(new GridLayout(4, 1, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.add(searchBtn);
        panel.add(myTicketsBtn);
        panel.add(questionBtn);
        panel.add(qnaBtn);

        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    // =========================================================
    // MY TICKETS
    // =========================================================

    private void openMyTickets() {
        JFrame frame = new JFrame("My Tickets");
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Current Tickets", createTicketsPanel(false));
        tabs.addTab("Past Tickets", createTicketsPanel(true));

        frame.add(tabs);
        frame.setVisible(true);
    }

    private JPanel createTicketsPanel(boolean past) {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel() {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;
                    int modelRow = table.convertRowIndexToModel(row);
                    String ticketId = model.getValueAt(modelRow, 0).toString();
                    openTicketDetails(ticketId);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton cancelBtn = new JButton("Cancel Selected Ticket");
        if (!past) {
            panel.add(cancelBtn, BorderLayout.SOUTH);
        }

        try {
            String sql =
                "SELECT " +
                "t.tid AS ticket_id, " +
                "t.ticket_type, " +
                "t.status, " +
                "t.total_fare, " +
                "t.booking_fee, " +
                "MIN(fi.flight_date) AS departure_date, " +
                "MAX(fi.flight_date) AS return_date, " +
                "MIN(CASE WHEN tf.segment_order = 1 THEN f.d_apid END) AS from_airport, " +
                "MIN(CASE WHEN tf.segment_order = 1 THEN f.a_apid END) AS to_airport, " +
                "MIN(CASE WHEN tf.segment_order = 1 THEN f.depart_time END) AS depart_time, " +
                "MIN(CASE WHEN tf.segment_order = 2 THEN f.depart_time END) AS return_time, " +
                "GROUP_CONCAT(DISTINCT tf.seat_class) AS seat_classes " +
                "FROM Tickets t " +
                "JOIN Ticket_Flights tf ON t.tid = tf.tid " +
                "JOIN Flight_Instances fi ON tf.instance_id = fi.instance_id " +
                "JOIN Flights f ON fi.fid = f.fid AND fi.aid = f.aid " +
                "WHERE t.cid = ? " +
                "AND t.status <> 'canceled' " +
                "AND fi.flight_date " + (past ? "< CURDATE() " : ">= CURDATE() ") +
                "GROUP BY t.tid, t.ticket_type, t.status, t.total_fare, t.booking_fee " +
                "ORDER BY departure_date, depart_time";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, customerID);
            ResultSet rs = ps.executeQuery();

            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                model.addColumn(meta.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columns];
                for (int i = 1; i <= columns; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            rs.close();
            ps.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            ex.printStackTrace();
        }

        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a ticket.");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String ticketId = model.getValueAt(modelRow, 0).toString();
            cancelTicket(ticketId);
            SwingUtilities.getWindowAncestor(panel).dispose();
            openMyTickets();
        });

        return panel;
    }

    // =========================================================
    // TICKET DETAILS
    // =========================================================

    private void openTicketDetails(String ticketId) {
        JFrame frame = new JFrame("Ticket Details");
        frame.setSize(750, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JButton deleteBtn = new JButton("Cancel Reservation");

        try {
            String sql =
                "SELECT " +
                "t.tid, t.ticket_type, t.status, t.total_fare, t.booking_fee, t.purchase_time, " +
                "tf.seat_class, tf.seat_number, tf.meal, tf.segment_order, " +
                "fi.instance_id, fi.flight_date, fi.seats_available, " +
                "f.flight_number, f.d_apid, f.a_apid, f.depart_time, f.arrival_time, f.flight_type, f.base_price " +
                "FROM Tickets t " +
                "JOIN Ticket_Flights tf ON t.tid = tf.tid " +
                "JOIN Flight_Instances fi ON tf.instance_id = fi.instance_id " +
                "JOIN Flights f ON fi.fid = f.fid AND fi.aid = f.aid " +
                "WHERE t.tid = ? AND t.cid = ? " +
                "ORDER BY tf.segment_order";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ticketId);
            ps.setString(2, customerID);
            ResultSet rs = ps.executeQuery();

            StringBuilder sb = new StringBuilder();
            boolean first = true;

            while (rs.next()) {
                if (first) {
                    sb.append("TICKET DETAILS\n");
                    sb.append("-----------------------------\n");
                    sb.append("Ticket ID:     ").append(rs.getString("tid")).append("\n");
                    sb.append("Type:          ").append(rs.getString("ticket_type")).append("\n");
                    sb.append("Status:        ").append(rs.getString("status")).append("\n");
                    sb.append("Total Fare:    $").append(rs.getDouble("total_fare")).append("\n");
                    sb.append("Booking Fee:   $").append(rs.getDouble("booking_fee")).append("\n");
                    sb.append("Purchase Time: ").append(rs.getTimestamp("purchase_time")).append("\n\n");
                    first = false;
                }

                sb.append("FLIGHT SEGMENT ").append(rs.getInt("segment_order")).append("\n");
                sb.append("-----------------------------\n");
                sb.append("Instance ID:   ").append(rs.getInt("instance_id")).append("\n");
                sb.append("Flight Number: ").append(rs.getString("flight_number")).append("\n");
                sb.append("Route:         ").append(rs.getString("d_apid"))
                        .append(" → ").append(rs.getString("a_apid")).append("\n");
                sb.append("Date:          ").append(rs.getDate("flight_date")).append("\n");
                sb.append("Depart:        ").append(rs.getTime("depart_time")).append("\n");
                sb.append("Arrive:        ").append(rs.getTime("arrival_time")).append("\n");
                sb.append("Flight Type:   ").append(rs.getString("flight_type")).append("\n");
                sb.append("Seat Class:    ").append(rs.getString("seat_class")).append("\n");
                sb.append("Seat Number:   ").append(rs.getString("seat_number")).append("\n");
                sb.append("Meal:          ").append(rs.getString("meal")).append("\n");
                sb.append("Base Price:    $").append(rs.getDouble("base_price")).append("\n\n");
            }

            area.setText(sb.toString());
            rs.close();
            ps.close();

        } catch (Exception ex) {
            area.setText("Error loading ticket details:\n" + ex.getMessage());
            ex.printStackTrace();
        }

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to cancel this reservation?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;
            cancelTicket(ticketId);
            frame.dispose();
        });

        frame.add(new JScrollPane(area), BorderLayout.CENTER);
        frame.add(deleteBtn, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // =========================================================
    // CANCEL TICKET
    // =========================================================

    private void cancelTicket(String ticketId) {
        try {
            con.setAutoCommit(false);

            // check ticket exists
            String getStatus = "SELECT status FROM Tickets WHERE tid = ? AND cid = ?";
            PreparedStatement statusPs = con.prepareStatement(getStatus);
            statusPs.setString(1, ticketId);
            statusPs.setString(2, customerID);
            ResultSet statusRs = statusPs.executeQuery();

            if (!statusRs.next()) {
                JOptionPane.showMessageDialog(this, "Ticket not found.");
                con.rollback();
                return;
            }

            String status = statusRs.getString("status");
            statusRs.close();
            statusPs.close();

            if (status.equals("canceled")) {
                JOptionPane.showMessageDialog(this, "Ticket is already canceled.");
                con.rollback();
                return;
            }

            // check seat class
            String classCheck =
                "SELECT tf.seat_class FROM Ticket_Flights tf WHERE tf.tid = ? LIMIT 1";
            PreparedStatement classPs = con.prepareStatement(classCheck);
            classPs.setString(1, ticketId);
            ResultSet classRs = classPs.executeQuery();

            if (classRs.next()) {
                String seatClass = classRs.getString("seat_class");
                if (seatClass.equals("economy")) {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Economy tickets have a cancellation fee of $50. Proceed?",
                        "Cancellation Fee",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm != JOptionPane.YES_OPTION) {
                        con.rollback();
                        return;
                    }
                }
            }

            classRs.close();
            classPs.close();

            // give seats back
            if (status.equals("booked")) {
                String giveSeatsBack =
                    "UPDATE Flight_Instances fi " +
                    "JOIN Ticket_Flights tf ON fi.instance_id = tf.instance_id " +
                    "SET fi.seats_available = fi.seats_available + 1 " +
                    "WHERE tf.tid = ?";
                PreparedStatement seatPs = con.prepareStatement(giveSeatsBack);
                seatPs.setString(1, ticketId);
                seatPs.executeUpdate();
                seatPs.close();
            }

            // update status
            String update = "UPDATE Tickets SET status = 'canceled' WHERE tid = ? AND cid = ?";
            PreparedStatement ps = con.prepareStatement(update);
            ps.setString(1, ticketId);
            ps.setString(2, customerID);
            int rows = ps.executeUpdate();
            ps.close();

            con.commit();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Ticket canceled successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Ticket not found.");
            }

        } catch (Exception ex) {
            try { con.rollback(); } catch (Exception ignored) {}
            JOptionPane.showMessageDialog(this, ex.getMessage());
            ex.printStackTrace();
        } finally {
            try { con.setAutoCommit(true); } catch (Exception ignored) {}
        }
    }

    // =========================================================
    // JOIN WAITLIST
    // =========================================================

    private void openJoinWaitlist() {
        JFrame frame = new JFrame("Join Waitlist");
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Enter Instance ID:");
        JTextField instanceField = new JTextField();
        JButton joinBtn = new JButton("Join Waitlist");

        inputPanel.add(label);
        inputPanel.add(instanceField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(joinBtn);

        frame.add(inputPanel, BorderLayout.CENTER);

        joinBtn.addActionListener(e -> {
            String instanceIdStr = instanceField.getText().trim();
            if (instanceIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter an instance ID.");
                return;
            }

            int instanceId;
            try {
                instanceId = Integer.parseInt(instanceIdStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid instance ID.");
                return;
            }

            try {
                // check if already on waitlist
                String checkSql =
                    "SELECT waitlist_id FROM Waiting_List WHERE cid = ? AND instance_id = ?";
                PreparedStatement checkPs = con.prepareStatement(checkSql);
                checkPs.setString(1, customerID);
                checkPs.setInt(2, instanceId);
                ResultSet checkRs = checkPs.executeQuery();

                if (checkRs.next()) {
                    JOptionPane.showMessageDialog(frame, "You are already on the waitlist for this flight.");
                    checkRs.close();
                    checkPs.close();
                    return;
                }
                checkRs.close();
                checkPs.close();

                // insert into waitlist
                String insertSql =
                    "INSERT INTO Waiting_List (cid, instance_id, request_time) VALUES (?, ?, NOW())";
                PreparedStatement insertPs = con.prepareStatement(insertSql);
                insertPs.setString(1, customerID);
                insertPs.setInt(2, instanceId);
                insertPs.executeUpdate();
                insertPs.close();

                JOptionPane.showMessageDialog(frame, "You have been added to the waitlist.");
                frame.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error joining waitlist: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        frame.setVisible(true);
    }

    // =========================================================
    // Q & A
    // =========================================================

    private void openQnA() {
        JFrame frame = new JFrame("Customer Q & A");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Arial", Font.PLAIN, 16));
        area.setText(
            "Frequently Asked Questions\n\n"
            + "Q: How do I reserve a flight?\n"
            + "A: Use Search Flights to find and book a flight.\n\n"
            + "Q: How do I join a waitlist?\n"
            + "A: Click Join Waitlist and enter the instance ID.\n\n"
            + "Q: Can I cancel economy tickets?\n"
            + "A: Yes, but a $50 cancellation fee applies.\n\n"
            + "Q: Can I cancel business or first class tickets?\n"
            + "A: Yes, with no cancellation fee.\n\n"
            + "Q: How do I search flights?\n"
            + "A: Use the Search Flights button.\n\n"
            + "Q: How do I contact a rep?\n"
            + "A: Use the Ask Representative button."
        );

        frame.add(new JScrollPane(area));
        frame.setVisible(true);
    }

    // =========================================================
    // ASK QUESTION
    // =========================================================

    private void askQuestion() {

        String question = JOptionPane.showInputDialog(
                this,
                "Enter Your Question:"
        );

        if (question == null || question.trim().isEmpty()) return;

        Question.questions.add(
            new Question(customerID, question.trim())
        );

        JOptionPane.showMessageDialog(
                this,
                "Your question has been submitted.\n" +
                "A representative will answer it shortly."
        );
    }
}