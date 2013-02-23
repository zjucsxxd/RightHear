/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package audioStreamTranscriber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;

import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 * A class that uses Sphinx to translate a given <code>AudioInputStream</code>
 * into an <code>InputStream</code> of the detected words.
 */
public class StreamTranscriber {

	/**
	 * Transcribes the given <code>AudioInputStream</code> into an
	 * <code>InputStream</code> of the transcribed words. The list of words it
	 * can detect are currently pre-determined, and stroed in the grammar file
	 * (detectWords.gram) accompanying this class.
	 * 
	 * Detection will continue until the source audio stops, at which time the
	 * produced input stream will end as well. Kicks the detection and stream
	 * management into a new thread, allowing for this code to be placed in-line
	 * and the streams to be used as usual.
	 * 
	 * @param ais
	 *            The audio stream to be processed
	 * @return A stream of detected words, as they are detected.
	 */
	public static InputStream transcribeStream(AudioInputStream ais) {

		// Location of Config file
		URL configURL = StreamTranscriber.class.getResource("config.xml");

		// Configuration manager and recognizer setup
		ConfigurationManager cm = new ConfigurationManager(configURL);
		final Recognizer recognizer = (Recognizer) cm.lookup("recognizer");

		/* allocate the resource necessary for the recognizer */
		recognizer.allocate();

		// configure the audio input for the recognizer
		AudioFileDataSource dataSource = (AudioFileDataSource) cm
				.lookup("audioFileDataSource");
		dataSource.setInputStream(ais, null);

		// Create connected piped input and output streams, for streaming
		// detected words.
		PipedInputStream inStream = new PipedInputStream();
		OutputStream outPipe;
		try {
			outPipe = new PipedOutputStream(inStream);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
		final OutputStreamWriter out = new OutputStreamWriter(outPipe);

		// Kick detection into it's own thread, to allow for detection in concurrency with rest of program
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Result result;
					// Loop until last utterance in the audio file has been decoded, in
					// which case the recognizer will return null
					while ((result = recognizer.recognize()) != null) {
						String resultText = result.getBestResultNoFiller();
						// Write detected word to output stream
						out.write(resultText + "\n");
						out.flush();
					}
					// Audio ended, close stream
					out.flush();
					out.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}).start();

		return inStream;
	}
}
