package UI.TMS9918;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Display implements Runnable {
    TMS9918 tms;
    JFrame frame = new JFrame("TMS9918 Emulator");

    public Display(TMS9918 tms) {
        this.tms = tms;
        TMS9918Panel panel = new TMS9918Panel(tms);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(tms.TMS9918_PIXELS_X *4, tms.TMS9918_PIXELS_Y*4); // 2x scaling for 256x192 resolution
        frame.add(panel);
        frame.setVisible(true);
    }
    public void run() {
        JFrame frame = new JFrame("TMS9918 Emulator");
        TMS9918Panel panel = new TMS9918Panel(tms);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(tms.TMS9918_PIXELS_X *4, tms.TMS9918_PIXELS_Y*4); // 2x scaling for 256x192 resolution
        frame.add(panel);
        frame.setVisible(true);

        new Timer(16, e -> {
            panel.render();
            panel.repaint();
        }).start();
    }
}

class TMS9918Panel extends JPanel {

    private TMS9918 tms9918;
    private BufferedImage image;

    public TMS9918Panel(TMS9918 tms9918) {
        this.tms9918 = tms9918;
        image = new BufferedImage(tms9918.TMS9918_PIXELS_X, tms9918.TMS9918_PIXELS_Y, BufferedImage.TYPE_INT_RGB);
    }

    public void render() {
        byte[] pixels = new byte[tms9918.TMS9918_PIXELS_X];

        for (int y = 0; y < tms9918.TMS9918_PIXELS_Y; y++) {
            Arrays.fill(pixels, (byte) 0);
            tms9918.TMS9918ScanLine(y,pixels);
            for (int x = 0; x < tms9918.TMS9918_PIXELS_X; x++) {

                image.setRGB(x, y, tmsColorToRGB(pixels[x]));
            }
        }
    }
    private int tmsColorToRGB(byte color) {
        switch (color) {
            case 0: return Color.BLACK.getRGB();        // Transparent / Black
            case 1: return Color.BLACK.getRGB();        // Black
            case 2: return new Color(0, 180, 0).getRGB(); // Medium Green
            case 3: return new Color(60, 255, 60).getRGB(); // Light Green
            case 4: return new Color(0, 0, 200).getRGB(); // Dark Blue
            case 5: return new Color(60, 180, 255).getRGB(); // Light Blue
            case 6: return new Color(180, 0, 0).getRGB(); // Dark Red
            case 7: return Color.CYAN.getRGB();         // Cyan
            case 8: return new Color(255, 60, 60).getRGB(); // Medium Red
            case 9: return new Color(255, 180, 180).getRGB(); // Light Red
            case 10: return new Color(180, 180, 0).getRGB(); // Dark Yellow
            case 11: return Color.YELLOW.getRGB();      // Light Yellow
            case 12: return new Color(0, 130, 0).getRGB(); // Dark Green
            case 13: return Color.MAGENTA.getRGB();     // Magenta
            case 14: return Color.GRAY.getRGB();        // Grey
            case 15: return Color.WHITE.getRGB();       // White
            default: return Color.BLACK.getRGB();       // Fallback
        }
    }

    // Override the paintComponent to draw the image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the buffered image scaled to fit the panel
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }
}

