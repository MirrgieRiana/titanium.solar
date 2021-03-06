package titanium.solar.core.analyzer;

import java.io.PrintStream;

import titanium.solar.libs.analyze.mountainlisteners.IChainListener;
import titanium.solar.libs.analyze.mountainlisteners.IChainListenerProvider;

public class ChainListenerProviderOutput implements IChainListenerProvider
{

	public static PrintStream out;

	private double samplesPerSecond;

	public ChainListenerProviderOutput(double samplesPerSecond)
	{
		this.samplesPerSecond = samplesPerSecond;
	}

	@Override
	public IChainListener createChainListener()
	{
		return chain -> {
			if (chain.isValid()) out.println(chain.toString(samplesPerSecond));
		};
	}

}
