package EXPERIMENT_NO_13;

import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginDemo extends JFrame implements ActionListener {
    private JButton registerBtn, loginBtn;
    private JLabel userLabel, passLabel;
    private JTextField userField;
    private JPasswordField passField;
    
    public LoginDemo() {
        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        
        // Initialize components
        userLabel = new JLabel("Username:");
        passLabel = new JLabel("Password:");
        userField = new JTextField(15);
        passField = new JPasswordField(15);
        registerBtn = new JButton("Register");
        loginBtn = new JButton("Login");
        
        // Add action listeners
        registerBtn.addActionListener(this);
        loginBtn.addActionListener(this);
        
        // Set layout
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(registerBtn);
        panel.add(loginBtn);
        
        add(panel);
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/cse?useSSL=false", 
                    "root", "root")) {
                
                if (e.getSource() == registerBtn) {
                    registerUser(con, username, password);
                } else if (e.getSource() == loginBtn) {
                    loginUser(con, username, password);
                }
            }
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "MySQL JDBC Driver not found", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registerUser(Connection con, String username, String password) throws SQLException {
        String checkSql = "SELECT username FROM logint WHERE username = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        String insertSql = "INSERT INTO logint (username, password) VALUES (?, ?)";
        try (PreparedStatement insertStmt = con.prepareStatement(insertSql)) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Registration successful", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void loginUser(Connection con, String username, String password) throws SQLException {
        String sql = "SELECT username FROM logint WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginDemo());
    }
}