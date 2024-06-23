package theo32machine;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RamInfoTable {

    private static JTable table;
    private static DefaultTableModel tableModel;
    private static byte[] ramData = new byte[(int)Math.pow(2,15)]; // 32KB of RAM data

    public static void createAndShowGUI() {
        // Initialize the frame
        JFrame frame = new JFrame("RAM Information");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        // Create the table model with 65536 rows and 17 columns
        String[] columnNames = new String[17];
        columnNames[0] = "Row";
        for (int i = 1; i <= 16; i++) {
            columnNames[i] = Integer.toHexString(i - 1).toUpperCase();
        }

        // Initialize table model with empty data
        tableModel = new DefaultTableModel(columnNames,(int)Math.pow(2,15)/16); // Increase row count to 65536
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Populate initial data
        populateInitialData();

        // Show the frame
        frame.setVisible(true);
    }

    private static void populateInitialData() {
        for (int row = 0; row < (int)Math.pow(2,15)/16; row++) { // Change loop limit to 65536
            // Set the first column to the hexadecimal representation of the row number
            tableModel.setValueAt(String.format("%04X", row).toUpperCase(), row, 0); // Use String.format to ensure 4 digits
            for (int col = 0; col < 16; col++) {
                tableModel.setValueAt(String.format("%02X", ramData[row * 16 + col] & 0xFF).toUpperCase(), row, col + 1);
            }
        }
    }

    public static void updateTableData(byte[] newData) {
        if (tableModel == null) return;
        if (newData.length != ramData.length) {
            System.err.println("Error: Size of newData array must match the size of ramData array.");
            return;
        }

        ramData = newData.clone();

        for (int row = 0; row < (int)Math.pow(2,15)/16; row++) {
            // Update the first column with the hexadecimal representation of the row number
            tableModel.setValueAt(Integer.toHexString(row).toUpperCase(), row, 0);
            for (int col = 0; col < 16; col++) {
                tableModel.setValueAt(Integer.toHexString(ramData[row * 16 + col] & 0xFF).toUpperCase(), row, col + 1);
            }
        }
    }
}
