package titanium.solar.recorder.analyzer;

public class Chain
{

	public final String binary;
	public final int length;
	public final int x;

	public Chain(String binary, int x)
	{
		this.binary = binary;
		this.length = binary.length();
		this.x = x;
	}

}
