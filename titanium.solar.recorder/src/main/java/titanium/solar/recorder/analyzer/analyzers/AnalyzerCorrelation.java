package titanium.solar.recorder.analyzer.analyzers;

import titanium.solar.recorder.analyzer.IAnalyzer;

/**
 * 相関関数
 */
public class AnalyzerCorrelation implements IAnalyzer
{

	private double[] sampleData;
	private double[] cache;
	private int index;

	public AnalyzerCorrelation(double[] sampleData)
	{
		this.sampleData = sampleData;
		this.cache = new double[sampleData.length];
		this.index = 0;
	}

	@Override
	public void accept(double[] buffer, int length)
	{
		for (int i = 0; i < length; i++) {

			// キャッシュに入れる
			cache[index] = buffer[i];

			// キャッシュとサンプルデータの相関を計算
			long sum = 0;
			for (int j = 0; j < sampleData.length; j++) {
				sum += sampleData[j] * cache[(index + 1 + j) % sampleData.length];
			}

			// バッファに戻す
			buffer[i] = sum;

			// キャッシュの使用位置更新
			index = (index + 1) % sampleData.length;

		}
	}

}
