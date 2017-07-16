package titanium.solar.analyzer.analyzers;

import titanium.solar.analyzer.IAnalyzer;

public class AnalyzerDiff implements IAnalyzer
{

	private double prevX;

	@Override
	public void accept(double[] buffer, int length)
	{
		for (int i = 0; i < length; i++) {
			buffer[i] = buffer[i] - prevX;
			prevX = buffer[i];
		}
	}

}
