package titanium.solar.analyzer.old;

public class AnalyzerMul2
{

	public void accept(long[] src1, long[] src2, long[] dest)
	{
		accept(src1, 0, src2, 0, src1.length, dest);
	}

	public void accept(long[] src1, int start1, long[] src2, int start2, int length, long[] dest)
	{
		for (int i = 0; i < length; i++) {
			dest[i] = src1[Math.max(i + start1 - 1, 0)] * src2[i + start2];
		}
	}

}
