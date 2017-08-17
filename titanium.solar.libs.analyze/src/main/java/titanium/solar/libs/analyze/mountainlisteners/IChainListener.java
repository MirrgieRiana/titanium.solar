package titanium.solar.libs.analyze.mountainlisteners;

public interface IChainListener extends AutoCloseable
{

	public void onChain(Chain chain);

	@Override
	public default void close()
	{

	}

}
