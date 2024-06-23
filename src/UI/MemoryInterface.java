package UI;

import theo32machine.THEO32;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class MemoryInterface extends JFrame {
    private JTable Memory;
    private static DefaultTableModel memTableModel;
    private JPanel Interface;
    THEO32 theo32;
    private static byte[] ramData = new byte[(int)Math.pow(2,15)]; // 32KB of RAM data
    public MemoryInterface(THEO32 theo32) {
        this.theo32 = theo32;

        setTitle("Interface");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000,700);
        setLocationRelativeTo(null);

        Interface.setBackground(new Color(25,25,25));
        setContentPane(Interface);

        // Populate initial data
        String[] columnNames = new String[17];
        columnNames[0] = "Row";
        for (int i = 1; i <= 16; i++) {
            columnNames[i] = Integer.toHexString(i - 1).toUpperCase();
        }

        // Initialize table model with empty data
        memTableModel = new DefaultTableModel(columnNames,(int)Math.pow(2,15)/16); // Increase row count to 65536

        populateInitialData();

        Memory.setModel(memTableModel);
        Memory.setBackground(new Color(25,25,25));
        Memory.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer());


        setVisible(true);

    }
    private void populateInitialData() {
        for (int row = 0; row < (int)Math.pow(2,15)/16; row++) { // Change loop limit to 65536
            // Set the first column to the hexadecimal representation of the row number
            memTableModel.setValueAt(String.format("%04X", row).toUpperCase(), row, 0); // Use String.format to ensure 4 digits
            for (int col = 0; col < 16; col++) {
                memTableModel.setValueAt(String.format("%02X", theo32.RAM[row * 16 + col] & 0xFF).toUpperCase(), row, col + 1);
            }
        }
    }
    public static void updateTableData(byte[] newData) {
        if (newData.length != ramData.length) {
            System.err.println("Error: Size of newData array must match the size of ramData array.");
            return;
        }

        ramData = newData.clone();

        for (int row = 0; row < (int)Math.pow(2,15)/16; row++) {
            // Update the first column with the hexadecimal representation of the row number
            memTableModel.setValueAt(Integer.toHexString(row).toUpperCase(), row, 0);
            for (int col = 0; col < 16; col++) {
                memTableModel.setValueAt(Integer.toHexString(ramData[row * 16 + col] & 0xFF).toUpperCase(), row, col + 1);
            }
        }
    }
    private class CellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            //Get the status for the current row.
            TableModel tableModel = (TableModel) table.getModel();

            l.setBackground(Color.RED);
            l.setFocusable(false);

            //Return the JLabel which renders the cell.
            return l;

        }
    }
}
