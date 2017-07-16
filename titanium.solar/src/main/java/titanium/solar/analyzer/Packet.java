package titanium.solar.analyzer;

public class Packet
{

	public final int id;
	public final int voltage;
	public final int temperature;

	public Packet(int id, int voltage, int temperature)
	{
		this.id = id;
		this.voltage = voltage;
		this.temperature = temperature;

	}

}
