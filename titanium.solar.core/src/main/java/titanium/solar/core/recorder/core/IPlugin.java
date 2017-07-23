package titanium.solar.core.recorder.core;

import mirrg.lithium.properties.IProperties;

public interface IPlugin
{

	public void initialize(Recorder recorder, IProperties properties);

	public void apply();

	public String getName();

}
