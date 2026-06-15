import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainPage extends JFrame {

    final private Font mainFont = new Font("Lucida Sans", Font.BOLD, 18);
    public static Connection con = null;

    public void initialize() {
        JLabel label = new JLabel("Welcome to Travel Reservation System");
        label.setFont(mainFont);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnCustomer = new JButton("Customer");
        btnCustomer.setFont(mainFont);
        btnCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CustomerFrame(con);
            }
        });

        JButton btnRep = new JButton("Customer Representative");
        btnRep.setFont(mainFont);
        btnRep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RepFrame(con).initialize();
            }
        });

        JButton btnAdmin = new JButton("Admin");
        btnAdmin.setFont(mainFont);
        btnAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminFrame(con).initialize();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(btnCustomer);
        buttonPanel.add(btnRep);
        buttonPanel.add(btnAdmin);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(230, 140, 140));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainPanel.add(label, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        this.add(mainPanel);
        this.setTitle("Travel Reservation System");
        this.setSize(500, 300);
        this.setMinimumSize(new Dimension(300, 200));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/travel_reservation";
        String user = "root";
        String password = "Root08911*";
        try {
            con = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Unable to connect to database");
            e.printStackTrace();
            System.exit(0);
        }
        MainPage myFrame = new MainPage();
        myFrame.initialize();
    }
}