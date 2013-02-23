
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import jssc.SerialPort;
import jssc.SerialPortException;


public class SimpleRead {

    public static void main(String[] args) throws SerialPortException, IOException {
//        SerialPort port = new SerialPort("/dev/tty.usbmodemfd121");
        SerialPort port = new SerialPort("/dev/tty.usbmodem12341");
        

        port.openPort();
        System.out.println("Opened? " + port.isOpened());
        System.out.println("Params set? " + port.setParams(115200, 8, 1, 0));
        FileOutputStream fos = new FileOutputStream("out_ta_1");
        int ii = 0;
        while (true) {
            //wait to make sure we have at least a frame
            if (port.getInputBufferBytesCount() < 256) {
                continue;
            }
            System.out.println("Data Waiting: " + port.getInputBufferBytesCount());
            byte[] bytes = port.readBytes(256);
            applySilence(bytes);
            fos.write(bytes);
            if ((++ii % 100) == 0) {
                port.writeInt('A');
                ii = 0;
            }
        }
    }

    //TODO: smooth with simple moving average
    private static void applySilence(byte[] bytes) {
        for (int ii = 0; ii < bytes.length; ii++) {
            if (bytes[ii] < -127 || bytes[ii] > 125) {
                bytes[ii] = 127;
            }
        }
    }
}