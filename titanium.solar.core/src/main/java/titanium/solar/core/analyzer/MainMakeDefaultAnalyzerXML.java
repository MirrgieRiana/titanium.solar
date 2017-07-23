package titanium.solar.core.analyzer;

import java.io.File;
import java.net.MalformedURLException;

import com.thoughtworks.xstream.XStream;

import titanium.solar.libs.analyze.IFilterProvider;
import titanium.solar.libs.analyze.filters.FilterProviderConcatenate;
import titanium.solar.libs.analyze.filters.FilterProviderContinuous;
import titanium.solar.libs.analyze.filters.FilterProviderCorrelation;
import titanium.solar.libs.analyze.filters.FilterProviderExtractMountain;
import titanium.solar.libs.analyze.filters.FilterProviderMul;
import titanium.solar.libs.analyze.filters.FilterProviderQOM;
import titanium.solar.libs.analyze.mountainlisteners.MountainListenerProviderChain;
import titanium.solar.libs.analyze.waveformproviders.WaveformProviderLinkFile;

public class MainMakeDefaultAnalyzerXML
{

	public static void main(String[] args) throws MalformedURLException
	{
		double volume = 0.02;
		int offsetShort = 45;
		int offsetLong = 80;
		int threshold = 10;
		int firstThreshold = 30;
		int width = 7;
		int maxXError = 3;
		int timeout = 100;

		IFilterProvider filterProvider = new FilterProviderConcatenate()
			.add(new FilterProviderCorrelation(new WaveformProviderLinkFile(new File("waveform.csv"))))
			.add(new FilterProviderContinuous(offsetShort, offsetLong))
			.add(new FilterProviderQOM())
			.add(new FilterProviderMul(volume))
			.add(new FilterProviderExtractMountain(width, threshold, timeout)
				.addMountainListenerProvider(new MountainListenerProviderChain(offsetShort, offsetLong, firstThreshold, timeout, maxXError)
					.addChainListenerProvider(new ChainListenerProviderOutput())));

		System.out.println(new XStream().toXML(filterProvider));
	}

}
