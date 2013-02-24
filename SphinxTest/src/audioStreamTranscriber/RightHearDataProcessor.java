package audioStreamTranscriber;

import java.util.concurrent.LinkedBlockingQueue;

import jssc.SerialPort;
import edu.cmu.sphinx.frontend.BaseDataProcessor;
import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.DataEndSignal;
import edu.cmu.sphinx.frontend.DataProcessingException;
import edu.cmu.sphinx.frontend.DataStartSignal;
import edu.cmu.sphinx.frontend.DoubleData;
import edu.cmu.sphinx.frontend.util.DataUtil;

/**
 * A data processor that takes audio data from the RightHear wearable device, via
 * the serial port
 * 
 * @author thejenix
 *
 */
public class RightHearDataProcessor extends BaseDataProcessor {

    private Thread recordingThread;
    private boolean threadRunning;
    private SerialPort port;
    private LinkedBlockingQueue<Data> audioList;
    private boolean utteranceEndReached;
    private String stereoToMono = "average";

    private static final int SAMPLE_RATE = 9615;
    private static final int FRAME_SIZE = 256;
    private static final int CHANNELS = 2;


    @Override
    public void initialize() {
        super.initialize();
        try {
//      SerialPort port = new SerialPort("/dev/tty.usbmodemfd121");
            this.port =  new SerialPort("/dev/tty.usbmodem12341");
            this.audioList = new LinkedBlockingQueue<Data>();
    
            utteranceEndReached = false;
            this.port.openPort();
            System.out.println("Opened? " + this.port.isOpened());
            System.out.println("Params set? " + this.port.setParams(115200, 8, 1, 0));
    //        
    //        ProcessingAdapter adapter = new ProcessingAdapter();
    //        OutputStream toProcess = adapter.getStreamToProcessor();
    //        
    //        FileOutputStream fos = new FileOutputStream("out_ta_2");
    //        new Thread(new StreamPrinter(adapter.getDetectedWordsStream())).start();
    //        
            this.recordingThread = new Thread(new RecordRunner());  
            this.threadRunning   = false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    
    public void startRecording() {
        this.threadRunning = true;
        this.recordingThread.start();
    }
    
    
    public void stopRecording() {
        this.threadRunning = false;
        try {
            this.recordingThread.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
            //nothing to do here
        }
    }

    @Override
    public Data getData() throws DataProcessingException {
        getTimer().start();

        Data output = null;

        if (!utteranceEndReached) {
            try {
                output = audioList.take();
            } catch (InterruptedException ie) {
                throw new DataProcessingException("cannot take Data from audioList", ie);
            }
            if (output instanceof DataEndSignal) {
                utteranceEndReached = true;
            }
        }

        getTimer().stop();

        return output;
    }
    
    private class RecordRunner implements Runnable {

        @Override
        public void run() {
            try {
                int ii = 0;
                audioList.add(new DataStartSignal(SAMPLE_RATE));
    
                long totalSamplesRead = 0;
                while (threadRunning) {
                    //wait to make sure we have at least a frame
                    if (port.getInputBufferBytesCount() < FRAME_SIZE) {
    //                    Thread.sleep(1);
                        continue;
                    }
    //                System.out.println("Data Waiting: " + port.getInputBufferBytesCount());
                    long collectTime = System.currentTimeMillis();
                    byte[] data = port.readBytes(FRAME_SIZE);
                    long firstSampleNumber = totalSamplesRead / CHANNELS;
                    totalSamplesRead += FRAME_SIZE;
                    applySilence(data);
                    convertToSigned(data);
                    double [] samples = DataUtil.bytesToValues
                                (data, 0, data.length, 1, true);
    
                    samples = convertStereoToMono(samples, CHANNELS);
                    audioList.add(new DoubleData
                            (samples, SAMPLE_RATE,
                                    collectTime, firstSampleNumber));
    //                toProcess.write(bytes);
    //                fos.write(bytes);
    ////                if ((++ii % 100) == 0) {
    ////                    port.writeInt('A');
    ////                    ii = 0;
    ////                }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void convertToSigned(byte[] bytes) {
            for (int ii = 0; ii < bytes.length; ii++) {
                bytes[ii] -= 127;
            }
        }
        /**
         * Converts stereo audio to mono.
         *
         * @param samples  the audio samples, each double in the array is one sample
         * @param channels the number of channels in the stereo audio
         */
        private double[] convertStereoToMono(double[] samples, int channels) {
            assert (samples.length % channels == 0);
            double[] finalSamples = new double[samples.length / channels];
            if (stereoToMono .equals("average")) {
                for (int i = 0, j = 0; i < samples.length; j++) {
                    double sum = samples[i++];
                    for (int c = 1; c < channels; c++) {
                        sum += samples[i++];
                    }
                    finalSamples[j] = sum / channels;
                }
            } else if (stereoToMono.equals("selectChannel")) {
//                for (int i = selectedChannel, j = 0; i < samples.length;
//                     i += channels, j++) {
//                    finalSamples[j] = samples[i];
//                }
            } else {
                throw new Error("Unsupported stereo to mono conversion: " +
                        stereoToMono);
            }
            return finalSamples;
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
