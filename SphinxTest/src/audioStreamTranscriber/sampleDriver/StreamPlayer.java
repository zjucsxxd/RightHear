package audioStreamTranscriber.sampleDriver;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

public class StreamPlayer implements LineListener {

	private volatile boolean stillPlaying;
	private AudioInputStream ais;
	
	public StreamPlayer(AudioInputStream ais){
		this.ais = ais;
	}
	
	public void play() throws IOException,
			LineUnavailableException {
		stillPlaying = true;
		Clip clip = AudioSystem.getClip();
		clip.addLineListener(this);
		clip.open(ais);
		clip.start();
		
		while (stillPlaying) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {
				System.out.println("Timing thread interrupted.");
			}
		}
	}

	@Override
	public void update(LineEvent le) {
		if (le.getType().equals(LineEvent.Type.STOP)) {
			stillPlaying = false;
		}
	}
}
