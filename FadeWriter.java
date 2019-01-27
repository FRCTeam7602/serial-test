import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Random;
import java.util.TooManyListenersException;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class FadeWriter implements Runnable, SerialPortEventListener {
    public static final int BAUD_RATE = 57600;
    public static final int MAX_BLINKS = 5;
    public static final String PORT = "/dev/ttyACM0";

    private static CommPortIdentifier portId;
    private static Enumeration portList;

    private InputStream inputStream;
    private SerialPort serialPort;
    private Thread readThread;

    private static String messageString;
    private static OutputStream outputStream;
    private static boolean outputStreamEmptyFlag = false;

    public static void main(String[] args) {
        System.out.println("Looking for port " + PORT);

        boolean portFound = false;
        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                if (portId.getName().equals(PORT)) {
                    System.out.println("Found port " + PORT);
                    portFound = true;
                    FadeWriter writer = new FadeWriter();
                }
            }
        }
        if (!portFound) {
            System.out.println("Port " + PORT + " NOT found.");
        }
    }

    public void initWriteToPort() {
        try {
            outputStream = serialPort.getOutputStream();
        } catch(IOException e) {
            System.err.println("Error opening output stream:");
            System.err.println(e.toString());
        }
        try {
            serialPort.notifyOnOutputEmpty(true);
        } catch(Exception e) {
            System.err.println("Error setting event notification: ");
            System.err.println(e.toString());
            System.exit(-1);
        }
    }

    public void chooseRandomNumber() {
        Random random = new Random();
        int number = random.nextInt(MAX_BLINKS) + 1;
        messageString = String.valueOf(number);
    }

    public void writeToPort() {
        System.out.println("Writing " + messageString + " to " + PORT);
        try {
            outputStream.write(messageString.getBytes());
        } catch(IOException e) {
            System.err.println("Error writing to output stream:");
            System.err.println(e.toString());
        }
    }

    public FadeWriter() {
        try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
        } catch(PortInUseException e) {
            System.err.println("Error opening port:");
            System.err.println(e.toString());
            System.exit(-1);
        }
        try {
            inputStream = serialPort.getInputStream();
        } catch(IOException e) {
            System.err.println("Error opening input stream:");
            System.err.println(e.toString());
        }
        try {
            serialPort.addEventListener(this);
        } catch(TooManyListenersException e) {
            System.err.println("Error adding event listener:");
            System.err.println(e.toString());
        }
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch(UnsupportedCommOperationException e) {
            System.err.println("Error setting serial params:");
            System.err.println(e.toString());
        }

        readThread = new Thread(this);
        readThread.start();
    }

    public void run() {
        initWriteToPort();
        try {
            while(true) {
                chooseRandomNumber();
                writeToPort();
                Thread.sleep((MAX_BLINKS + 1) * 1000 /* millis in second */);
            }
        } catch(InterruptedException e) {
            System.out.println("... stopping ...");
        }
    }

    public void serialEvent(SerialPortEvent event) {
        System.out.println("... " + event.getEventType());
        if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            byte[] readBuffer = new byte[20];
            try {
                while (inputStream.available() > 0) {
                    int numBytes = inputStream.read(readBuffer);
                }
                String result = new String(readBuffer);
                System.out.println("Read: " + result);
            } catch(IOException e) {
                System.err.println("Error reading from input stream:");
                System.err.println(e.toString());
            }
        }
    }
}
