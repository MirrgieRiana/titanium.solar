package titanium.solar.recorder.core;

public class Attributes
{

	private StringBuilder sb = new StringBuilder();

	public void add(String key, Object value)
	{
		if (sb.length() != 0) sb.append(";");
		sb.append(key);
		sb.append(":");
		sb.append(value);
	}

	public void add(String entries)
	{
		if (sb.length() != 0) sb.append(";");
		sb.append(entries);
	}

	@Override
	public String toString()
	{
		return sb.toString();
	}

}
