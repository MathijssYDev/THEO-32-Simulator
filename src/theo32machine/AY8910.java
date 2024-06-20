package theo32machine;

import java.util.Arrays;
import com.fazecast.jSerialComm.SerialPort;

public class AY8910 extends Thread {
    THEO32 cpu;
    static SerialPort comPort;

    public AY8910(THEO32 cpu) {
        this.cpu = cpu;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> comPort.closePort()));

        // Initialize serial port
        comPort = SerialPort.getCommPorts()[cpu.COMport];
        comPort.setComPortParameters(115200, 8, 1, 0);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);

        if (comPort.openPort()) {
            System.out.println("Port is open.");
        } else {
            System.out.println("Failed to open port.");
        }
    }


    public void run() {
        while (true) {
            for (Integer[] v : cpu.AY8910_Requests) {
                boolean BC1 = v[0] == 1;
                boolean BDIR = v[1] == 1;

                if (!BC1 && !BDIR) {
                    setInactive();
                } else if (BC1 && BDIR) {
                    setLatch(v[2]);
                } else if (BDIR) {
                    setWrite(v[2]);
                }
                cpu.AY8910_Requests.remove(v);
            }

        }
//        comPort.closePort();
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
//        while (comPort.bytesAvailable() > 0) {
//
//        }
        String command = message.substring(0, 1);
        int value = Integer.parseInt(message.substring(1));

        char encodedValue = (char) (value);
        String messageToSend = command + String.format("%02x", encodedValue & 0xff).toUpperCase();

        byte[] messageBytes = messageToSend.getBytes();

        // System.out.println("0x" + String.format("%02x", encodedValue & 0xff));
        comPort.writeBytes(messageBytes, messageBytes.length);
        comPort.flushIOBuffers(); // Ensure the message is sent immediately

        try {
            Thread.sleep(1); // Reduce the delay to 100 ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
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
                    System.out.println("Received: " + message);
                }
                data = data.substring(endIndex + 1);
            }
            buffer.setLength(0); // Clear the buffer
            buffer.append(data); // Append remaining incomplete data back to the buffer
        }
    }
}
