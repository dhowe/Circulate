import java.util.Arrays;

import processing.core.PApplet;
import processing.sound.*;

public class SampleFromInput extends PApplet {
	AudioIn input;
	Waveform waveform;
	AudioSample sample;

	// allocate a buffer for up to a 10 second sample
	int maxSampleSize = 44100 * 10;

	public void settings() {
		size(100, 400);
	}

	public void setup() {
		noStroke();
		input = new AudioIn(this, 0);
		waveform = new Waveform(this, maxSampleSize);
	}

	public void keyPressed() {
		if (keyCode == 32) { // start record
			waveform.input(input);
		}
		if (keyCode == 10) { // stop record
			
			float[] samples = waveform.analyze();
			int startIndexOfRecordedSample = maxSampleSize
					- waveform.getLastAnalysisOffset();
			
			// if we recorded for >= 10 seconds this index will be 0, otherwise it will be
			// later into the float[] array
			sample = new AudioSample(this, Arrays.copyOfRange(samples, startIndexOfRecordedSample, maxSampleSize));
		}
	}

	public static void main(String[] args) {
		PApplet.main(SampleFromInput.class.getName());
	}
}
