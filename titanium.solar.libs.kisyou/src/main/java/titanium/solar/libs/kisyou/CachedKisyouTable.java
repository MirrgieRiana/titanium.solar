package titanium.solar.libs.kisyou;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Hashtable;

import mirrg.lithium.struct.ImmutableArray;

public class CachedKisyouTable
{

	private File cacheDirectory;

	private Hashtable<Key, ImmutableArray<KisyouEntry>> table = new Hashtable<>();

	public CachedKisyouTable(File cacheDirectory)
	{
		this.cacheDirectory = cacheDirectory;
	}

	public ImmutableArray<KisyouEntry> getKisyouEntries(Key key) throws IOException
	{
		ImmutableArray<KisyouEntry> kisyouEntries = table.get(key);
		if (kisyouEntries == null) {
			kisyouEntries = getKisyouEntries2(key);
			table.put(key, kisyouEntries);
		}
		return kisyouEntries;
	}

	protected String getCacheFileName(Key key)
	{
		return key + ".html";
	}

	public File getCacheFile(Key key)
	{
		return new File(cacheDirectory, getCacheFileName(key));
	}

	protected ImmutableArray<KisyouEntry> getKisyouEntries2(Key key) throws IOException
	{
		return new ImmutableArray<>(HKisyou.parse(key, new String(getPageData(key))));
	}

	protected byte[] getPageData(Key key) throws IOException
	{
		File cacheFile = getCacheFile(key);
		if (cacheFile.isFile()) {
			try (InputStream in = new FileInputStream(cacheFile)) {
				return HKisyou.getPageData(in);
			}
		} else {
			byte[] pageData;
			try (InputStream in = HKisyou.getURL(key).openStream()) {
				pageData = HKisyou.getPageData(in);
			}
			try (OutputStream out = new FileOutputStream(cacheFile)) {
				out.write(pageData);
			}
			return pageData;
		}
	}

	public KisyouEntry getKisyouEntry(LocalDateTime time) throws IOException
	{
		ImmutableArray<KisyouEntry> kisyouEntries = getKisyouEntries(new Key(time));
		for (int i = kisyouEntries.length() - 1; i >= 0; i--) {
			KisyouEntry kisyouEntry = kisyouEntries.get(i);
			if (kisyouEntry.time.compareTo(time) <= 0) {
				return kisyouEntry;
			}
		}
		throw new NullPointerException();
	}

}
