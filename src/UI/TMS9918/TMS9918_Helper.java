package UI.TMS9918;

public class TMS9918_Helper {
    public void initMultiColorMode(TMS9918 tms) {
        tms.writeToVDPRegister((byte) (TMS9918.TMS_R0_MODE_MULTICOLOR));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_0.getValue()|0x80));
        tms.writeToVDPRegister((byte) (TMS9918.TMS_R1_MODE_MULTICOLOR));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_1.getValue()|0x80));

        defaultInit(tms);
    }
    public void initTextMode(TMS9918 tms) {
        tms.writeToVDPRegister((byte) (TMS9918.TMS_R0_MODE_TEXT));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_0.getValue()|0x80));
        tms.writeToVDPRegister((byte) (TMS9918.TMS_R1_MODE_TEXT));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_1.getValue()|0x80));

        defaultInit(tms);
    }
    public void initGraphicsModeII(TMS9918 tms) {

        tms.writeToVDPRegister((byte) (TMS9918.TMS_R0_MODE_GRAPHICS_II));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_0.getValue()|0x80));
        tms.writeToVDPRegister((byte) (TMS9918.TMS_R1_MODE_GRAPHICS_II));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_1.getValue()|0x80));

        defaultInit(tms);
    }
    public void initGraphicsModeI(TMS9918 tms) {
        tms.writeToVDPRegister((byte) (TMS9918.TMS_R0_MODE_GRAPHICS_I));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_0.getValue()|0x80));
        tms.writeToVDPRegister((byte) (TMS9918.TMS_R1_MODE_GRAPHICS_I));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_1.getValue()|0x80));

        defaultInit(tms);
    }
    public void setFgBg(TMS9918 tms, byte Fg, byte Bg) {
        tms.writeToVDPRegister((byte) ((Fg<<4)|Bg));
    }
    private void defaultInit(TMS9918 tms) {
        // Set Bg and Fg
        tms.writeToVDPRegister((byte) 0);
        tms.writeToVDPRegister((byte) (0b10000111));
        // Set name address
        tms.writeToVDPRegister((byte)(TMS9918.TMS_DEFAULT_VRAM_NAME_ADDRESS/1024));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_NAME_TABLE.getValue()|0x80));
        // Set color address
        tms.writeToVDPRegister((byte)(TMS9918.TMS_DEFAULT_VRAM_COLOR_ADDRESS/64));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_COLOR_TABLE.getValue()|0x80));
        // Set sprite attr address
        tms.writeToVDPRegister((byte)(TMS9918.TMS_DEFAULT_VRAM_SPRITE_ATTR_ADDRESS/128));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_SPRITE_ATTR_TABLE.getValue()|0x80));
        // Set sprite pattern address
        tms.writeToVDPRegister((byte)(TMS9918.TMS_DEFAULT_VRAM_SPRITE_PATT_ADDRESS/2048));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_SPRITE_PATT_TABLE.getValue()|0x80));
        // Set pattern address
        tms.writeToVDPRegister((byte)(TMS9918.TMS_DEFAULT_VRAM_PATT_ADDRESS/2048));
        tms.writeToVDPRegister((byte)(TMS9918.TMS9918Registers.TMS_REG_PATTERN_TABLE.getValue()|0x80));
    }
    public void writeToVRAM(TMS9918 tms, int address, int data) {
        int[] Address = tms.convertAddressIntoBytes(address);
        tms.writeToVRAM((byte)Address[0]);
        tms.writeToVRAM((byte)Address[1]);
        tms.writeToVRAM((byte)data);
    }
    public void writeToVRAM(TMS9918 tms, int data) {
        tms.writeToVRAMIncrement((byte)data);
    }
    public void writeBytesToVRAM(TMS9918 tms, int address, int[] data) {
        int[] Address = tms.convertAddressIntoBytes(address);
        tms.writeToVRAM((byte)Address[0]);
        tms.writeToVRAM((byte)Address[1]);
        boolean first = false;
        for (int d : data) {
            if (!first) { first=true; tms.writeToVRAM((byte)d); continue;}

            tms.writeToVRAMIncrement((byte)d);
        }
    }
    public void writeBytesToVRAM(TMS9918 tms, int[] data) {
        for (int d : data) {
            tms.writeToVRAMIncrement((byte)d);
        }
    }
    // Basic Interface
    private boolean MODE = false;
    private byte r1,r2 = 0;
    private int count = 0;
    public void writeByte(TMS9918 tms, byte data) {
        if (count == 0) {r1 = data; return;}
        byte regMask = (byte)(data & 1);
        if (count == 1 && regMask == 1) {
            tms.writeToVDPRegister(r1);
            tms.writeToVDPRegister(data);
            count = 0;
            return;
        }
        if (count == 1) {r2 = data; return;}
        if (count == 2) {
            tms.writeToVRAM(r1);
            tms.writeToVRAM(r2);
            tms.writeToVRAM(data);
            count = 0;
            return;
        }
        if (!MODE) {
            tms.writeToVRAMIncrement(data);
            count = 0;
            return;
        }
    }
    public int readByte(TMS9918 tms, byte data) {
        if (MODE && count == 0) {
            return tms.readFromVDPRegister();
        }
        if (count == 2) {
            tms.writeToVRAM(r1);
            tms.writeToVRAM(r2);
            count = 0;
            return tms.readFromVRAM(data);
        }
        if (!MODE && count == 0) {
            return tms.readFromVRAMIncrement(data);
        }
        return -1;
    }
    public void setMode(boolean MODE) {
        this.MODE = MODE;
    }
}
