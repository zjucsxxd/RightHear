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

import jssc.SerialPortException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

/**
 *
 * Based on the Sphinx-4 HelloWorld example, modified to use the RightHearDataProcessor.
 * 
 */
public class HelloTaylor {

    public static void main(String[] args) throws SerialPortException {
        ConfigurationManager cm;

        if (args.length > 0) {
            cm = new ConfigurationManager(args[0]);
        } else {
            cm = new ConfigurationManager(
                    HelloTaylor.class.getResource("hellotaylor.config.xml"));
        }

        Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
        recognizer.allocate();

        // start the microphone or exit if the programm if this is not possible
        RightHearDataProcessor processor = (RightHearDataProcessor) cm
                .lookup("microphone");
        processor.startRecording();
        // if (!processor.startRecording()) {
        // System.out.println("Cannot start microphone.");
        // recognizer.deallocate();
        // System.exit(1);
        // }

        System.out
                .println("Say: (Hello | Tiffany | Stop | Hold on | Halt | Hey)");

        // loop the recognition until the programm exits.
        while (true) {
            System.out.println("Start speaking. Press Ctrl-C to quit.\n");

            Result result = recognizer.recognize();

            if (result != null) {
                String resultText = result.getBestFinalResultNoFiller();
                System.out.println("You said: " + resultText + '\n');
                //send an alert back to the RightHear device
                if (resultText != null && (resultText.equals("Hold on") | resultText.equals("Stop"))) {
                    processor.sendAlert();
                }
            } else {
                System.out.println("I can't hear what you said.\n");
            }
        }
    }
}
