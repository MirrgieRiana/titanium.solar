package titanium.solar.core.recorder.analyze;

import titanium.solar.core.analyzer.MainAnalyzer;
import titanium.solar.libs.analyze.mountainlisteners.IChainListener;
import titanium.solar.libs.analyze.mountainlisteners.IChainListenerProvider;

public class ChainListenerProviderStdout implements IChainListenerProvider
{

	@Override
	public IChainListener createChainListener()
	{
		return chain -> {
			System.out.println(chain.toString(MainAnalyzer.samplePerSecond));
		};
	}

}
