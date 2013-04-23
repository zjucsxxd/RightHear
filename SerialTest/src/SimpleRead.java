
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jssc.SerialPort;
import jssc.SerialPortException;


public class SimpleRead {

    public static void main(String[] args) throws SerialPortException, IOException, InterruptedException {
//        SerialPort port = new SerialPort("/dev/tty.usbmodemfd121");
//    	SerialPort port =  new SerialPort("/dev/tty.usbmodem12341");
//        SerialPort port =  new SerialPort("/dev/tty.LauszussArduino-TKJSP");
        SerialPort port =  new SerialPort("/dev/tty.DuinoHearYou-TKJSP");
  	
        
//        port.closePort();
        
        do {
            try {
                port.openPort();
                break;
            } catch (SerialPortException e) {
                if (e.getExceptionType().equals(SerialPortException.TYPE_PORT_BUSY)) {
                    System.out.println("Trying again...");
                    continue;
                }
                throw e;
            }
        } while(true);
        System.out.println("Opened? " + port.isOpened());
        System.out.println("Params set? " + port.setParams(115200, 8, 1, 0));
        
//        ProcessingAdapter adapter = new ProcessingAdapter();
//        OutputStream toProcess = adapter.getStreamToProcessor();
//        
//        FileOutputStream fos = new FileOutputStream("out_ta_2");
//        new Thread(new StreamPrinter(adapter.getDetectedWordsStream())).start();
        
        int ii = 0;
        while (true) {
            //wait to make sure we have at least a frame
//            if (port.getInputBufferBytesCount() < 256) {
////                Thread.sleep(1);
//                continue;
//            }
            int toRead = port.getInputBufferBytesCount();
            if (toRead > 0) {
                System.out.println(String.format("%d: Data Waiting: %d", System.currentTimeMillis(), toRead));
                byte[] bytes = port.readBytes(toRead);
    //            applySilence(bytes);
    //            toProcess.write(bytes);
                System.out.println(new String(bytes, 0, toRead));
    //            fos.write(bytes);
    //            if ((++ii % 100) == 0) {
    //                port.writeInt('A');
    //                ii = 0;
    //            }
            } else {
                Thread.sleep(1000);
                port.writeByte((byte) 0xFF);
                port.writeByte((byte) 0xFE);
            }
        }
    }

    //TODO: smooth with simple moving average
    private static void applySilence(byte[] bytes) {
        for (int ii = 0; ii < bytes.length; ii++) {
            if (bytes[ii] < -120 || bytes[ii] > 119) {
                bytes[ii] = 127;
            }
        }
    }
}