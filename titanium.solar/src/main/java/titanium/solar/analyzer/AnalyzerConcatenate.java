package titanium.solar.analyzer;

import java.util.ArrayList;

public class AnalyzerConcatenate implements IAnalyzer
{

	private ArrayList<IAnalyzer> analyzers = new ArrayList<>();

	public AnalyzerConcatenate add(IAnalyzer analyzer)
	{
		analyzers.add(analyzer);
		return this;
	}

	@Override
	public void accept(double[] buffer, int length)
	{
		for (IAnalyzer analyzer : analyzers) {
			analyzer.accept(buffer, length);
		}
	}

}
