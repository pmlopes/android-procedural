package com.jetdrone.and2d;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioMixer implements Runnable {
	
	private final Thread audioThread;
	
	private final AudioTrack device;
	private final short[] audioData;
	
	private final float[][] audioBuffer;
	private final int[] bufferState;
	private final int[] bufferPos;
	private final int minBufSize;
	
	private boolean run = true;

	public AudioMixer(int sampleRateInHz, int channels) {
		device = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, sampleRateInHz, AudioTrack.MODE_STREAM);
		audioBuffer = new float[channels + 1][];
		bufferState = new int[channels];
		bufferPos = new int[channels];
		minBufSize = AudioTrack.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
		audioData = new short[minBufSize];
		
		audioThread = new Thread(this);
		audioThread.start();
	}
	
	public void setAudioData(int channel, float[] audioData) {
		audioBuffer[channel + 1] = audioData;
	}
	
	public void play(int channel) {
		bufferState[channel] = 1;
		bufferPos[channel] = 0;		
	}

	public void loop(int channel) {
		bufferState[channel] = -1;
	}
	
	public void stop(int channel) {
		bufferState[channel] = 0;
	}

	public void stop() {
		for(int i=0; i<bufferState.length; i++) {
			bufferState[i] = 0;
		}
	}
	
	@Override
	public void run() {
		device.play();
		while(run) {
			for(int i=0; i<audioBuffer[0].length; i++) {
				audioBuffer[0][i] = 0f;
			}
			for(int i=1; i<audioBuffer.length; i++) {
				if(bufferState[i] != 0) {
					for(int j=0; j<audioData.length; j++) {
						if(bufferPos[i] == audioBuffer[i].length) {
							bufferPos[i] = 0;
							bufferState[i]--;
							if(bufferState[i] == 0) {
								break;
							} else if(bufferState[i] < 0) {
								bufferState[i] = -1;
							}
						}
						audioData[j] = (short) ((audioBuffer[0][j] + audioBuffer[i][bufferPos[i]]) - (audioBuffer[0][j] * audioBuffer[i][bufferPos[i]]) * Short.MAX_VALUE);
						bufferPos[i]++;
					}
				}
			}
			
			device.write(audioData, 0, audioData.length);
		}
		device.release();
	}
}
