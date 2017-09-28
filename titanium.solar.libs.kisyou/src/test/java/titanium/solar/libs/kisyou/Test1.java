package titanium.solar.libs.kisyou;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;

import org.junit.Test;

public class Test1
{

	private static void delete(File f)
	{
		if (f.exists() == false) {
			return;
		}

		if (f.isFile()) {
			f.delete();
		}

		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				delete(files[i]);
			}
			f.delete();
		}
	}

	@Test
	public void test1() throws Exception
	{
		assertEquals(24 * 6, HKisyou.getKisyouEntries(new Key(2017, 4, 2)).size());
	}

	@Test
	public void test2() throws Exception
	{
		File cacheDirectory = new File("cache");
		delete(cacheDirectory);
		cacheDirectory.mkdirs();
		CachedKisyouTable cachedKisyouTable = new CachedKisyouTable(cacheDirectory);

		assertEquals(24 * 6, cachedKisyouTable.getKisyouEntries(new Key(2017, 4, 2)).length());
		assertEquals(24 * 6, cachedKisyouTable.getKisyouEntries(new Key(2017, 4, 2)).length());
	}

	@Test
	public void test3() throws Exception
	{
		File cacheDirectory = new File("cache2");
		cacheDirectory.mkdirs();
		CachedKisyouTable cachedKisyouTable = new CachedKisyouTable(cacheDirectory);

		BiConsumer<LocalDateTime, LocalDateTime> f1 = (a, b) -> {
			try {
				assertEquals(a, cachedKisyouTable.getKisyouEntry(b).time);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};

		f1.accept(LocalDateTime.of(2017, 4, 6, 12, 50, 0), LocalDateTime.of(2017, 4, 6, 12, 53, 32));
		f1.accept(LocalDateTime.of(2017, 5, 30, 15, 30, 0), LocalDateTime.of(2017, 5, 30, 15, 30, 0));
		f1.accept(LocalDateTime.of(2017, 5, 30, 15, 30, 0), LocalDateTime.of(2017, 5, 30, 15, 30, 1));
		f1.accept(LocalDateTime.of(2017, 5, 30, 15, 30, 0), LocalDateTime.of(2017, 5, 30, 15, 39, 59));
		f1.accept(LocalDateTime.of(2017, 5, 30, 15, 40, 0), LocalDateTime.of(2017, 5, 30, 15, 40, 0));
	}

}
