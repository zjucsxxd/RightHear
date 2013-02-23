package audioStreamTranscriber.sampleDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.IllegalFormatException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import audioStreamTranscriber.StreamTranscriber;


public class StreamTranscriberDriver {

	public static void main(String[] args) throws IOException,
			UnsupportedAudioFileException {
		URL audioURL;

		if (args.length > 0) {
			audioURL = new File(args[0]).toURI().toURL();
		} else {
			// audioURL =Transcriber.class.getResource("10001-90210-01803.wav"); // Base Example
			// audioURL = Transcriber.class.getResource("out_2_a"); // Our Data
			audioURL = StreamTranscriberDriver.class.getResource("ritaTest.wav"); // Test Data
		}
		
		// Base Example
		AudioInputStream ais;
		
		try{
			ais = AudioSystem.getAudioInputStream(new File(audioURL.getFile()));
		} catch(IllegalFormatException ife){
			AudioFormat pcmFormat = new AudioFormat(9600f, 8, 1, false, false);
	        ais = new AudioInputStream(new FileInputStream(
					audioURL.getFile()), pcmFormat, 2000000);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(StreamTranscriber.transcribeStream(ais)));
		String line = null;
		while((line = reader.readLine()) != null){
			System.out.println(line);
		}
		System.out.println("--Done Processing--");
	}
}
