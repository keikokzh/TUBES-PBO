import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MainFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtDate, txtId, txtSearch, txtToday;
    private JComboBox<String> searchCriteria;

    public MainFrame() {
        setTitle("Aplikasi Pengingat Ulang Tahun");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Input
        JPanel panelInput = new JPanel(new GridBagLayout());
        panelInput.setBorder(BorderFactory.createTitledBorder("Form Input"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblId = new JLabel("ID (Otomatis saat klik tabel):");
        txtId = new JTextField();
        txtId.setEditable(false);

        JLabel lblName = new JLabel("Nama:");
        txtName = new JTextField();

        JLabel lblDate = new JLabel("Tanggal (YYYY-MM-DD):");
        txtDate = new JTextField();

        JLabel lblToday = new JLabel("Tanggal Hari Ini (Manual):");
        txtToday = new JTextField(LocalDate.now().toString()); // Default ke hari ini

        // Penempatan komponen
        gbc.gridx = 0; gbc.gridy = 0;
        panelInput.add(lblId, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        panelInput.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelInput.add(lblName, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panelInput.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelInput.add(lblDate, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        panelInput.add(txtDate, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelInput.add(lblToday, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        panelInput.add(txtToday, gbc);

        // Tombol CRUD
        JButton btnAdd = new JButton("Tambah");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Hapus");
        JButton btnRefresh = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panelInput.add(buttonPanel, gbc);

        add(panelInput, BorderLayout.NORTH);

        // Panel Search
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSearch = new JLabel("Cari Berdasarkan:");
        searchCriteria = new JComboBox<>(new String[]{"Nama", "Bulan"});
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Cari");
        panelSearch.add(lblSearch);
        panelSearch.add(searchCriteria);
        panelSearch.add(txtSearch);
        panelSearch.add(btnSearch);

        add(panelSearch, BorderLayout.SOUTH);

        // Tabel Data
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Tanggal"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Tambahkan MouseListener ke tabel
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtDate.setText(tableModel.getValueAt(selectedRow, 2).toString());
                }
            }
        });

        // Tombol Refresh
        btnRefresh.addActionListener(e -> loadTableData());

        // Tombol Tambah
        btnAdd.addActionListener(e -> {
            String name = txtName.getText();
            String date = txtDate.getText();
            if (isDateValid(date)) {
                DatabaseHelper.addBirthday(name, date);
                clearInputFields();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Format tanggal salah! Harap gunakan format YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tombol Update
        btnUpdate.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                String date = txtDate.getText();
                if (isDateValid(date)) {
                    DatabaseHelper.updateBirthday(id, name, date);
                    clearInputFields();
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Format tanggal salah! Harap gunakan format YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Belum ada data yang dipilih.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tombol Hapus
        btnDelete.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                DatabaseHelper.deleteBirthday(id);
                clearInputFields();
                loadTableData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Belum ada data yang dipilih.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tombol Cari
        btnSearch.addActionListener(e -> searchTable());

        loadTableData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<String[]> birthdays = DatabaseHelper.getAllBirthdays();
        for (String[] row : birthdays) {
            tableModel.addRow(row);
        }
        checkUpcomingBirthdays();
    }

    private void checkUpcomingBirthdays() {
        String todayInput = txtToday.getText();
        try {
            LocalDate today = LocalDate.parse(todayInput);
            List<String[]> birthdays = DatabaseHelper.getAllBirthdays();

            for (String[] row : birthdays) {
                String name = row[1];
                LocalDate birthday = LocalDate.parse(row[2]);
                LocalDate nextBirthday = birthday.withYear(today.getYear());

                if (nextBirthday.isBefore(today) || nextBirthday.equals(today)) {
                    nextBirthday = nextBirthday.plusYears(1);
                }

                long daysUntil = ChronoUnit.DAYS.between(today, nextBirthday);
                if (daysUntil <= 7) {
                    JOptionPane.showMessageDialog(this, 
                        "Heyy ulang tahun " + name + " tinggal " + daysUntil + " hari lagi loh bro!\nPada tanggal: " + nextBirthday,
                        "Pengingat Ulang Tahun", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Format tanggal salah bro! Gunakan format YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTable() {
        String keyword = txtSearch.getText();
        String criteria = (String) searchCriteria.getSelectedItem();
        tableModel.setRowCount(0);

        List<String[]> results;

        if (criteria.equals("Nama")) {
            results = DatabaseHelper.searchByName(keyword);
        } else { // Cari berdasarkan bulan
            results = DatabaseHelper.searchByMonth(keyword);
        }

        for (String[] row : results) {
            tableModel.addRow(row);
        }
    }

    private boolean isDateValid(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void clearInputFields() {
        txtId.setText("");
        txtName.setText("");
        txtDate.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
