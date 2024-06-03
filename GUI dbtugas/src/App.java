import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class App extends JFrame {
    private JTextField namaField, nimField;
    private JTextArea outputArea;
    private Connection connection;
    private JLabel instructionLabel;

    public App() {
        super("Database Mahasiswa");

        // Create the connection
        String url = "jdbc:mariadb://localhost:3306/data_mahasiswa";
        String username = "root";
        String password = "";

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Koneksi Database Error :" + e.getMessage());
            System.exit(1);
        }

        setLayout(new BorderLayout());

     
        namaField = new JTextField(20);
        nimField = new JTextField(20);
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        instructionLabel = new JLabel("Isi list BOX Nama dan NIM sebelum mengklik CRUD nya!");


        JButton tambahButton = new JButton("Tambah data mahasiswa");
        JButton hapusButton = new JButton("Hapus data mahasiswa");
        JButton tampilkanButton = new JButton("Tampilkan data mahasiswa");
        JButton editButton = new JButton("Edit data mahasiswa");
        JButton clearButton = new JButton("Clear");
        JButton keluarButton = new JButton("Keluar");

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Nama Mahasiswa:"));
        inputPanel.add(namaField);
        inputPanel.add(new JLabel("NIM Mahasiswa:"));
        inputPanel.add(nimField);
        inputPanel.add(instructionLabel);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6));
        buttonPanel.add(tambahButton);
        buttonPanel.add(hapusButton);
        buttonPanel.add(tampilkanButton);
        buttonPanel.add(editButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(keluarButton);

        add(buttonPanel, BorderLayout.SOUTH);

 
        tambahButton.addActionListener(e -> tambahData());
        hapusButton.addActionListener(e -> hapusData());
        tampilkanButton.addActionListener(e -> tampilkanData());
        editButton.addActionListener(e -> editData());
        clearButton.addActionListener(e -> clearOutputArea());
        keluarButton.addActionListener(e -> exitProgram());

 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); 
        setVisible(true);
    }

    private void tambahData() {
        String newNama = namaField.getText();
        String newNIM = nimField.getText();

        String insertQuery = "INSERT INTO `mahasiswainfo` (nama, NIM) VALUES (?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setString(1, newNama);
            insertStatement.setString(2, newNIM);
            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                outputArea.append("Data baru berhasil ditambahkan ke database!\n");
                outputArea.append("NIM Mahasiswa: " + newNIM + "\n");
            } else {
                outputArea.append("Gagal menambahkan data baru ke database.\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void hapusData() {
        String nimToDelete = nimField.getText();
        String deleteQuery = "DELETE FROM `mahasiswainfo` WHERE NIM = ?";
        try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setString(1, nimToDelete);
            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected > 0) {
                outputArea.append("Data mahasiswa berhasil dihapus dari database!\n");
            } else {
                outputArea.append("Gagal menghapus data mahasiswa dari database.\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void tampilkanData() {
        outputArea.setText(""); 
        try {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM `mahasiswainfo` ");

            while (result.next()) {
                String nama = result.getString("nama");
                String nim = result.getString("NIM");

                outputArea.append("Nama Mahasiswa: " + nama + "\n");
                outputArea.append("NIM: " + nim + "\n");
                outputArea.append("========================================\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void editData() {
        String nimToEdit = nimField.getText();

        String checkQuery = "SELECT * FROM `mahasiswainfo` WHERE NIM = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
            checkStatement.setString(1, nimToEdit);
            ResultSet checkResult = checkStatement.executeQuery();

            if (checkResult.next()) {
                String editedNama = namaField.getText();
                String editedNIM = nimField.getText();

                String updateQuery = "UPDATE `mahasiswainfo` SET nama = ?, NIM = ? WHERE NIM = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setString(1, editedNama);
                    updateStatement.setString(2, editedNIM);
                    updateStatement.setString(3, nimToEdit);
                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        outputArea.append("Data mahasiswa berhasil diedit di database!\n");
                    } else {
                        outputArea.append("Gagal mengedit data mahasiswa di database.\n");
                    }
                }
            } else {
                outputArea.append("Data mahasiswa dengan NIM tersebut tidak ditemukan.\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearOutputArea() {
        outputArea.setText("");
    }

    private void exitProgram() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}
