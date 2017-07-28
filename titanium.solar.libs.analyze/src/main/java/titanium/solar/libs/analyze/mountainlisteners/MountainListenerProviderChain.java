package titanium.solar.libs.analyze.mountainlisteners;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mirrg.lithium.struct.ImmutableArray;
import mirrg.lithium.struct.Tuple3;
import titanium.solar.libs.analyze.filters.IMountainListener;
import titanium.solar.libs.analyze.filters.IMountainListenerProvider;
import titanium.solar.libs.analyze.filters.Mountain;

public class MountainListenerProviderChain implements IMountainListenerProvider
{

	private final int offsetShort;
	private final int offsetLong;
	private final int firstThreshold;
	private final int timeout;
	private final int maxXError;

	private ArrayList<IChainListenerProvider> chainListenersPrioviders = new ArrayList<>();

	public MountainListenerProviderChain(int offsetShort, int offsetLong, int firstThreshold, int timeout, int maxXError)
	{
		this.offsetShort = offsetShort;
		this.offsetLong = offsetLong;
		this.firstThreshold = firstThreshold;
		this.timeout = timeout;
		this.maxXError = maxXError;
	}

	public MountainListenerProviderChain addChainListenerProvider(IChainListenerProvider chainListenerProvider)
	{
		chainListenersPrioviders.add(chainListenerProvider);
		return this;
	}

	@Override
	public IMountainListener createMountainListener()
	{
		ArrayList<IChainListener> chainListeners = chainListenersPrioviders.stream()
			.map(IChainListenerProvider::createChainListener)
			.collect(Collectors.toCollection(ArrayList::new));

		return new IMountainListener() {

			private Mountain lastMountain;
			private ArrayList<Mountain> mountains;

			private ArrayList<Mountain> mountainsChain;
			private String chain;

			@Override
			public void onMountain(Mountain mountain)
			{
				if (lastMountain == null) {
					// 初めての山はタイミングに関わらず受理
					if (mountain.y >= firstThreshold) {
						setFirstMountain(mountain);
					}
					return;
				}

				mountains.add(mountain);
				spend(mountain.x);
			}

			private void setFirstMountain(Mountain mountain)
			{
				lastMountain = mountain;
				mountains = new ArrayList<>();

				mountainsChain = new ArrayList<>();
				mountainsChain.add(lastMountain);
				chain = "";
			}

			@Override
			public void onTimeout(long x)
			{
				spend(x);
			}

			private void spend(long x)
			{
				while (lastMountain != null && x > lastMountain.x + timeout) {
					pullMountain();
				}
			}

			private void pullMountain()
			{
				Tuple3<Mountain, Double, String> tuple3 = Stream.concat(
					mountains.stream()
						.map(m -> new Tuple3<>(m, 1 - (double) getNextShortMountainDistance(m) / maxXError, "0")),
					mountains.stream()
						.map(m -> new Tuple3<>(m, (1 - (double) getNextLongMountainDistance(m) / maxXError) * 0.75, "1")))
					.filter(t -> t.x.y >= lastMountain.y * 0.5)
					.filter(t -> t.y >= 0)
					.max((a, b) -> (int) Math.signum(a.y - b.y))
					.orElse(null);

				if (tuple3 == null) {
					// 山が見つからなかったのでパケット終了
					lastMountain = null;
					chainListeners.forEach(l -> l.onChain(new Chain(new ImmutableArray<>(mountainsChain), chain)));
				} else {
					// 山が見つかったので追加
					lastMountain = tuple3.x;
					mountainsChain.add(lastMountain);
					chain += tuple3.z;
					cut(lastMountain.x);
				}
			}

			private int getNextShortMountainDistance(Mountain mountain)
			{
				return (int) Math.abs(mountain.x - (lastMountain.x + offsetShort));
			}

			private int getNextLongMountainDistance(Mountain mountain)
			{
				return (int) Math.abs(mountain.x - (lastMountain.x + offsetLong));
			}

			private void cut(long x)
			{
				mountains.removeIf(m -> m.x <= x);
			}

		};
	}

}
