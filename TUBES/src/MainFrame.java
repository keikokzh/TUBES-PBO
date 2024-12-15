import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainFrame extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtName, txtDate, txtId, txtSearch;
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

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
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
            DatabaseHelper.addBirthday(name, date);
            loadTableData();
        });

        // Tombol Update
        btnUpdate.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                String date = txtDate.getText();
                DatabaseHelper.updateBirthday(id, name, date);
                loadTableData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Belum ada data yang dipilih bro", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Tombol Hapus
        btnDelete.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                DatabaseHelper.deleteBirthday(id);
                loadTableData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Belum ada data yang dipilih bro", "Error", JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}