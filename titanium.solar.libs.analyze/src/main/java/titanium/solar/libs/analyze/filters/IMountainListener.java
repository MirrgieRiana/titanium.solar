package titanium.solar.libs.analyze.filters;

public interface IMountainListener
{

	public void onMountain(Mountain mountain);

	/**
	 * 最後の山から指定時間経過したときに呼び出される。
	 */
	public void onTimeout(long x);

}
