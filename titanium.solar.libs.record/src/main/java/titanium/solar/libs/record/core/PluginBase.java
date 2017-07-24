package titanium.solar.libs.record.core;

import mirrg.lithium.properties.IProperties;

public abstract class PluginBase implements IPlugin
{

	protected Recorder recorder;
	protected IProperties properties;

	@Override
	public void initialize(Recorder recorder, IProperties properties)
	{
		this.recorder = recorder;
		this.properties = properties;
	}

}
