package titanium.solar.recorder.analyzer.analyzers;

import org.apache.commons.math3.util.FastMath;

import titanium.solar.recorder.analyzer.IAnalyzer;

/**
 * √(x * a)
 */
public class AnalyzerQOM implements IAnalyzer
{

	private double prevX;
	private double prevPrevX;

	@Override
	public void accept(double[] buffer, int length)
	{
		for (int i = 0; i < length; i++) {
			double x = buffer[i];
			double dd = (x - prevX) - (prevX - prevPrevX);
			buffer[i] = Math.sqrt(FastMath.max(prevX, 0) * -FastMath.min(dd, 0));
			prevPrevX = prevX;
			prevX = x;
		}
	}

}
