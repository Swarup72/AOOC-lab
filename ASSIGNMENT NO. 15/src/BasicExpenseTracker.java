import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Expense {
    String description, date, category;
    double amount;

    public Expense(String description, double amount, String date, String category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public String toString() {
        return description + " | ₹" + amount + " | " + date + " | " + category;
    }
}

public class BasicExpenseTracker extends JFrame implements ActionListener {
    JPanel sidebar, dashboardPanel, addPanel, viewPanel;
    JButton btnDashboard, btnAdd, btnView;
    JTextField incomeField, descField, amountField, dateField;
    JComboBox<String> categoryBox;
    JTextArea dashboardText, transactionsText;
    ArrayList<Expense> expenses = new ArrayList<>();
    double income = 0;
    JList<String> expenseList;
    DefaultListModel<String> listModel;
    int selectedIndex = -1;

    public BasicExpenseTracker() {
        setTitle("Expense Tracker");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Sidebar
        sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setBackground(Color.DARK_GRAY);
        sidebar.setBounds(0, 0, 180, 500);

        JLabel title = new JLabel("Expense Tracker");
        title.setForeground(Color.WHITE);
        title.setBounds(20, 20, 150, 30);
        sidebar.add(title);

        btnDashboard = new JButton("Dashboard");
        btnAdd = new JButton("Add/Edit Expense");
        btnView = new JButton("View All");

        btnDashboard.setBounds(20, 80, 140, 30);
        btnAdd.setBounds(20, 120, 140, 30);
        btnView.setBounds(20, 160, 140, 30);

        btnDashboard.addActionListener(this);
        btnAdd.addActionListener(this);
        btnView.addActionListener(this);

        sidebar.add(btnDashboard);
        sidebar.add(btnAdd);
        sidebar.add(btnView);
        add(sidebar);

        // Dashboard
        dashboardPanel = new JPanel(null);
        dashboardPanel.setBounds(180, 0, 620, 500);

        JLabel incomeLabel = new JLabel("Monthly Income:");
        incomeLabel.setBounds(20, 20, 120, 25);
        dashboardPanel.add(incomeLabel);

        incomeField = new JTextField();
        incomeField.setBounds(140, 20, 120, 25);
        dashboardPanel.add(incomeField);

        JButton setIncomeBtn = new JButton("Set Income");
        setIncomeBtn.setBounds(270, 20, 120, 25);
        setIncomeBtn.addActionListener(e -> {
            try {
                income = Double.parseDouble(incomeField.getText());
                updateDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid income");
            }
        });
        dashboardPanel.add(setIncomeBtn);

        dashboardText = new JTextArea();
        dashboardText.setBounds(20, 60, 550, 380);
        dashboardText.setEditable(false);
        dashboardPanel.add(dashboardText);

        // Add/Edit Expense Panel
        addPanel = new JPanel(null);
        addPanel.setBounds(180, 0, 620, 500);

        JLabel descLabel = new JLabel("Description:");
        JLabel amountLabel = new JLabel("Amount:");
        JLabel dateLabel = new JLabel("Date (DD/MM/YYYY):");
        JLabel categoryLabel = new JLabel("Category:");

        descLabel.setBounds(30, 30, 120, 25);
        amountLabel.setBounds(30, 70, 120, 25);
        dateLabel.setBounds(30, 110, 150, 25);
        categoryLabel.setBounds(30, 150, 120, 25);

        descField = new JTextField();
        amountField = new JTextField();
        dateField = new JTextField();
        categoryBox = new JComboBox<>(new String[]{"Food", "Transport", "Entertainment", "Others"});

        descField.setBounds(180, 30, 200, 25);
        amountField.setBounds(180, 70, 200, 25);
        dateField.setBounds(180, 110, 200, 25);
        categoryBox.setBounds(180, 150, 200, 25);

        JButton addBtn = new JButton("Save Expense");
        addBtn.setBounds(180, 200, 200, 30);
        addBtn.addActionListener(e -> {
            try {
                String desc = descField.getText();
                double amt = Double.parseDouble(amountField.getText());
                String date = dateField.getText();
                String cat = (String) categoryBox.getSelectedItem();
                if (selectedIndex >= 0) {
                    expenses.set(selectedIndex, new Expense(desc, amt, date, cat));
                    selectedIndex = -1;
                } else {
                    expenses.add(new Expense(desc, amt, date, cat));
                }
                JOptionPane.showMessageDialog(this, "Expense saved!");
                clearForm();
                updateDashboard();
                updateExpenseList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data!");
            }
        });

        listModel = new DefaultListModel<>();
        expenseList = new JList<>(listModel);
        expenseList.setBounds(400, 30, 200, 300);
        expenseList.addListSelectionListener(e -> {
            selectedIndex = expenseList.getSelectedIndex();
            if (selectedIndex >= 0) {
                Expense exp = expenses.get(selectedIndex);
                descField.setText(exp.description);
                amountField.setText(String.valueOf(exp.amount));
                dateField.setText(exp.date);
                categoryBox.setSelectedItem(exp.category);
            }
        });

        addPanel.add(descLabel);
        addPanel.add(amountLabel);
        addPanel.add(dateLabel);
        addPanel.add(categoryLabel);
        addPanel.add(descField);
        addPanel.add(amountField);
        addPanel.add(dateField);
        addPanel.add(categoryBox);
        addPanel.add(addBtn);
        addPanel.add(expenseList);

        // View Panel
        viewPanel = new JPanel(null);
        viewPanel.setBounds(180, 0, 620, 500);

        transactionsText = new JTextArea("No transactions yet");
        transactionsText.setEditable(false);
        transactionsText.setBounds(20, 20, 570, 400);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(250, 430, 100, 25);
        refreshBtn.addActionListener(e -> updateTransactions());

        viewPanel.add(transactionsText);
        viewPanel.add(refreshBtn);

        add(dashboardPanel);
        add(addPanel);
        add(viewPanel);

        showPanel(dashboardPanel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnDashboard) {
            showPanel(dashboardPanel);
        } else if (e.getSource() == btnAdd) {
            showPanel(addPanel);
            updateExpenseList();
        } else if (e.getSource() == btnView) {
            updateTransactions();
            showPanel(viewPanel);
        }
    }

    void showPanel(JPanel panel) {
        dashboardPanel.setVisible(false);
        addPanel.setVisible(false);
        viewPanel.setVisible(false);
        panel.setVisible(true);
    }

    void updateDashboard() {
        Map<String, Double> monthTotals = new TreeMap<>();
        double totalExpense = 0;
        for (Expense exp : expenses) {
            String[] parts = exp.date.split("/");
            if (parts.length == 3) {
                String monthYear = parts[1] + "/" + parts[2];
                monthTotals.put(monthYear, monthTotals.getOrDefault(monthYear, 0.0) + exp.amount);
            }
            totalExpense += exp.amount;
        }

        StringBuilder sb = new StringBuilder("=== DASHBOARD ===\n");
        sb.append("Total Income: ₹").append(income).append("\n");
        sb.append("Total Expenses: ₹").append(totalExpense).append("\n");
        sb.append("Balance: ₹").append(income - totalExpense).append("\n\n");
        sb.append("Month-wise Expenses:\n");
        for (String month : monthTotals.keySet()) {
            sb.append(month).append(" : ₹").append(monthTotals.get(month)).append("\n");
        }

        dashboardText.setText(sb.toString());
    }

    void updateTransactions() {
        if (expenses.isEmpty()) {
            transactionsText.setText("No transactions yet");
            return;
        }
        StringBuilder all = new StringBuilder("=== TRANSACTION HISTORY ===\n");
        for (Expense e : expenses) {
            all.append(e).append("\n");
        }
        transactionsText.setText(all.toString());
    }

    void updateExpenseList() {
        listModel.clear();
        for (Expense e : expenses) {
            listModel.addElement(e.toString());
        }
    }

    void clearForm() {
        descField.setText("");
        amountField.setText("");
        dateField.setText("");
        categoryBox.setSelectedIndex(0);
        selectedIndex = -1;
        expenseList.clearSelection();
    }

    public static void main(String[] args) {
        new BasicExpenseTracker();
    }
}