package theo32machine;

import java.util.concurrent.CopyOnWriteArrayList;
import com.fazecast.jSerialComm.SerialPort;

public class AYTEST extends Thread {
    public static CopyOnWriteArrayList<Integer[]> AY8910_Requests = new CopyOnWriteArrayList<>();
    static SerialPort comPort;

    private static void DeviceRequestCheck(int byteLocation, int value) {
        if (byteLocation >= 0x8000) {
            if (byteLocation >= 0x8080 && byteLocation <= 0x8380) {
                boolean BC1 = false;
                boolean BDIR = false;
                if (byteLocation == 0x8180) {
                    BC1 = true;
                } else if (byteLocation == 0x8280) {
                    BDIR = true;
                } else if (byteLocation == 0x8380) {
                    BC1 = true;
                    BDIR = true;
                }
                Integer[] command = new Integer[]{BC1 ? 1 : 0, BDIR ? 1 : 0, value};
                AY8910_Requests.add(command);
            }
        }
    }

    public static void write(char reg, char value) {
        DeviceRequestCheck(0x8080, 0);
        DeviceRequestCheck(0x8380, reg);
        DeviceRequestCheck(0x8080, 0);
        DeviceRequestCheck(0x8280, value);
    }

    public static void main(String[] args) {
        DeviceRequestCheck(0x8080, 0);
        write((char)7,(char)0b00111110);
        write((char)8,(char)0b00001111);
        write((char)0,(char)0xF0);
        write((char)1,(char)1);

        AYTEST ay = new AYTEST();
        SerialReader serialReader = new SerialReader();
        serialReader.start();
        try {
            Thread.sleep(1500); // Sleep for a bit to prevent busy waiting
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ay.start();

    }

    public AYTEST() {
        // Initialize serial port
        Runtime.getRuntime().addShutdownHook(new Thread(() -> comPort.closePort()));

        comPort = SerialPort.getCommPorts()[0];
        comPort.setComPortParameters(115200, 8, 1, 0);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        if (comPort.openPort()) {
            System.out.println("Port is open.");
        } else {
            System.out.println("Failed to open port.");
        }
    }

    public void run() {
        for (Integer[] v : AY8910_Requests) {
            boolean BC1 = v[0] == 1;
            boolean BDIR = v[1] == 1;

            if (!BC1 && !BDIR) {
                setInactive();
            } else if (BC1 && BDIR) {
                setLatch(v[2]);
            } else if (BDIR) {
                setWrite(v[2]);
            }
            AY8910_Requests.remove(v);
        }

    }

    public void setLatch(int value) {
        sendToArduino("L" + value);
    }

    public void setWrite(int value) {
        sendToArduino("W" + value);
    }

    public void setInactive() {
        sendToArduino("I00");
    }

    private void sendToArduino(String message) {
        String command = message.substring(0, 1);
        int value = Integer.parseInt(message.substring(1));

        char encodedValue = (char) (value);
        String messageToSend = command + String.format("%02x", encodedValue & 0xff).toUpperCase();

        byte[] messageBytes = messageToSend.getBytes();

       // System.out.println("0x" + String.format("%02x", encodedValue & 0xff));
        comPort.writeBytes(messageBytes, messageBytes.length);
        comPort.flushIOBuffers(); // Ensure the message is sent immediately

        try {
            Thread.sleep(150); // Reduce the delay to 100 ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    // Inner class for reading from the serial port
    public static class SerialReader extends Thread {
        private StringBuilder buffer = new StringBuilder();

        public void run() {
            byte[] readBuffer = new byte[1024];
            while (true) {
                int numBytes = comPort.readBytes(readBuffer, readBuffer.length);
                if (numBytes > 0) {
                    synchronized (buffer) {
                        for (int i = 0; i < numBytes; i++) {
                            buffer.append((char) readBuffer[i]);
                        }
                        processBuffer();
                    }
                }
            }
        }

        private void processBuffer() {
            String data = buffer.toString();
            int endIndex;
            while ((endIndex = data.indexOf('\n')) != -1) { // Assuming newline character separates messages
                String message = data.substring(0, endIndex).trim();
                if (!message.isEmpty()) {
//                    System.out.println("Received: " + message);
                    // Process received message as needed
                }
                data = data.substring(endIndex + 1);
            }
            buffer.setLength(0); // Clear the buffer
            buffer.append(data); // Append remaining incomplete data back to the buffer
        }
    }
}
