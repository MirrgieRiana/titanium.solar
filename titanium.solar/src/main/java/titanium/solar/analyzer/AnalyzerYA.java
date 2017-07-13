package titanium.solar.analyzer;

public class AnalyzerYA implements IAnalyzer
{

	private double c;
	private double b;

	@Override
	public void accept(double[] buffer, int length)
	{
		for (int i = 0; i < length; i++) {

			double a = buffer[i];
			buffer[i] = -Math.min(((a - b) - (b - c)), 0) * Math.max(b, 0);

			c = b;
			b = a;

		}
	}

}
