import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BookingReviewFrame extends JFrame {

    private Connection con;
    private String customerId;
    private int depInstanceId;
    private Integer retInstanceId;
    private BookingService bookingService;

    private JTextArea detailsArea;
    private JComboBox<String> classBox;
    private JSpinner quantitySpinner;

    private JButton buyBtn;
    private JButton reserveBtn;
    private JButton waitlistBtn;
    private JButton cancelBtn;

    public BookingReviewFrame(Connection con, String customerId,
                              int depInstanceId, Integer retInstanceId) {

        this.con = con;
        this.customerId = customerId;
        this.depInstanceId = depInstanceId;
        this.retInstanceId = retInstanceId;
        this.bookingService = new BookingService(con);

        setTitle("Review Booking");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        classBox = new JComboBox<>(new String[]{"economy", "business", "first"});
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(new JLabel("Seat Class:"));
        topPanel.add(classBox);

        topPanel.add(new JLabel("Number of Tickets:"));
        topPanel.add(quantitySpinner);

        buyBtn = new JButton("Buy Ticket(s)");
        reserveBtn = new JButton("Reserve Ticket(s)");
        waitlistBtn = new JButton("Join Waitlist");
        cancelBtn = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buyBtn);
        buttonPanel.add(reserveBtn);
        buttonPanel.add(waitlistBtn);
        buttonPanel.add(cancelBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadPreview();

        classBox.addActionListener(e -> loadPreview());
        quantitySpinner.addChangeListener(e -> loadPreview());

        buyBtn.addActionListener(e -> {
            try {
                String seatClass = (String) classBox.getSelectedItem();
                int quantity = (int) quantitySpinner.getValue();

                java.util.List<String> ticketIds =
                        bookingService.buyMultipleFlights(
                                customerId,
                                depInstanceId,
                                retInstanceId,
                                seatClass,
                                quantity
                        );

                JOptionPane.showMessageDialog(
                        this,
                        "Tickets bought successfully!\n\nTicket IDs:\n" +
                                String.join("\n", ticketIds)
                );

                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Booking failed:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });

        reserveBtn.addActionListener(e -> {
            try {
                String seatClass = (String) classBox.getSelectedItem();
                int quantity = (int) quantitySpinner.getValue();

                java.util.List<String> ticketIds =
                        bookingService.reserveMultipleFlights(
                                customerId,
                                depInstanceId,
                                retInstanceId,
                                seatClass,
                                quantity
                        );

                JOptionPane.showMessageDialog(
                        this,
                        "Tickets reserved successfully!\n\nTicket IDs:\n" +
                                String.join("\n", ticketIds)
                );

                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Reservation failed:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });

        waitlistBtn.addActionListener(e -> {
            try {

                JOptionPane.showMessageDialog(
                        this,
                        "Added to waitlist!"
                );

                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Waitlist failed:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadPreview() {
        try {
            String seatClass = (String) classBox.getSelectedItem();
            int quantity = (int) quantitySpinner.getValue();

            String details = bookingService.getBookingPreview(
                    customerId,
                    depInstanceId,
                    retInstanceId,
                    seatClass,
                    quantity
            );

            detailsArea.setText(details);

            boolean depEnough = bookingService.hasEnoughSeats(depInstanceId, quantity);
            boolean retEnough = retInstanceId == null ||
                    bookingService.hasEnoughSeats(retInstanceId, quantity);

            if (depEnough && retEnough) {
                buyBtn.setVisible(true);
                reserveBtn.setVisible(true);
                waitlistBtn.setVisible(false);
            } else {
                buyBtn.setVisible(false);
                reserveBtn.setVisible(false);
                waitlistBtn.setVisible(true);
            }

            cancelBtn.setVisible(true);

        } catch (Exception ex) {
            detailsArea.setText("Error loading booking details:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }
}