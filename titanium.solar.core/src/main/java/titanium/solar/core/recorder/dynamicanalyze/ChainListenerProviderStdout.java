package titanium.solar.core.recorder.dynamicanalyze;

import titanium.solar.libs.analyze.mountainlisteners.IChainListener;
import titanium.solar.libs.analyze.mountainlisteners.IChainListenerProvider;

public class ChainListenerProviderStdout implements IChainListenerProvider
{

	private double samplesPerSecond;

	public ChainListenerProviderStdout(double samplesPerSecond)
	{
		this.samplesPerSecond = samplesPerSecond;
	}

	@Override
	public IChainListener createChainListener()
	{
		return chain -> {
			System.out.println(chain.toString(samplesPerSecond));
		};
	}

}
