package theo32machine;

import UI.MemoryInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class THEO32 extends Thread {
    boolean HALT = false;

    // ################### SIMULATION ###################
    int Intruction;
    int ClockHz = 0;
    int COMport = 0;

    long nanoSecondDelay = 0L;

    // ################### CONSTANTS ###################
    final int k32 = (int)Math.pow(2,15);
    final int k16 = (int)Math.pow(2,14);

    final int theo32NanoInstructionCount = 16;

    // ################### MEMORY ###################
    public byte[] RAM = new byte[k32];  // 32K RAM
    public byte[] ROM = new byte[k32];  // 32K ROM
    public byte[] Boot = new byte[k16]; // 16K bootloader

    // ################### DEVICES ###################
    AY8910 ay;

    // ################### REQUESTS ###################
    public CopyOnWriteArrayList<Integer[]> AY8910_Requests = new CopyOnWriteArrayList<Integer[]>();
    public ArrayList<Integer[]> TMS9918_Requests = new ArrayList<Integer[]>();
    public ps2Keyboard ps2keyboard;

    // ################### THEO32 REGISTERS ###################
    int Accumulator = 0;
    int Programcounter = 0;
    int InstructionRegister = 0;

    boolean FLG_CARRY = false;
    boolean FLG_OVERFLOW = false;
    boolean FLG_EQUAL = false;

    boolean CNU_BIT = false;

    // ################### INSTRUCTIONS ###################
    enum Instruction {
        NOP((cpu, args) -> {
            cpu.Programcounter += 1;
        }),
        CNU((cpu, args) -> {
            cpu.CNU_BIT = true;
            cpu.Programcounter += 1;
        }),
        BRK((cpu, args) -> {
            cpu.HALT = true;
        }),
        INC((cpu, args) -> {
            cpu.Accumulator++;
            cpu.updateFlags();
            cpu.Programcounter += 1;
        }),
        DEC((cpu, args) -> {
            cpu.Accumulator--;
            cpu.updateFlags();
            cpu.Programcounter += 1;
        }),
        ADD_A((cpu, args) -> {
            cpu.Accumulator+=cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF;
            cpu.updateFlags();
            cpu.Programcounter += 3;
        }),
        ADD_V((cpu, args) -> {
            cpu.Accumulator+=(args[0] & 0xFF);
            cpu.updateFlags();
            cpu.Programcounter += 2;
        }),
        SUB_A((cpu, args) -> {
            cpu.Accumulator-=cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF;
            cpu.updateFlags();
            cpu.Programcounter += 3;
        }),
        SUB_V((cpu, args) -> {
            cpu.Accumulator-=(args[0] & 0xFF);
            cpu.updateFlags();
            cpu.Programcounter += 2;
        }),
        ADC_A((cpu, args) -> {
            cpu.Accumulator+=cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF+(cpu.FLG_CARRY?1:0);
            cpu.updateFlags();
            cpu.Programcounter += 3;
        }),
        ADC_V((cpu, args) -> {
            cpu.Accumulator+=(args[0] & 0xFF)+(cpu.FLG_CARRY?1:0);
            cpu.updateFlags();
            cpu.Programcounter += 2;
        }),
        SUC_A((cpu, args) -> {
            cpu.Accumulator-=cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF+(cpu.FLG_CARRY?1:0);
            cpu.updateFlags();
            cpu.Programcounter += 3;
        }),
        SUC_V((cpu, args) -> {
            cpu.Accumulator-=(args[0] & 0xFF)+(cpu.FLG_CARRY?1:0);
            cpu.updateFlags();
            cpu.Programcounter += 2;
        }),
        ROL((cpu, args) -> {
            cpu.Accumulator=(cpu.Accumulator << 1) | (cpu.Accumulator >>> (32 - 1));
            cpu.Programcounter += 1;
        }),
        ROR((cpu, args) -> {
            cpu.Accumulator=(cpu.Accumulator >>> 1) | (cpu.Accumulator << (32 - 1));
            cpu.Programcounter += 1;
        }),
        AND_A((cpu, args) -> {
            cpu.Accumulator=cpu.Accumulator&cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF;
            cpu.Programcounter += 3;
        }),
        AND_V((cpu, args) -> {
            cpu.Accumulator=cpu.Accumulator&(args[0] & 0xFF)& 0xFF;
            cpu.Programcounter += 2;
        }),
        OR_A((cpu, args) -> {
            cpu.Accumulator=cpu.Accumulator|cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF;
            cpu.Programcounter += 3;
        }),
        OR_V((cpu, args) -> {
            cpu.Accumulator=cpu.Accumulator|(args[0] & 0xFF)& 0xFF;
            cpu.Programcounter += 2;
        }),
        XOR_A((cpu, args) -> {
            cpu.Accumulator=cpu.Accumulator^cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF;
            cpu.Programcounter += 3;
        }),
        XOR_V((cpu, args) -> {
            cpu.Accumulator=cpu.Accumulator^(args[0] & 0xFF)& 0xFF;
            cpu.Programcounter += 2;
        }),
        CMP_A((cpu, args) -> {
            cpu.FLG_CARRY=(cpu.Accumulator>(cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF)?true:false);
            cpu.FLG_OVERFLOW=(cpu.Accumulator<(cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF)?true:false);
            cpu.FLG_EQUAL=(cpu.Accumulator==(cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF),false)& 0xFF)?true:false);
            cpu.Programcounter += 3;
        }),
        CMP_V((cpu, args) -> {
            cpu.FLG_CARRY=(cpu.Accumulator>(args[0] & 0xFF)?true:false);
            cpu.FLG_OVERFLOW=(cpu.Accumulator<(args[0] & 0xFF)?true:false);
            cpu.FLG_EQUAL=(cpu.Accumulator==(args[0] & 0xFF)?true:false);
            cpu.Programcounter += 2;
        }),
        STA((cpu, args) -> {
            try {
                cpu.writeByte(cpu.InstructionRegister, ((args[0] & 0xFF) << 8) | (args[1] & 0xFF), false, cpu.Accumulator);
                cpu.Programcounter += 3;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }),
        LDA_A((cpu, args) -> {
            cpu.Accumulator = cpu.readByte(cpu.InstructionRegister,((args[0] & 0xFF) << 8) | (args[1] & 0xFF) & 0xFF,false)& 0xFF;
            cpu.Programcounter += 3;
        }),
        LDA_V((cpu, args) -> {
            cpu.Accumulator = args[0] & 0xFF;
            cpu.Programcounter += 2;
        }),
        JMP((cpu, args) -> {
            cpu.Programcounter = ((args[0] & 0xFF) << 8) | (args[1] & 0xFF) & 0xFF;
        }),
        JME((cpu, args) -> {
            cpu.Programcounter += 3;
            if (cpu.FLG_EQUAL) {
                cpu.Programcounter = ((args[0] & 0xFF) << 8) | (args[1] & 0xFF) & 0xFF;
            }
        }),
        JMC((cpu, args) -> {
            cpu.Programcounter += 3;
            if (cpu.FLG_CARRY) {
                cpu.Programcounter = ((args[0] & 0xFF) << 8) | (args[1] & 0xFF) & 0xFF;
            }
        }),
        JMO((cpu, args) -> {
            cpu.Programcounter += 3;
            if (cpu.FLG_OVERFLOW) {
                cpu.Programcounter = ((args[0] & 0xFF) << 8) | (args[1] & 0xFF) & 0xFF;
            }
        });
        private final BiConsumer<THEO32, int[]> operation;

        Instruction(BiConsumer<THEO32, int[]> operation) {
            this.operation = operation;
        }

        public void execute(THEO32 cpu, int[] args) {
            operation.accept(cpu, args);
        }
    }
    private void updateFlags() {
        FLG_CARRY = false;
        FLG_EQUAL = false;
        FLG_OVERFLOW = false;

        if (Accumulator > 255) {
            FLG_CARRY = true;
            Accumulator -=256;
        } else if (Accumulator < 0) {
            FLG_OVERFLOW = true;
            Accumulator+=256;
        } else if (Accumulator == 0) {
            FLG_EQUAL = true;
        }
    }
    private Instruction decodeInstruction(int instructionCode) {
        switch (instructionCode & 0xFF) {
            case 0x00: return Instruction.NOP;
            case 0x10: return Instruction.CNU;
            case 0x20: return Instruction.BRK;
            case 0x01: return Instruction.INC;
            case 0x11: return Instruction.DEC;
            case 0x02: return Instruction.ADD_A;
            case 0x12: return Instruction.ADD_V;
            case 0x22: return Instruction.SUB_A;
            case 0x32: return Instruction.SUB_V;
            case 0x42: return Instruction.ADC_A;
            case 0x52: return Instruction.ADC_V;
            case 0x62: return Instruction.SUC_A;
            case 0x72: return Instruction.SUC_V;
            case 0x82: return Instruction.ADD_A;
            case 0x92: return Instruction.ADD_V;
            case 0xA2: return Instruction.SUB_A;
            case 0xB2: return Instruction.SUB_V;
            case 0xC2: return Instruction.ADC_A;
            case 0xD2: return Instruction.ADC_V;
            case 0xE2: return Instruction.SUC_A;
            case 0xF2: return Instruction.SUC_V;
            case 0x03: return Instruction.ROL;
            case 0x13: return Instruction.ROR;
            case 0x04: return Instruction.AND_A;
            case 0x14: return Instruction.AND_V;
            case 0x24: return Instruction.OR_A;
            case 0x34: return Instruction.OR_V;
            case 0x44: return Instruction.XOR_A;
            case 0x54: return Instruction.XOR_V;
            case 0x84: return Instruction.AND_A;
            case 0x94: return Instruction.AND_V;
            case 0xA4: return Instruction.OR_A;
            case 0xB4: return Instruction.OR_V;
            case 0xC4: return Instruction.XOR_A;
            case 0xD4: return Instruction.XOR_V;
            case 0x05: return Instruction.CMP_A;
            case 0x15: return Instruction.CMP_V;
            case 0x06: return Instruction.STA;
            case 0x46: return Instruction.STA;
            case 0x86: return Instruction.STA;
            case 0x26: return Instruction.LDA_A;
            case 0x36: return Instruction.LDA_V;
            case 0x66: return Instruction.LDA_A;
            case 0x76: return Instruction.LDA_V;
            case 0xA6: return Instruction.LDA_A;
            case 0xB6: return Instruction.LDA_V;
            case 0x07: return Instruction.JMP;
            case 0x17: return Instruction.JMP;
            case 0x27: return Instruction.JME;
            case 0x37: return Instruction.JME;
            case 0x47: return Instruction.JMC;
            case 0x57: return Instruction.JMC;
            case 0x67: return Instruction.JMO;
            case 0x77: return Instruction.JMO;


            default: throw new IllegalArgumentException("Invalid instruction code: " + (instructionCode & 0xFF));
        }
    }

    public THEO32(String ROMLocation, String BootLocation,int COMport,int ClockHz, boolean BasicUIEnabled) throws Exception {
        this.COMport = COMport;
        this.ClockHz = ClockHz;

        nanoSecondDelay = 1000_000_000L/ClockHz;

        // Initialize ROM/BOOTLOADER
        ROM = readBinaryFile(ROMLocation,k32);
        Boot = readBinaryFile(BootLocation,k32);

        // Start AY8910 Communication
        ay = new AY8910(this);

        AY8910.SerialReader serialReader = new AY8910.SerialReader();
        serialReader.start();


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ay.start();
        if (BasicUIEnabled) {
            ps2keyboard = new ps2Keyboard();
            RamInfoTable.createAndShowGUI();
        }

    }
    long prevNanoSeconds = 0;
    long prevNanoSecondsUpdate = 0;

    public void run() {
        while (!HALT) {
            if (System.nanoTime()>=prevNanoSeconds+nanoSecondDelay*theo32NanoInstructionCount) {
                long NanoSeconds = System.nanoTime();
                cycle();
                prevNanoSeconds = NanoSeconds;
            }
            if (System.nanoTime()>=prevNanoSecondsUpdate+100_000_000L) {
                long NanoSeconds = System.nanoTime();
                RamInfoTable.updateTableData(RAM);

                MemoryInterface.updateTableData(RAM);
                prevNanoSecondsUpdate = NanoSeconds;
            }
        }

    }

    public void cycle() {
        int instructionByte = readByte(0,Programcounter,true);
        int[] arguments = new int[]{
                readByte(instructionByte,Programcounter+1,true),
                readByte(instructionByte,Programcounter+2,true)
        };
        InstructionRegister = instructionByte;

        Instruction instruction = decodeInstruction(instructionByte);
        instruction.execute(this,arguments);
    }

    public int readByte(int Instruction, int byteLocation, boolean Command) {
        int response = DeviceRequestCheck(byteLocation,-1);
        if (response != -1) return response;

        Instruction = Instruction&0xFF;
        try {
            if (Command) {
                if (byteLocation >= 0x4000) {
                    return RAM[byteLocation];
                }
                return (CNU_BIT?RAM[byteLocation]:Boot[byteLocation]);
            }
            if (Instruction == 0x46 || Instruction == 0x66 || Instruction == 0x76) {
                return (byteLocation >= 0x4000 ? RAM[byteLocation] : Boot[byteLocation]);
            }
            return (Instruction >= 0x80 ? ROM[byteLocation] : RAM[byteLocation]);
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
    public void writeByte(int Instruction, int byteLocation, boolean Command, int value) throws Exception {
        int response = DeviceRequestCheck(byteLocation,value);

        Instruction = Instruction&0xFF;
        try {
            if (Command) {
                if (CNU_BIT) {
                    RAM[byteLocation] = (byte) value;
                } else {
                    Boot[byteLocation] = (byte) value;
                }
                return;
            }
            if (Instruction == 0x46 || Instruction == 0x66 || Instruction == 0x76) {
                if (byteLocation >= 0x4000) {
                    RAM[byteLocation] = (byte) value;
                }
                return;
            }
            if (Instruction >= 0x80){
                ROM[byteLocation] = (byte) value;
            } else {
                RAM[byteLocation] = (byte) value;
            }
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }
    }
    private int DeviceRequestCheck(int byteLocation, int value) {
        if (byteLocation < 0x8000) return -1;

        if (byteLocation >= 0x8080 && byteLocation <= 0x8380) {
            boolean BC1 = false;
            boolean BDIR = false;

            switch (byteLocation) {
                case 0x8180:
                    BC1 = true;
                    break;
                case 0x8280:
                    BDIR = true;
                    break;
                case 0x8380:
                    BC1 = true;
                    BDIR = true;
                    break;
            }

            Integer[] command = new Integer[]{BC1 ? 1 : 0, BDIR ? 1 : 0, value};
            AY8910_Requests.add(command);
        }
        if (byteLocation >= 0xA100 && byteLocation <= 0xA400) {
            switch (byteLocation) {
                case 0xA100:
                    return ps2keyboard.getRegister1();
                case 0xA200:
                    return ps2keyboard.getRegister2();
                case 0xA400:
                    return ps2keyboard.getRegister3();
            }
        }
        return -1;
    }

    private byte[] readBinaryFile(String Location,int Size) throws Exception {
        byte[] content = new byte[Size];
        File File = new File(Location);
        Scanner myReader = new Scanner(File);

        int row = 0;
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();

            int collumb = 0;
            for(String hexByte : Arrays.stream(data.split("\t")).filter(list -> !list.isEmpty()).collect(Collectors.toList())) {
                content[collumb+row] = (byte) Integer.parseInt(hexByte.substring(2),16);
                collumb++;
            }
            row+=16;
        }
        myReader.close();

        return content;
    }
}
