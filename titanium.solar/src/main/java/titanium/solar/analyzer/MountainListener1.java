package titanium.solar.analyzer;

import java.util.ArrayList;

import titanium.solar.analyzer.analyzers.IMountainListener;
import titanium.solar.analyzer.analyzers.Mountain;

public class MountainListener1 implements IMountainListener
{

	private final int offsetShort;
	private final int offsetLong;
	private final int firstThreshold;
	private final int timeout;
	private final int maxXError;

	private Mountain lastMountain;
	private ArrayList<Mountain> mountains;
	private String chain;
	private int startX;

	private ArrayList<IChainListener> listeners = new ArrayList<>();

	public MountainListener1(int offsetShort, int offsetLong, int firstThreshold, int timeout, int maxXError)
	{
		this.offsetShort = offsetShort;
		this.offsetLong = offsetLong;
		this.firstThreshold = firstThreshold;
		this.timeout = timeout;
		this.maxXError = maxXError;
	}

	@Override
	public void onMountain(Mountain mountain)
	{
		if (lastMountain == null) {
			// 初めての山はタイミングに関わらず受理
			if (mountain.y >= firstThreshold) {
				lastMountain = mountain;
				mountains = new ArrayList<>();
				chain = "";
				startX = lastMountain.x;
			}
			return;
		}

		mountains.add(mountain);
		spend(mountain.x);
	}

	@Override
	public void onTimeout(int x)
	{
		spend(x);
	}

	private void spend(int x)
	{
		while (lastMountain != null && x > lastMountain.x + timeout) {
			pullMountain();
		}
	}

	private void pullMountain()
	{
		Mountain nextShortMountain = null;
		Mountain nextLongMountain = null;

		for (Mountain mountain : mountains) {
			if (isNextShortMountain(mountain)) {
				nextShortMountain = mountain;
			}
			if (isNextLongMountain(mountain)) {
				nextLongMountain = mountain;
			}
		}

		if (nextShortMountain != null) {
			if (nextLongMountain != null) {
				// 山が両方見つかった

				if (nextShortMountain.y > nextLongMountain.y) {
					// 短山を選択
					lastMountain = nextShortMountain;
					chain += "0";
					cut(lastMountain.x);
				} else {
					// 長山を選択
					lastMountain = nextLongMountain;
					chain += "1";
					cut(lastMountain.x);
				}

			} else {
				// 短山だけが見つかった
				lastMountain = nextShortMountain;
				chain += "0";
				cut(lastMountain.x);
			}
		} else {
			if (nextLongMountain != null) {
				// 長山だけが見つかった
				lastMountain = nextLongMountain;
				chain += "1";
				cut(lastMountain.x);
			} else {
				// 山が見つからなかったのでパケット終了
				lastMountain = null;
				listeners.forEach(l -> l.onChain(new Chain(chain, startX)));
			}
		}
	}

	private boolean isNextShortMountain(Mountain mountain)
	{
		return mountain.x >= lastMountain.x + offsetShort - maxXError
			&& mountain.x <= lastMountain.x + offsetShort + maxXError;
	}

	private boolean isNextLongMountain(Mountain mountain)
	{
		return mountain.x >= lastMountain.x + offsetLong - maxXError
			&& mountain.x <= lastMountain.x + offsetLong + maxXError;
	}

	private void cut(int x)
	{
		mountains.removeIf(m -> m.x <= x);
	}

	public MountainListener1 addChainListener(IChainListener listener)
	{
		listeners.add(listener);
		return this;
	}

}
