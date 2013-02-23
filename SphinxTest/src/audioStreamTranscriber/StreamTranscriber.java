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
import javax.sound.sampled.UnsupportedAudioFileException;

import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 * A simple example that shows how to transcribe a continuous audio file that
 * has multiple utterances in it.
 */
public class StreamTranscriber {

	public static InputStream transcribeStream(AudioInputStream ais)
			throws IOException, UnsupportedAudioFileException {

		URL configURL = StreamTranscriber.class.getResource("config.xml");

		ConfigurationManager cm = new ConfigurationManager(configURL);
		final Recognizer recognizer = (Recognizer) cm.lookup("recognizer");

		/* allocate the resource necessary for the recognizer */
		recognizer.allocate();

		// configure the audio input for the recognizer
		AudioFileDataSource dataSource = (AudioFileDataSource) cm
				.lookup("audioFileDataSource");
		dataSource.setInputStream(ais, null);

		PipedInputStream inStream = new PipedInputStream();
		OutputStream outPipe = new PipedOutputStream(inStream);
		final OutputStreamWriter out = new OutputStreamWriter(outPipe);

		// Loop until last utterance in the audio file has been decoded, in
		// which case the recognizer will return null.
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Result result;
					while ((result = recognizer.recognize()) != null) {

						String resultText = result.getBestResultNoFiller();
						out.write(resultText + "\n");
						out.flush();
					}
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
