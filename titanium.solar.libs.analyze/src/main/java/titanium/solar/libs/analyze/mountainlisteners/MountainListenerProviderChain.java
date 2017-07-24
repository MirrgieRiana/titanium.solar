package titanium.solar.libs.analyze.mountainlisteners;

import java.util.ArrayList;
import java.util.stream.Collectors;

import mirrg.lithium.struct.ImmutableArray;
import mirrg.lithium.struct.Tuple;
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
						lastMountain = mountain;
						mountains = new ArrayList<>();

						mountainsChain = new ArrayList<>();
						mountainsChain.add(lastMountain);
						chain = "";
					}
					return;
				}

				mountains.add(mountain);
				spend(mountain.x);
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
				Mountain nextShortMountain = mountains.stream()
					.map(m -> new Tuple<>(m, getNextShortMountainDistance(m)))
					.filter(t -> t.y < maxXError)
					.min((a, b) -> a.y - b.y)
					.map(t -> t.x)
					.orElse(null);
				Mountain nextLongMountain = mountains.stream()
					.map(m -> new Tuple<>(m, getNextLongMountainDistance(m)))
					.filter(t -> t.y < maxXError)
					.min((a, b) -> a.y - b.y)
					.map(t -> t.x)
					.orElse(null);

				if (nextShortMountain != null) {
					if (nextLongMountain != null) {
						// 山が両方見つかった

						if (nextShortMountain.y > nextLongMountain.y) {
							// 短山を選択
							lastMountain = nextShortMountain;
							mountainsChain.add(lastMountain);
							chain += "0";
							cut(lastMountain.x);
						} else {
							// 長山を選択
							lastMountain = nextLongMountain;
							mountainsChain.add(lastMountain);
							chain += "1";
							cut(lastMountain.x);
						}

					} else {
						// 短山だけが見つかった
						lastMountain = nextShortMountain;
						mountainsChain.add(lastMountain);
						chain += "0";
						cut(lastMountain.x);
					}
				} else {
					if (nextLongMountain != null) {
						// 長山だけが見つかった
						lastMountain = nextLongMountain;
						mountainsChain.add(lastMountain);
						chain += "1";
						cut(lastMountain.x);
					} else {
						// 山が見つからなかったのでパケット終了
						lastMountain = null;
						chainListeners.forEach(l -> l.onChain(new Chain(new ImmutableArray<>(mountainsChain), chain)));
					}
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
