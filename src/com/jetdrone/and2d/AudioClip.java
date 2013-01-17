package com.jetdrone.and2d;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioClip {
	
	private AudioTrack track;
	private int sampleRateInHz = 22050;
	private short[] audioData;
	
	public AudioClip(int sampleRateInHz) {
		this.sampleRateInHz = sampleRateInHz;
	}
	
	public AudioClip(int sampleRateInHz, short[] audioData) {
		this.sampleRateInHz = sampleRateInHz;
		setData(audioData);
	}
	
	public void setData(short[] audioData) {
		
		if(track != null) {
			track.release();
		}
		
    	final int bufSize = Math.max(sampleRateInHz, audioData.length);
    	this.audioData = new short[bufSize];
    	
		track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, bufSize, AudioTrack.MODE_STATIC);
		
		if(track.getState() == AudioTrack.STATE_NO_STATIC_DATA) {
			System.arraycopy(audioData, 0, this.audioData, 0, audioData.length);
			track.write(audioData, 0, audioData.length);
		}
	}
	
	public void play() {
		if(track != null) {
			track.play();
			track.stop();
			track.reloadStaticData();
		}
	}
	
	public void release() {
		if(track != null) {
			track.release();
		}
	}
	
	@Override
	public void finalize() throws Throwable {
		release();
		super.finalize();
	}
}
