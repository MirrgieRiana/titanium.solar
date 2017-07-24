package titanium.solar.libs.analyze;

import mirrg.lithium.struct.Struct1;

public interface IFilter
{

	public void accept(double[] buffer, int length, Struct1<Double> offset);

}
