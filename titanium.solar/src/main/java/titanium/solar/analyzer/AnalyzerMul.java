package titanium.solar.analyzer;

public class AnalyzerMul implements IAnalyzer
{

	private double a;

	public AnalyzerMul(double a)
	{
		this.a = a;
	}

	@Override
	public void accept(double[] buffer, int length)
	{
		for (int i = 0; i < length; i++) {
			buffer[i] = buffer[i] * a;
		}
	}

}
