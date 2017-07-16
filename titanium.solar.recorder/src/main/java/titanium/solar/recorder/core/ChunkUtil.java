package titanium.solar.recorder.core;

public class ChunkUtil
{

	public static String getStringGraph(Chunk chunk, int width, double zoom)
	{
		int w = width;
		StringBuilder sb = new StringBuilder();

		for (int x1 = 0; x1 < w; x1++) {
			int x2 = x1 + 1;

			double rate1 = (double) x1 / w;
			double rate2 = (double) x2 / w;

			int index1 = (int) (chunk.length * rate1);
			int index2 = (int) (chunk.length * rate2);
			if (index1 == index2) index2++;

			int min = 0;
			int max = 0;
			for (int i = index1; i < index2; i++) {
				if (i >= chunk.length) break;
				if (min > chunk.buffer.array[i]) min = chunk.buffer.array[i];
				if (max < chunk.buffer.array[i]) max = chunk.buffer.array[i];
			}

			int v = (int) ((double) Math.max(max, -min) / 128 * 35 * zoom);
			v = Math.max(Math.min(v, 35), 0);
			sb.append("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(v));
		}

		return sb.toString();
	}

}
