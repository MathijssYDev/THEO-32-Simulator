package UI.TMS9918;

import java.util.Arrays;

public class TMS9918 {
    private static final int VRAM_SIZE = 1 << 14; // 16KB
    private static final int VRAM_MASK = VRAM_SIZE - 1; // 0x3fff

    private static final int GRAPHICS_NUM_COLS = 32;
    private static final int GRAPHICS_NUM_ROWS = 24;
    private static final int GRAPHICS_CHAR_WIDTH = 8;

    private static final int TEXT_NUM_COLS = 40;
    private static final int TEXT_NUM_ROWS = 24;
    private static final int TEXT_CHAR_WIDTH = 6;
    private static final int TEXT_PADDING_PX = 8;

    private static final int PATTERN_BYTES = 8;
    private static final int GFXI_COLOR_GROUP_SIZE = 8;

    private static final int MAX_SPRITES = 32;

    private static final int SPRITE_ATTR_Y = 0;
    private static final int SPRITE_ATTR_X = 1;
    private static final int SPRITE_ATTR_NAME = 2;
    private static final int SPRITE_ATTR_COLOR = 3;
    private static final int SPRITE_ATTR_BYTES = 4;
    private static final int LAST_SPRITE_YPOS = 0xD0;
    private static final int MAX_SCANLINE_SPRITES = 4;
    // TMS Register 0 Modes
    public static final int TMS_R0_MODE_GRAPHICS_I = 0x00;
    public static final int TMS_R0_MODE_GRAPHICS_II = 0x02;
    public static final int TMS_R0_MODE_MULTICOLOR = 0x00;
    public static final int TMS_R0_MODE_TEXT = 0x00;
    public static final int TMS_R0_EXT_VDP_ENABLE = 0x01;
    public static final int TMS_R0_EXT_VDP_DISABLE = 0x00;

    // TMS Register 1 Modes
    public static final int TMS_R1_RAM_16K = 0x80;
    public static final int TMS_R1_RAM_4K = 0x00;
    public static final int TMS_R1_DISP_BLANK = 0x00;
    public static final int TMS_R1_DISP_ACTIVE = 0x40;
    public static final int TMS_R1_INT_ENABLE = 0x20;
    public static final int TMS_R1_INT_DISABLE = 0x00;
    public static final int TMS_R1_MODE_GRAPHICS_I = 0x00;
    public static final int TMS_R1_MODE_GRAPHICS_II = 0x00;
    public static final int TMS_R1_MODE_MULTICOLOR = 0x08;
    public static final int TMS_R1_MODE_TEXT = 0x10;
    public static final int TMS_R1_SPRITE_8 = 0x00;
    public static final int TMS_R1_SPRITE_16 = 0x02;
    public static final int TMS_R1_SPRITE_MAG1 = 0x00;
    public static final int TMS_R1_SPRITE_MAG2 = 0x01;

    public static final int STATUS_INT = 0x80;
    public static final int STATUS_5S = 0x40;
    public static final int STATUS_COL = 0x20;

    public static final int TMS_DEFAULT_VRAM_NAME_ADDRESS = 0x3800;
    public static final int TMS_DEFAULT_VRAM_COLOR_ADDRESS = 0x0000;
    public static final int TMS_DEFAULT_VRAM_PATT_ADDRESS = 0x2000;
    public static final int TMS_DEFAULT_VRAM_SPRITE_ATTR_ADDRESS = 0x3B00;
    public static final int TMS_DEFAULT_VRAM_SPRITE_PATT_ADDRESS = 0x1800;

    public static final int TMS_REG_0 = 0;
    public static final int TMS_REG_1 = 1;
    public static final int TMS_REG_NAME_TABLE = 2;
    public static final int TMS_REG_COLOR_TABLE = 3;
    public static final int TMS_REG_PATTERN_TABLE = 4;
    public static final int TMS_REG_SPRITE_ATTR_TABLE = 5;
    public static final int TMS_REG_SPRITE_PATT_TABLE = 6;
    public static final int TMS_REG_FG_BG_COLOR = 7;
    public static final int TMS_NUM_REGISTERS = 8;

    public static final int TMS_MODE_GRAPHICS_I = 0;
    public static final int TMS_MODE_GRAPHICS_II = 1;
    public static final int TMS_MODE_MULTICOLOR = 2;
    public static final int TMS_MODE_TEXT = 3;

    public static final int TMS_TRANSPARENT = 0;

    public static final int TMS9918_PIXELS_X = 256;
    public static final int TMS9918_PIXELS_Y = 192;

    public enum Mode {
        GRAPHICS_I,
        GRAPHICS_II,
        TEXT,
        MULTICOLOR
    }
    public enum TMS9918Registers {
        TMS_REG_0,
        TMS_REG_1,
        TMS_REG_2,
        TMS_REG_3,
        TMS_REG_4,
        TMS_REG_5,
        TMS_REG_6,
        TMS_REG_7,
        TMS_NUM_REGISTERS,
        TMS_REG_NAME_TABLE(TMS_REG_2.ordinal()),
        TMS_REG_COLOR_TABLE(TMS_REG_3.ordinal()),
        TMS_REG_PATTERN_TABLE(TMS_REG_4.ordinal()),
        TMS_REG_SPRITE_ATTR_TABLE(TMS_REG_5.ordinal()),
        TMS_REG_SPRITE_PATT_TABLE(TMS_REG_6.ordinal()),
        TMS_REG_FG_BG_COLOR(TMS_REG_7.ordinal());

        private final int value;

        TMS9918Registers(int value) {
            this.value = value;
        }

        TMS9918Registers() {
            this.value = ordinal();
        }

        public int getValue() {
            return value;
        }
    }
    public enum TMS9918Color {
        TMS_TRANSPARENT(0),
        TMS_BLACK(1),
        TMS_MED_GREEN(2),
        TMS_LT_GREEN(3),
        TMS_DK_BLUE(4),
        TMS_LT_BLUE(5),
        TMS_DK_RED(6),
        TMS_CYAN(7),
        TMS_MED_RED(8),
        TMS_LT_RED(9),
        TMS_DK_YELLOW(10),
        TMS_LT_YELLOW(11),
        TMS_DK_GREEN(12),
        TMS_MAGENTA(13),
        TMS_GREY(14),
        TMS_WHITE(15);
        private int value;

        TMS9918Color(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private byte[] registers = new byte[TMS_NUM_REGISTERS];
    private byte status;
    private int currentAddress;
    private byte regWriteStage;
    private byte regWriteStage0Value;
    private byte readAheadBuffer;
    private int mode;
    private byte[] vram = new byte[VRAM_SIZE];
    private byte[] rowSpriteBits = new byte[TMS9918_PIXELS_X];

    public void reset() {
        regWriteStage0Value = 0;
        currentAddress = 0;
        regWriteStage = 0;
        status = 0;
        readAheadBuffer = 0;
        Arrays.fill(registers, (byte) 0);
        mode = getTmsMode(this);
    }
    private int getTmsMode(TMS9918 tms9918) {
        if ((registers[TMS_REG_0] & TMS_R0_MODE_GRAPHICS_II) != 0) {
            return TMS_MODE_GRAPHICS_II;
        }

        switch ((registers[TMS_REG_1] & (TMS_R1_MODE_MULTICOLOR | TMS_R1_MODE_TEXT)) >> 3) {
            case 0:
                return TMS_MODE_GRAPHICS_I;
            case 1:
                return TMS_MODE_MULTICOLOR;
            case 2:
                return TMS_MODE_TEXT;
        }
        return TMS_MODE_GRAPHICS_I;
    }
    private void TMS9918OutputSprites(int y, byte[] pixels) {
        boolean spriteMag = tmsSpriteMag();
        boolean sprite16 = tmsSpriteSize() == 16;
        byte spriteSize = tmsSpriteSize();
        byte spriteSizePx = (byte) (spriteSize * (spriteMag ? 2 : 1));
        int spriteAttrTableAddr = tmsSpriteAttrTableAddr();
        int spritePatternAddr = tmsSpritePatternTableAddr();

        byte spritesShown = 0;

        if (y == 0) {
            status = 0;
        }

        byte[] spriteAttr = Arrays.copyOfRange(vram, spriteAttrTableAddr, spriteAttrTableAddr + MAX_SPRITES * SPRITE_ATTR_BYTES);
        for (int spriteIdx = 0; spriteIdx < MAX_SPRITES; ++spriteIdx) {
            int yPos = spriteAttr[SPRITE_ATTR_Y] & 0xFF;

            if (yPos == LAST_SPRITE_YPOS) {
                if ((status & STATUS_5S) == 0) {
                    status |= spriteIdx;
                }
                break;
            }

            if (yPos > 0xe0) {
                yPos -= 256;
            }

            yPos += 1;

            int pattRow = y - yPos;
            if (spriteMag) {
                pattRow >>= 1;
            }

            if (pattRow < 0 || pattRow >= spriteSize) {
                continue;
            }

            if (spritesShown == 0) {
                Arrays.fill(rowSpriteBits, (byte) 0);
            }

            byte spriteColor = (byte) (spriteAttr[SPRITE_ATTR_COLOR] & 0x0f);

            if (++spritesShown > MAX_SCANLINE_SPRITES) {
                if ((status & STATUS_5S) == 0) {
                    status |= STATUS_5S | spriteIdx;
                }
                break;
            }

            byte pattIdx = spriteAttr[SPRITE_ATTR_NAME];
            int pattOffset = spritePatternAddr + pattIdx * PATTERN_BYTES + pattRow;

            int earlyClockOffset = (spriteAttr[SPRITE_ATTR_COLOR] & 0x80) != 0 ? -32 : 0;
            int xPos = (spriteAttr[SPRITE_ATTR_X] & 0xFF) + earlyClockOffset;

            byte pattByte = vram[pattOffset];
            byte screenBit = 0, pattBit = 0;

            int endXPos = xPos + spriteSizePx;
            if (endXPos >= TMS9918_PIXELS_X) {
                endXPos = TMS9918_PIXELS_X;
            }

            for (int screenX = xPos; screenX < endXPos; ++screenX, ++screenBit) {
                if (screenX >= 0) {
                    if (pattByte < 0) {
                        if (spriteColor != TMS_TRANSPARENT && rowSpriteBits[screenX] < 2) {
                            pixels[screenX] = spriteColor;
                        }

                        if (rowSpriteBits[screenX] != 0) {
                            status |= STATUS_COL;
                        } else {
                            rowSpriteBits[screenX] = (byte) (spriteColor + 1);
                        }
                    }
                }

                if (!spriteMag || (screenBit & 0x01) != 0) {
                    pattByte <<= 1;
                    if (++pattBit == GRAPHICS_CHAR_WIDTH && sprite16) {
                        pattBit = 0;
                        pattByte = vram[pattOffset + PATTERN_BYTES * 2];
                    }
                }
            }
        }
    }
    private void TMS9918GraphicsIScanLine(int y, byte[] pixels) {
        byte tileY = (byte) (y >> 3);
        byte pattRow = (byte) (y & 0x07);

        int rowNamesAddr = tmsNameTableAddr() + tileY * GRAPHICS_NUM_COLS;

        byte[] patternTable = Arrays.copyOfRange(vram, tmsPatternTableAddr(), tmsPatternTableAddr() + VRAM_SIZE);
        byte[] colorTable = Arrays.copyOfRange(vram, tmsColorTableAddr(), tmsColorTableAddr() + VRAM_SIZE);

        for (int tileX = 0; tileX < GRAPHICS_NUM_COLS; ++tileX) {
            byte pattIdx = vram[rowNamesAddr + tileX];
            byte pattByte = patternTable[pattIdx * PATTERN_BYTES + pattRow];
            byte colorByte = colorTable[pattIdx / GFXI_COLOR_GROUP_SIZE];

            byte fgColor = tmsFgColor(colorByte);
            byte bgColor = tmsBgColor(colorByte);

            for (int pattBit = 0; pattBit < GRAPHICS_CHAR_WIDTH; ++pattBit) {
                boolean pixelBit = (pattByte & 0x80) != 0;
                pixels[tileX * GRAPHICS_CHAR_WIDTH + pattBit] = (byte) (pixelBit ? fgColor : bgColor);
                pattByte <<= 1;
            }
        }

        TMS9918OutputSprites(y, pixels);
    }
    private void vrEmuTms9918GraphicsIIScanLine(int y, byte[] pixels) {
        byte tileY = (byte) (y >> 3);
        byte pattRow = (byte) (y & 0x07);

        int rowNamesAddr = tmsNameTableAddr() + tileY * GRAPHICS_NUM_COLS;

        byte nameMask = (byte) (((registers[TMS_REG_COLOR_TABLE] & 0x7f) << 3) | 0x07);

        int pageThird = ((tileY & 0x18) >> 3) & (registers[TMS_REG_PATTERN_TABLE] & 0x03);
        int pageOffset = pageThird << 11;

        byte[] patternTable = Arrays.copyOfRange(vram, tmsPatternTableAddr() + pageOffset, tmsPatternTableAddr() + pageOffset + VRAM_SIZE);
        byte[] colorTable = Arrays.copyOfRange(vram, tmsColorTableAddr() + (pageOffset & ((registers[TMS_REG_COLOR_TABLE] & 0x60) << 6)), tmsColorTableAddr() + (pageOffset & ((registers[TMS_REG_COLOR_TABLE] & 0x60) << 6)) + VRAM_SIZE);

        for (int tileX = 0; tileX < GRAPHICS_NUM_COLS; ++tileX) {
            byte pattIdx = (byte) (vram[rowNamesAddr + tileX] & nameMask);

            int pattRowOffset = pattIdx * PATTERN_BYTES + pattRow;
            byte pattByte = patternTable[pattRowOffset];
            byte colorByte = colorTable[pattRowOffset];

            byte fgColor = tmsFgColor(colorByte);
            byte bgColor = tmsBgColor(colorByte);

            for (int pattBit = 0; pattBit < GRAPHICS_CHAR_WIDTH; ++pattBit) {
                boolean pixelBit = (pattByte << pattBit & 0x80) != 0;
                pixels[tileX * GRAPHICS_CHAR_WIDTH + pattBit] = (byte) (pixelBit ? fgColor : bgColor);
            }
        }

        TMS9918OutputSprites(y, pixels);
    }
    private void TMS9918TextScanLine(int y, byte[] pixels) {
        byte tileY = (byte) (y >> 3);
        byte pattRow = (byte) (y & 0x07);

        int rowNamesAddr = tmsNameTableAddr() + tileY * TEXT_NUM_COLS;
        byte[] patternTable = Arrays.copyOfRange(vram, tmsPatternTableAddr(), tmsPatternTableAddr() + VRAM_SIZE);
        byte bgColor = tmsMainBgColor();
        byte fgColor = tmsMainFgColor();

        Arrays.fill(pixels, 0, TEXT_PADDING_PX, bgColor);
        Arrays.fill(pixels, TMS9918_PIXELS_X - TEXT_PADDING_PX, TMS9918_PIXELS_X, bgColor);

        for (int tileX = 0; tileX < TEXT_NUM_COLS; ++tileX) {
            byte pattIdx = vram[rowNamesAddr + tileX];
            byte pattByte = patternTable[pattIdx * PATTERN_BYTES + pattRow];
            //System.out.println(pattIdx);

            for (int pattBit = 0; pattBit < TEXT_CHAR_WIDTH; ++pattBit) {
                boolean pixelBit = (pattByte << pattBit & 0x80) != 0;
                pixels[TEXT_PADDING_PX + tileX * TEXT_CHAR_WIDTH + pattBit] = (byte) (pixelBit ? fgColor : bgColor);
            }
        }
    }
    private void TMS9918MultiColorScanLine(int y, byte[] pixels) {
        byte tileY = (byte) (y >> 3);
        byte pattRow = (byte) (((y / 4) & 0x01) + (tileY & 0x03) * 2);

        int namesAddr = tmsNameTableAddr() + tileY * GRAPHICS_NUM_COLS;
        byte[] patternTable = Arrays.copyOfRange(vram, tmsPatternTableAddr(), tmsPatternTableAddr() + VRAM_SIZE);

        for (int tileX = 0; tileX < GRAPHICS_NUM_COLS; ++tileX) {
            byte pattIdx = vram[namesAddr + tileX];
            byte colorByte = patternTable[pattIdx * PATTERN_BYTES + pattRow];

            Arrays.fill(pixels, tileX * 8, tileX * 8 + 4, tmsFgColor(colorByte));
            Arrays.fill(pixels, tileX * 8 + 4, tileX * 8 + 8, tmsBgColor(colorByte));
        }

        TMS9918OutputSprites(y, pixels);
    }
    public void TMS9918ScanLine(int y, byte[] pixels) {
        switch (mode) {
            case TMS_MODE_GRAPHICS_I:
                TMS9918GraphicsIScanLine(y, pixels);
                break;
            case TMS_MODE_GRAPHICS_II:
                vrEmuTms9918GraphicsIIScanLine(y, pixels);
                break;
            case TMS_MODE_MULTICOLOR:
                TMS9918MultiColorScanLine(y, pixels);
                break;
            case TMS_MODE_TEXT:
                TMS9918TextScanLine(y, pixels);
                break;
        }
    }

    private byte writeToVDPRegister_STAGE = 0;
    private byte writeToVDPRegister_DATA = 0;
    private boolean M3,M2,M1 = false;
    public void writeToVDPRegister(byte data) {
        if (writeToVDPRegister_STAGE == 0) { writeToVDPRegister_DATA = data; writeToVDPRegister_STAGE = 1; return;}

        if ((data & 0x80) == 0) {System.err.println("VDP: Write to VDP register (Address): (address & 0x80) == 0; 10000(RS0)(RS1)(RS2)"); return;}

        registers[data&0b01111111] = writeToVDPRegister_DATA;
        writeToVDPRegister_STAGE = 0;

        if ((data&0b01111111) <= TMS9918Registers.TMS_REG_1.getValue()) {
            M1 = (registers[1] & 0b00010000) > 0;
            M2 = (registers[1] & 0b00001000) > 0;
            M3 = (registers[0] & 0b00000010) > 0;

            switch (((M1? 1 : 0)<<2)|((M2? 1 : 0)<<1)|(M3? 1 : 0)) {
                case 0:
                    mode = TMS_MODE_GRAPHICS_I;
                    break;
                case 1:
                    mode = TMS_MODE_GRAPHICS_II;
                    break;
                case 2:
                    mode = TMS_MODE_MULTICOLOR;
                    break;
                case 4:
                    mode = TMS_MODE_TEXT;
                    break;
            }
        }
    }
    public int readFromVDPRegister() {return STATUS_INT;}

    private byte writeToVRAM_STAGE = 0;
    private byte writeToVRAM_ADDR1,writeToVRAM_ADDR2 = 0;
    public void writeToVRAM(byte data) {
        if (writeToVRAM_STAGE == 0) { writeToVRAM_ADDR1 = (byte) (data&0xff); writeToVRAM_STAGE = 1; return;}
        if (writeToVRAM_STAGE == 1) { writeToVRAM_ADDR2 = (byte) (data&0xff); writeToVRAM_STAGE = 2; return;}

        currentAddress = combineBytes(writeToVRAM_ADDR1,writeToVRAM_ADDR2);
        vram[currentAddress++] = (byte) (data&0xff);
        writeToVRAM_STAGE = 0;
    }
    public void writeToVRAMIncrement(byte data) {
        vram[currentAddress++] = (byte) (data&0xff);
    }

    private byte readFromVRAM_STAGE = 0;
    private byte readFromVRAM_ADDR1,readFromVRAM_ADDR2 = 0;
    public byte readFromVRAM(byte data) {
        if (readFromVRAM_STAGE == 0) { readFromVRAM_ADDR1 = (byte) (data&0xff); readFromVRAM_STAGE = 1; return 0;}
        if (readFromVRAM_STAGE == 1) { readFromVRAM_ADDR2 = (byte) (data&0xff); readFromVRAM_STAGE = 2; return 0;}

        currentAddress = combineBytes(readFromVRAM_ADDR1,readFromVRAM_ADDR2);
        readFromVRAM_STAGE = 0;
        return vram[currentAddress++];
    }
    public int readFromVRAMIncrement(byte data) {
        return vram[currentAddress++];
    }


    public static int combineBytes(int byte1, int byte2) {
        int A6to13 = (byte1 & 0xFF);
        int A0to5 = byte2>>2;
        int result = ((A6to13 << 6) | A0to5);
        return result;
    }
    public static int[] convertAddressIntoBytes(int value) {
        // Ensure the value is within the 14-bit range (0 to 16383)
        if (value < 0 || value > 0x3FFF) {
            throw new IllegalArgumentException("Value must be a 14-bit integer (0 to 16383)");
        }
        int byte1 = (value >> 6) & 0xFF;
        int byte2 = (value & 0x3F) << 2;

        return new int[] { byte1, byte2 };
    }
    private byte tmsSpriteSize() {
        return (registers[TMS_REG_1] & TMS_R1_SPRITE_16) != 0 ? (byte) 16 : (byte) 8;
    }

    private boolean tmsSpriteMag() {
        return (registers[TMS_REG_1] & TMS_R1_SPRITE_MAG2) != 0;
    }

    private int tmsNameTableAddr() {
        return (registers[TMS_REG_NAME_TABLE] & 0x0f) << 10;
    }

    private int tmsColorTableAddr() {
        byte mask = (mode == TMS_MODE_GRAPHICS_II) ? (byte) 0x80 : (byte) 0xff;
        return (registers[TMS_REG_COLOR_TABLE] & mask) << 6;
    }

    private int tmsPatternTableAddr() {
        byte mask = (mode == TMS_MODE_GRAPHICS_II) ? (byte) 0x04 : (byte) 0x07;
        return (registers[TMS_REG_PATTERN_TABLE] & mask) << 11;
    }

    private int tmsSpriteAttrTableAddr() {
        return (registers[TMS_REG_SPRITE_ATTR_TABLE] & 0x7f) << 7;
    }

    private int tmsSpritePatternTableAddr() {
        return (registers[TMS_REG_SPRITE_PATT_TABLE] & 0x07) << 11;
    }

    private byte tmsMainBgColor() {
        return (byte) (registers[TMS_REG_FG_BG_COLOR] & 0x0f);
    }

    private byte tmsMainFgColor() {
        byte c = (byte) (registers[TMS_REG_FG_BG_COLOR] >> 4);
        return c == TMS_TRANSPARENT ? tmsMainBgColor() : c;
    }

    private byte tmsFgColor(byte colorByte) {
        byte c = (byte) (colorByte >> 4);
        return c == TMS_TRANSPARENT ? tmsMainBgColor() : c;
    }

    private byte tmsBgColor(byte colorByte) {
        byte c = (byte) (colorByte & 0x0f);
        return c == TMS_TRANSPARENT ? tmsMainBgColor() : c;
    }
}
