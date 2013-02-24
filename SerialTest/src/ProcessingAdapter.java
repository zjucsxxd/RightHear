import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import audioStreamTranscriber.StreamTranscriber;
import audioStreamTranscriber.sampleDriver.StreamConverter;


public class ProcessingAdapter {

	PipedOutputStream toProcessor;
	PipedInputStream forSphinx;
	InputStream result;
	
	public ProcessingAdapter() throws IOException{
		forSphinx = new PipedInputStream();
		toProcessor = new PipedOutputStream(forSphinx);
		result = null;
	}
	
	public OutputStream getStreamToProcessor(){
		return toProcessor;
	}
	
	public InputStream getDetectedWordsStream() throws IOException{
		if(result == null){
			AudioFormat pcmFormat = new AudioFormat(9615f, 8, 2,
					true, false);
			AudioInputStream ais = new AudioInputStream(StreamConverter.unsignedToSigned(forSphinx),
					pcmFormat, Long.MAX_VALUE);
			result = StreamTranscriber.transcribeStream(ais);
		}
		return result;
	}
	
}
