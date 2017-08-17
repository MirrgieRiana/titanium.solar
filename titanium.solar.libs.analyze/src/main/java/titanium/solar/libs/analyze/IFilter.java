package titanium.solar.libs.analyze;

import mirrg.lithium.struct.Struct1;

public interface IFilter extends AutoCloseable
{

	public void accept(double[] buffer, int length, Struct1<Double> offset);

	@Override
	public default void close()
	{

	}

}
