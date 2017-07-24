package titanium.solar.libs.record.core;

public class ChunkStatistics
{

	public int min;
	public int max;
	public long bunsan;
	public long noiz;

	public ChunkStatistics(Chunk entry)
	{
		min = 255;
		max = -255;
		bunsan = 0;
		noiz = 0;

		int last = 0;
		for (int i = 0; i < entry.length; i++) {
			int v = entry.buffer.array[i];

			if (min > v) min = v;
			if (max < v) max = v;
			bunsan += v * v;
			if (Math.abs(v - last) > 64) noiz++;

			last = v;
		}

		bunsan /= entry.length;
	}

	@Override
	public String toString()
	{
		return String.format(
			"Min:%s;Max:%s;Variance:%s;Noiz:%s",
			min,
			max,
			bunsan,
			noiz);
	}

}
