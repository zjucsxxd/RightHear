package audioStreamTranscriber.sampleDriver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import audioStreamTranscriber.StreamTranscriber;

public class StreamTranscriberDriver {

	public static void main(String[] args) throws IOException,
			UnsupportedAudioFileException {
		URL audioURL;
		String filename;

		// Set the file to be processed.  The words to be detected need to go into detectWords.gram
		if (args.length > 0) {
			audioURL = new File(args[0]).toURI().toURL();
		} else {
			filename = "10001-90210-01803.wav"; // Base example
//			filename = "out_2_a"; // Our Sample Data
//			filename = "ritaTest.wav"; // More clear test data
			audioURL = StreamTranscriberDriver.class.getResource(filename);
		}

		// Creating of the input stream object
		AudioInputStream ais;

		// Attempt to automagically create ais from the AudioSystem
		try {
			ais = AudioSystem.getAudioInputStream(new File(audioURL.getFile()));
		} catch (UnsupportedAudioFileException uafe) {
			// If that doesn't work, it's probably our unprocessed audio from
			// the arduino.
			AudioFormat pcmFormat = new AudioFormat(9600f, 8, 1, false, false);
			ais = new AudioInputStream(new FileInputStream(audioURL.getFile()),
					pcmFormat, 2000000);
		}

		// Create a buffered reader from the input stream the transcriber returns
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				StreamTranscriber.transcribeStream(ais)));
		
		// Print results of the transcribing
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		System.out.println("--Done Processing--");
	}
}
