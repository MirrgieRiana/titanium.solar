package titanium.solar.libs.analyze.filters;

import mirrg.lithium.event.EventManager;
import mirrg.lithium.struct.Struct1;
import titanium.solar.libs.analyze.EventFilterControl;
import titanium.solar.libs.analyze.IFilter;
import titanium.solar.libs.analyze.IFilterProvider;

/**
 * 相関関数
 */
public class FilterProviderCorrelation implements IFilterProvider
{

	private IWaveformProvider waveformProvider;
	private int hotspot;

	public FilterProviderCorrelation(IWaveformProvider waveformProvider, int hotspot)
	{
		this.waveformProvider = waveformProvider;
		this.hotspot = hotspot;
	}

	@Override
	public IFilter createFilter(EventManager<EventFilterControl> eventManager)
	{
		double[] waveform = waveformProvider.createWaveform();
		double[] cache = new double[waveform.length];

		return new IFilter() {

			private int index = 0;

			@Override
			public void accept(double[] buffer, int length, Struct1<Double> offset)
			{
				offset.x += (waveform.length - 1) - hotspot;
				for (int i = 0; i < length; i++) {

					// キャッシュに入れる
					cache[index] = buffer[i];

					// キャッシュとサンプルデータの相関を計算
					long sum = 0;
					for (int j = 0; j < waveform.length; j++) {
						sum += waveform[j] * cache[(index + 1 + j) % waveform.length];
					}

					// バッファに戻す
					buffer[i] = sum;

					// キャッシュの使用位置更新
					index = (index + 1) % waveform.length;

				}
			}

		};
	}

}
