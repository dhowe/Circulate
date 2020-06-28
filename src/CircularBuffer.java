import processing.core.PApplet;
import processing.sound.*;

public class CircularBuffer extends PApplet {

	AudioIn input;
	Waveform waveform;
	float[] values, rms;
	int lastOffset, samplesPerPixel;

	public void settings() {
		size(800, 600);
	}

	public void setup() {

		Sound.list();
		input = new AudioIn(this);
		 
		int targetBufferSz = new Sound(this).sampleRate() * 3;
		samplesPerPixel = floor(targetBufferSz / (float) width);
		waveform = new Waveform(this, samplesPerPixel * width);
		values = new float[width];
		rms = new float[width];
		waveform.input(input);
		lastOffset = 0;
	}

	public void draw() {
		float[] samples = waveform.analyze();

		// calculate the # of new samples
		int offset = waveform.getLastAnalysisOffset();
		int newSamples = (offset - lastOffset);
		if (newSamples < 0) newSamples += samples.length;
		int shift = ceil((newSamples / (float) samples.length) * values.length);
		lastOffset = offset;

		// shift the array to the left
		values = shiftLeft(values, shift);
		rms = shiftLeft(rms, shift);

		// fill in the new values on the right
		for (int i = width - shift; i < width; i++) {
			values[i] = absAvg(samples, i);
			rms[i] = rmsAvg(samples, i);
		}

		// draw the values as rects
		background(245);
		noStroke();
		for (int i = 0; i < width; i++) {
			fill(49, 48, 205);
			float h = values[i] * height;
			rect(i, height / 2 - h / 2, 1, h);
			fill(98, 101, 222);
			h = rms[i] * height;
			rect(i, height / 2 - h / 2, 1, h);
		}
	}

	float rmsAvg(float[] samples, int startIdx) {
		float sumSq = 0;
		for (int j = 0; j < samplesPerPixel; j++) {
			float val = samples[startIdx * samplesPerPixel + j];
			sumSq += (val * val);
		}
		float mean = sumSq / samplesPerPixel;
		return (float) Math.sqrt(mean);
	}

	float absAvg(float[] samples, int startIdx) {
		float sum = 0;
		for (int j = 0; j < samplesPerPixel; j++) {
			sum += Math.abs(samples[startIdx * samplesPerPixel + j]);
		}
		return (sum * 2) / (float) samplesPerPixel;
	}

	float absMax(float[] samples, int startIdx) {
		float max = 0;
		for (int j = 0; j < samplesPerPixel; j++) {
			float val = Math.abs(samples[startIdx * samplesPerPixel + j]);
			if (val > max) max = val;
		}
		return max;
	}

	float[] shiftLeft(float[] arr, int shift) {
		float[] tmp = new float[arr.length];
		System.arraycopy(arr, shift, tmp, 0, arr.length - shift);
		return tmp;
	}

	public static void main(String[] args) {
		PApplet.main(CircularBuffer.class.getName());
	}
}
