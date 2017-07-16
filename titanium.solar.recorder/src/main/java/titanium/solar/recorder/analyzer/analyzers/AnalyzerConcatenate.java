package titanium.solar.recorder.analyzer.analyzers;

import java.util.ArrayList;

import titanium.solar.recorder.analyzer.IAnalyzer;

public class AnalyzerConcatenate implements IAnalyzer
{

	private ArrayList<IAnalyzer> analyzers = new ArrayList<>();

	@Override
	public void accept(double[] buffer, int length)
	{
		for (IAnalyzer analyzer : analyzers) {
			analyzer.accept(buffer, length);
		}
	}

	public AnalyzerConcatenate add(IAnalyzer analyzer)
	{
		analyzers.add(analyzer);
		return this;
	}

}
