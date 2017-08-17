package titanium.solar.core.synchronizer;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import mirrg.lithium.properties.HPropertiesParser;
import mirrg.lithium.properties.IProperties;
import mirrg.lithium.properties.PropertiesMultipleInheritable;

public class MainSynchronizer
{

	private static int highUs;
	private static int zeroLowUs;
	private static int oneLowUs;

	public static void main(String[] args) throws Exception
	{
		IProperties properties = parse("default.synchronizer.properties", args);

		int periodMs = properties.get("periodMs").getInteger().get();
		String gpioPin = properties.get("gpioPin").getString().get();
		highUs = properties.get("highUs").getInteger().get();
		zeroLowUs = properties.get("zeroLowUs").getInteger().get();
		oneLowUs = properties.get("oneLowUs").getInteger().get();
		int[] data = Stream.of(properties.get("data").getString().get().split("\\s+"))
			.mapToInt(s -> Integer.parseInt(s, 10))
			.toArray();

		GpioController gpio = GpioFactory.getInstance();
		Pin pinName = (Pin) RaspiPin.class.getField(gpioPin).get(null);
		GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(pinName, MainSynchronizer.class.getPackage().getName(), PinState.LOW);
		while (true) {
			new Thread(() -> {
				try {
					sendPacket(pin, data);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}).start();
			Thread.sleep(periodMs);
		}

		//gpio.shutdown();
	}

	public static IPacketSender createPacketSender() throws Exception, UnsatisfiedLinkError
	{
		IProperties properties = parse("default.synchronizer.properties", new String[0]);

		String gpioPin = properties.get("gpioPin").getString().get();
		highUs = properties.get("highUs").getInteger().get();
		zeroLowUs = properties.get("zeroLowUs").getInteger().get();
		oneLowUs = properties.get("oneLowUs").getInteger().get();

		GpioController gpio = GpioFactory.getInstance();
		Pin pinName = (Pin) RaspiPin.class.getField(gpioPin).get(null);
		GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(pinName, MainSynchronizer.class.getPackage().getName(), PinState.LOW);

		return new IPacketSender() {

			@Override
			public void send(int[] data) throws InterruptedException
			{
				sendPacket(pin, data);
			}

			@Override
			public void close()
			{
				gpio.shutdown();
			}

		};
	}

	public static interface IPacketSender
	{

		public void send(int[] data) throws InterruptedException;

		public void close();

	}

	public static void sendPacket(GpioPinDigitalOutput pin, int[] data) throws InterruptedException
	{
		sendPre(pin);
		for (int i : data) {
			sendByte(pin, i);
		}
		sendPost(pin);
	}

	public static void sendByte(GpioPinDigitalOutput pin, int b) throws InterruptedException
	{
		for (int i = 0; i < 8; i++) {
			if ((b & (1 << i)) != 0) {
				sendOne(pin);
			} else {
				sendZero(pin);
			}
		}
	}

	public static void sendPre(GpioPinDigitalOutput pin) throws InterruptedException
	{
		sendOne(pin);
		sendOne(pin);
		sendOne(pin);
		sendOne(pin);
	}

	public static void sendPost(GpioPinDigitalOutput pin) throws InterruptedException
	{
		sendOne(pin);
		sendBlank(pin);
	}

	public static void sendZero(GpioPinDigitalOutput pin) throws InterruptedException
	{
		pin.high();
		sleep(highUs);
		pin.low();
		sleep(zeroLowUs);
	}

	public static void sendOne(GpioPinDigitalOutput pin) throws InterruptedException
	{
		pin.high();
		sleep(highUs);
		pin.low();
		sleep(oneLowUs);
	}

	public static void sendBlank(GpioPinDigitalOutput pin) throws InterruptedException
	{
		sleep(5000);
	}

	public static void sleep(int micros)
	{
		long start = System.nanoTime();
		while (System.nanoTime() < start + micros * 1000) {

		}
	}

	private static IProperties parse(String defaultPropertyFileName, String... args) throws Exception
	{
		boolean useDefaultPropertyFile = true;

		PropertiesMultipleInheritable properties = new PropertiesMultipleInheritable();
		{
			for (String arg : args) {

				// =が入ってたら値
				int index = arg.indexOf("=");
				if (index >= 0) {
					properties.put(arg.substring(0, index), arg.substring(index + 1));
					continue;
				}

				// 空じゃなかったら親
				if (!arg.isEmpty()) {

					ArrayList<Exception> exceptions = new ArrayList<>();
					properties.addParent(HPropertiesParser.parse(new File(arg), exceptions::add));
					if (!exceptions.isEmpty()) throw exceptions.get(0);

					useDefaultPropertyFile = false;
					continue;
				}

				throw new RuntimeException();

			}
		}

		if (useDefaultPropertyFile) {
			ArrayList<Exception> exceptions = new ArrayList<>();
			properties.addParent(HPropertiesParser.parse(new File(defaultPropertyFileName), exceptions::add));
			if (!exceptions.isEmpty()) throw exceptions.get(0);
		}

		return properties;
	}

}
