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

	private static final float SAMPLE_RATE = 9615f;
	private static final int CHANNELS = 2;
	private static final String samplesLocation = "audioSamples/";

	public static void main(String[] args) throws IOException,
			UnsupportedAudioFileException {
		URL audioURL;
		String filename;

		// Set the file to be processed. The words to be detected need to go
		// into detectWords.gram
		if (args.length > 0) {
			audioURL = new File(args[0]).toURI().toURL();
		} else {
			// filename = "10001-90210-01803.wav"; // Base example
			// filename = "ritaTest.wav"; // More clear test data
//			 filename = "out_ta_1";
			 filename = "hello_rita_9615_8bit_stereo.raw";
//			 filename = "hello_rita_9615_8bit_stereo_16raw.raw";
//			filename = "hello_rita_9615_8bit_stereo.wav";
			audioURL = StreamTranscriberDriver.class
					.getResource(samplesLocation + filename);
		}

		// Creating of the input stream object
		AudioInputStream ais;

		ais = getAudioStream(audioURL);

		System.out.println(ais.getFormat());

//		playStream(ais);
//		ais = getAudioStream(audioURL); // If not playing stream, this reloading of the stream is unnecessary

		// Create a buffered reader from the input stream the transcriber
		// returns
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				StreamTranscriber.transcribeStream(ais)));

		// Print results of the transcribing
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		System.out.println("--Done Processing--");
	}

	private static void playStream(AudioInputStream ais) {
		try {
			new StreamPlayer(ais).play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static AudioInputStream getAudioStream(URL audioURL) throws IOException{
		// Attempt to automagically create ais from the AudioSystem
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File(audioURL.getFile()));
			return ais;
		} catch (UnsupportedAudioFileException uafe) {
			System.out.println("Doing manual stream reading.");
			// If that doesn't work, it's probably our unprocessed audio from
			// the arduino.
			AudioFormat pcmFormat = new AudioFormat(SAMPLE_RATE, 8, CHANNELS,
					true, false);
			return new AudioInputStream(StreamConverter.unsignedToSigned(new FileInputStream(audioURL.getFile())),
					pcmFormat, new File(audioURL.getFile()).length());
		}
	}
}
