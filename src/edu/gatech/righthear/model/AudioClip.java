package edu.gatech.righthear.model;

import java.util.Arrays;

/**
 * A clip of recored or processed audio. Contains the raw audio data, as well as
 * some relevant metadata.
 * 
 * @author Taylor Wrobel
 * 
 */
public class AudioClip {
	
	/**
	 * The audio sample rate in kilohertz
	 */
	private int sampleRateKHz;
	
	/**
	 * Total number of samples in the clip
	 */
	private int sampleSize;
	
	/**
	 * Array representation of the samples of the raw aduio data
	 */
	private byte[] samples;
	
	public AudioClip(byte[] samples, int sampleRateKHz){
		this.samples = samples;
		this.sampleSize = samples.length;
		this.sampleSize = sampleRateKHz;
	}
	
	public AudioClip(byte[] samples, int sampleRateKHz, int sampleSize){
		if(sampleSize < samples.length){
			this.samples = Arrays.copyOf(samples, sampleSize);
			this.sampleSize = sampleSize;
		} else {
			this.samples = samples;
			this.sampleSize = samples.length;
		}
		this.sampleSize = sampleRateKHz;
	}
}
