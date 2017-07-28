package titanium.solar.core.analyzer.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import mirrg.lithium.struct.Tuple;
import titanium.solar.libs.analyze.TimeConversion;

public class Main6Syuukei
{

	public static void main(String[] args)
	{
		File srcFile = new File("H:\\amyf\\jesqenvina\\xa1\\5_extract\\data.csv");

		IPacketListener packetListener = new IPacketListener() {

			private boolean isFirst = true;
			private LocalDateTime threshold;
			private ArrayList<Packet1> packets;

			@Override
			public void onPacket(Packet1 packet)
			{

				if (isFirst) {
					isFirst = false;
					threshold = packet.time.withMinute(0).withSecond(0).withNano(0);
					packets = new ArrayList<>();
				}

				while (packet.time.compareTo(threshold) > 0) {

					collect();

					threshold = threshold.plusMinutes(10);
					packets = new ArrayList<>();
				}

				packets.add(packet);

			}

			@Override
			public void onFinish()
			{
				collect();
			}

			private void collect()
			{
				int maxId = 48;
				int[] sumVoltages = new int[maxId + 1];
				int[] sumTemperature = new int[maxId + 1];
				int[] counts = new int[maxId + 1];

				packets.forEach(p -> {
					if (p.id < 1) return;
					if (p.id > 48) return;
					if (p.crc01 != (p.crc() & 0xff)) return;

					sumVoltages[p.id] += p.voltage;
					sumTemperature[p.id] += p.temperature;
					counts[p.id]++;
				});

				IntStatistics statisticsCount = new IntStatistics(IntStream.range(1, 48)
					.map(i -> counts[i])
					.toArray());
				DoubleStatistics statisticsVoltage = new DoubleStatistics(IntStream.range(1, 48)
					.filter(i -> counts[i] != 0)
					.mapToDouble(i -> (double) sumVoltages[i] / counts[i])
					.toArray());
				DoubleStatistics statisticsTemperature = new DoubleStatistics(IntStream.range(1, 48)
					.filter(i -> counts[i] != 0)
					.mapToDouble(i -> (double) sumTemperature[i] / counts[i])
					.toArray());
				String ranking = IntStream.range(1, 48)
					.mapToObj(i -> new Tuple<>(i, counts[i]))
					.sorted((a, b) -> -(a.y - b.y))
					.limit(5)
					.map(t -> t.y + "(ID:" + t.x + ")")
					.collect(Collectors.joining(" / "));
				String string = IntStream.range(1, 48)
					.mapToObj(i -> String.format(" %6.2f/%4d",
						counts[i] == 0 ? 0 : (double) sumVoltages[i] / counts[i],
						counts[i]))
					.collect(Collectors.joining(""));

				/*
				System.out.println(String.format("%s %s",
					FORMATTER.format(threshold.minusMinutes(10)),
					sb.toString()));
					*/
				System.out.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s"
					//+ ",%s"
					+ "",
					TimeConversion.format(threshold.minusMinutes(10)),
					statisticsCount.sum,
					statisticsCount.variance,
					statisticsVoltage.average,
					statisticsVoltage.variance,
					statisticsTemperature.average,
					statisticsTemperature.variance,
					ranking,
					//string,
					null));
			}

		};

		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)))) {
			while (true) {
				String line = in.readLine();
				if (line == null) break;

				// パケット行解析
				Optional<Packet1> oPacket = Packet1.parse(line);
				if (!oPacket.isPresent()) continue;

				// 受理
				packetListener.onPacket(oPacket.get());

			}
			packetListener.onFinish();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public interface IPacketListener
	{

		public void onPacket(Packet1 packet);

		public void onFinish();

	}

	public static class DoubleStatistics
	{

		public final double sum;
		public final double average;
		public final double variance;

		public DoubleStatistics(double[] doubles)
		{
			sum = DoubleStream.of(doubles)
				.sum();
			average = DoubleStream.of(doubles)
				.average()
				.orElse(0);
			variance = DoubleStream.of(doubles)
				.map(d -> d * d)
				.average()
				.orElse(0) - average * average;
		}

	}

	public static class IntStatistics
	{

		public final int sum;
		public final double average;
		public final double variance;

		public IntStatistics(int[] ints)
		{
			sum = IntStream.of(ints)
				.sum();
			average = IntStream.of(ints)
				.average()
				.getAsDouble();
			variance = IntStream.of(ints)
				.map(d -> d * d)
				.average()
				.getAsDouble() - average * average;
		}

	}

}
