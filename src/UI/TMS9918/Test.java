package UI.TMS9918;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import UI.TMS9918.TMS9918;


public class Test {
    static TMS9918 tms = new TMS9918();
    static TMS9918_Helper TMSInterface = new TMS9918_Helper();

    static int[] xpos = new int[10];
    public static void loopRender() {
        boolean first = true;
        for (int y = 0; y < 10; y++) {
            int[] attrSprite = {
                    y*8,
                    xpos[y]++,
                    y,
                    0b00000010 + y
            };
            System.out.println(attrSprite[3]);
            if (xpos[y] > 100) {
                xpos[y] = 0;
            }
            if (first) {first = false; TMSInterface.writeBytesToVRAM(tms, tms.TMS_DEFAULT_VRAM_SPRITE_ATTR_ADDRESS, attrSprite); continue;}
            TMSInterface.writeBytesToVRAM(tms, attrSprite);
        }
    }
    public static void main(String[] args) {

        TMSInterface.initGraphicsModeII(tms);
//        int[] dataPattern = {0b00011000, 0b00111100, 0b01111110, 0b01000010, 0b01011010, 0b01011010, 0b01000010, 0b11111111,};
//        int[] colorPattern = {0x67, 0x67, 0x67, 0xEF, 0xEF, 0xEF, 0xEF, 0x20,};
//        int[] dataPattern = {0b00111000, 0b01111100, 0b11111110, 0b11111110, 0b11111110, 0b01111100, 0b00111000, 0b00000000,};
//        int[] colorPattern = {0x20, 0x30, 0x20, 0x30, 0x20, 0x30, 0x20, 0x30};
//        TMSInterface.writeBytesToVRAM(tms,tms.TMS_DEFAULT_VRAM_PATT_ADDRESS, dataPattern);
//        TMSInterface.writeBytesToVRAM(tms,tms.TMS_DEFAULT_VRAM_COLOR_ADDRESS, colorPattern);
//        TMSInterface.writeToVRAM(tms,tms.TMS_DEFAULT_VRAM_NAME_ADDRESS, 0);
        int[] dataSprite = {0b00011000, 0b00111100, 0b01111110, 0b01000010, 0b01011010, 0b01011010, 0b01000010, 0b11111111,};

        TMSInterface.writeBytesToVRAM(tms,tms.TMS_DEFAULT_VRAM_SPRITE_PATT_ADDRESS, dataSprite);
        TMSInterface.writeBytesToVRAM(tms, dataSprite);
        TMSInterface.writeBytesToVRAM(tms, dataSprite);
        TMSInterface.writeBytesToVRAM(tms, dataSprite);
        TMSInterface.writeBytesToVRAM(tms, dataSprite);
        TMSInterface.writeBytesToVRAM(tms, dataSprite);



        // Write characters to VRAM (For Text Mode)
//        int[] bytesPATT_ADDRESS = tms.convertAddressIntoBytes(tms.TMS_DEFAULT_VRAM_PATT_ADDRESS);
//        tms.writeToVRAM((byte)bytesPATT_ADDRESS[0]);
//        tms.writeToVRAM((byte)bytesPATT_ADDRESS[1]);
//        tms.writeToVRAM((byte)0);
//        for (int d : ascii) {
//            tms.writeToVRAMIncrement((byte)d);
//        }
        // Write characters on screen (For Text Mode)
//        int[] bytesNAME_ADDRESS = tms.convertAddressIntoBytes(tms.TMS_DEFAULT_VRAM_NAME_ADDRESS);
//        tms.writeToVRAM((byte)bytesNAME_ADDRESS[0]);
//        tms.writeToVRAM((byte)bytesNAME_ADDRESS[1]);
//        tms.writeToVRAM((byte)String.valueOf(' ').codePointAt(0));
//        byte[] ch = "TMS9918A".getBytes();
//        for(byte c : ch) {
//            tms.writeToVRAMIncrement(c);
//        }

//        int[] bytesCOLOR_ADDRESS = tms.convertAddressIntoBytes(tms.TMS_DEFAULT_VRAM_COLOR_ADDRESS);
//        tms.writeToVRAM((byte)bytesCOLOR_ADDRESS[0]);
//        tms.writeToVRAM((byte)bytesCOLOR_ADDRESS[1]);
//        tms.writeToVRAM((byte)0b10000111);

//        int[] bytesPATT_ADDRESS = tms.convertAddressIntoBytes(tms.TMS_DEFAULT_VRAM_PATT_ADDRESS+8);
//        tms.writeToVRAM((byte)bytesPATT_ADDRESS[0]);
//        tms.writeToVRAM((byte)bytesPATT_ADDRESS[1]);
//        tms.writeToVRAM((byte)0b00011000);
//        int[] dataPattern = {
//
//                0b00111100,
//                0b01111110,
//                0b01000010,
//                0b01011010,
//                0b01011010,
//                0b01000010,
//                0b11111111,
//        };
//        for (int d : dataPattern) {
//            tms.writeToVRAMIncrement((byte)d);
//        }
//        int[] bytesSpritePATT_ADDRESS = tms.convertAddressIntoBytes(tms.TMS_DEFAULT_VRAM_SPRITE_PATT_ADDRESS+8);
//        tms.writeToVRAM((byte)bytesSpritePATT_ADDRESS[0]);
//        tms.writeToVRAM((byte)bytesSpritePATT_ADDRESS[1]);
//        tms.writeToVRAM((byte)0b00011000);
//        for (int d : dataPattern) {
//            tms.writeToVRAMIncrement((byte)d);
//        }
//
//        int[] bytesCOLOR_ADDRESS = tms.convertAddressIntoBytes(tms.TMS_DEFAULT_VRAM_COLOR_ADDRESS+8);
//        tms.writeToVRAM((byte)bytesCOLOR_ADDRESS[0]);
//        tms.writeToVRAM((byte)bytesCOLOR_ADDRESS[1]);
//        tms.writeToVRAM((byte)0x67);
//        int[] colorPattern = {
//                0x67,
//                0x67,
//                0xEF,
//                0xEF,
//                0xEF,
//                0xEF,
//                0x20,
//        };
//        for (int d : colorPattern) {
//            tms.writeToVRAMIncrement((byte)d);
//        }
//        int[] bytesNAME_ADDRESS = tms.convertAddressIntoBytes(tms.TMS_DEFAULT_VRAM_NAME_ADDRESS);
//        tms.writeToVRAM((byte)bytesNAME_ADDRESS[0]);
//        tms.writeToVRAM((byte)bytesNAME_ADDRESS[1]);
//        tms.writeToVRAM((byte)1);
//        tms.writeToVRAMIncrement((byte)1);


        JFrame frame = new JFrame("TMS9918 Emulator");
        TMS9918Panel panel = new TMS9918Panel(tms);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(tms.TMS9918_PIXELS_X *4, tms.TMS9918_PIXELS_Y*4); // 2x scaling for 256x192 resolution
        frame.add(panel);
        frame.setVisible(true);

        new Timer(16, e -> {
            loopRender();
            panel.render();
            panel.repaint();
        }).start();
    }
}
