package titanium.solar.core.recorder.dynamicanalyze;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.lang.HMath;
import mirrg.lithium.struct.Tuple;
import net.arnx.jsonic.JSON;
import titanium.solar.core.synchronizer.MainSynchronizer;
import titanium.solar.core.synchronizer.MainSynchronizer.IPacketSender;
import titanium.solar.libs.analyze.mountainlisteners.Chain;
import titanium.solar.libs.analyze.mountainlisteners.IChainListener;
import titanium.solar.libs.analyze.mountainlisteners.IChainListenerProvider;

public class ChainListenerProviderWeb implements IChainListenerProvider
{

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

	private double samplesPerSecond;
	private String host;
	private int port;
	private int backlog;
	private int webSocketPort;
	private boolean useSynchronizer;

	public ChainListenerProviderWeb(
		double samplesPerSecond,
		String host,
		int port,
		int backlog,
		int webSocketPort,
		boolean useSynchronizer)
	{
		this.samplesPerSecond = samplesPerSecond;
		this.host = host;
		this.port = port;
		this.backlog = backlog;
		this.webSocketPort = webSocketPort;
		this.useSynchronizer = useSynchronizer;
	}

	@Override
	public IChainListener createChainListener()
	{
		return new IChainListener() {

			private HttpServer httpServer;
			private WebSocketServer webSocketServer;
			private Optional<IPacketSender> oPacketSender = Optional.empty();

			{
				try {
					httpServer = HttpServer.create();
					httpServer.bind(new InetSocketAddress(host, port), backlog);

					httpServer.createContext("/api/streamPort", e -> {
						send(e, 200, "" + webSocketPort);
					});

					httpServer.createContext("/", e -> {
						String path = e.getRequestURI().getPath();
						if (path.endsWith("/")) {
							redirect(e, path + "index.html");
						} else {
							URL url = ChainListenerProviderWeb.class.getResource(path.substring(1));
							if (url != null) {
								sendFile(e, url);
								return;
							}
						}
						send(e, 404, "404");
					});

					webSocketServer = new WebSocketServer(new InetSocketAddress(host, webSocketPort)) {

						@Override
						public void onStart()
						{

						}

						@Override
						public void onOpen(WebSocket conn, ClientHandshake handshake)
						{

						}

						@Override
						public void onMessage(WebSocket conn, String message)
						{
							try {
								message = message.trim();
								message = message.substring(1, message.length() - 1);
								int[] data = Stream.of(message.split(","))
									.map(String::trim)
									.mapToInt(s -> Integer.parseInt(s, 10))
									.toArray();

								System.out.println("Send: " + IntStream.of(data)
									.mapToObj(i -> "" + i)
									.collect(Collectors.joining(",")));

								if (oPacketSender.isPresent()) oPacketSender.get().send(data);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onClose(WebSocket conn, int code, String reason, boolean remote)
						{

						}

						@Override
						public void onError(WebSocket conn, Exception ex)
						{
							ex.printStackTrace();
						}

					};
					if (useSynchronizer) {
						try {
							oPacketSender = Optional.of(MainSynchronizer.createPacketSender());
						} catch (Exception | UnsatisfiedLinkError e) {
							e.printStackTrace();
						}
					}

					httpServer.start();
					webSocketServer.start();
					new Thread(new Runnable() {

						private double[] v = new double[4];
						private double[] t = new double[4];
						private double[] a = new double[4];

						{
							for (int own_id = 0; own_id < v.length; own_id++) {
								v[own_id] = Math.random() * 14 + 1;
								t[own_id] = Math.random() * 45 + 15;
							}
						}

						@Override
						public void run()
						{
							while (true) {
								try {
									Thread.sleep(HMath.randomBetween(100, 2000));
								} catch (InterruptedException e1) {
									break;
								}

								int own_id = HMath.randomBetween(0, 3);

								a[own_id] += Math.random() * 3 - 1.5;
								if (a[own_id] > 2) a[own_id] = 2;
								if (a[own_id] < -2) a[own_id] = -2;

								v[own_id] += a[own_id] * (Math.random() * 1 + 2);
								if (v[own_id] > 15) {
									v[own_id] = 15;
									a[own_id] = 0;
								}
								if (v[own_id] < 1) {
									v[own_id] = 1;
									a[own_id] = 0;
								}

								t[own_id] += a[own_id] * (Math.random() * 1 + 2);
								if (t[own_id] > 60) {
									t[own_id] = 60;
									a[own_id] = 0;
								}
								if (t[own_id] < 15) {
									t[own_id] = 15;
									a[own_id] = 0;
								}

								onChain(
									"00001111111100000000111111110000000011111111",
									LocalDateTime.now(),
									Optional.of(new int[] {
										1, 5, 2, own_id + 2, (int) v[own_id], (int) t[own_id], 0,
								}));
							}
						}

					}).start();
					System.err.println("HTTP Server Start: " + host + ":" + port);
					System.err.println("WebSocket Server Start: " + host + ":" + webSocketPort);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			private int id = 0;
			private Long firstTime = null;

			@Override
			public void onChain(Chain chain)
			{
				onChain(chain.binary, chain.getFirstMountain().getTime(samplesPerSecond), chain.getBytes());
			}

			private void onChain(String binary, LocalDateTime time, Optional<int[]> bytes)
			{
				if (firstTime == null) firstTime = time.toInstant(ZoneOffset.UTC).toEpochMilli();
				int timeInt = (int) (time.toInstant(ZoneOffset.UTC).toEpochMilli() - firstTime);

				for (WebSocket connection : webSocketServer.connections()) {
					connection.send(toString(id, binary, time, timeInt, bytes));
				}
				id++;
			}

			private String toString(int id, String binary, LocalDateTime time, int timeInt, Optional<int[]> bytes)
			{
				return JSON.encode(new Hashtable<String, Object>() {
					{
						put("id", id);
						put("binary", binary);
						put("time", FORMATTER.format(time));
						put("time_int", timeInt);
						put("bytes", bytes);
					}
				});
			}

			@Override
			public void close()
			{
				try {
					httpServer.stop(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					webSocketServer.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
	}

	protected static void redirect(HttpExchange httpExchange, String string) throws IOException
	{
		httpExchange.getResponseHeaders().add("Location", string);
		httpExchange.sendResponseHeaders(301, 0);
		httpExchange.getResponseBody().close();
	}

	protected static void send(HttpExchange httpExchange, String text) throws IOException
	{
		send(httpExchange, 200, "text/html", text, "utf-8");
	}

	protected static void send(HttpExchange httpExchange, int code, String text) throws IOException
	{
		send(httpExchange, code, "text/html", text, "utf-8");
	}

	protected static void send(HttpExchange httpExchange, int code, String contentType, String text, String charset) throws IOException
	{
		httpExchange.getResponseHeaders().add("Content-Type", contentType + "; charset= " + charset);
		byte[] bytes = text.getBytes(charset);
		httpExchange.sendResponseHeaders(code, bytes.length);
		httpExchange.getResponseBody().write(bytes);
		httpExchange.getResponseBody().close();
	}

	protected static void sendFile(HttpExchange httpExchange, URL url) throws IOException
	{
		try {
			InputStream in = url.openStream();

			ArrayList<Tuple<byte[], Integer>> buffers = new ArrayList<>();
			while (true) {
				byte[] buffer = new byte[4000];
				int len = in.read(buffer);
				if (len == -1) break;
				buffers.add(new Tuple<>(buffer, len));
			}
			in.close();

			httpExchange.sendResponseHeaders(200, buffers.stream()
				.mapToInt(t -> t.y)
				.sum());
			for (Tuple<byte[], Integer> buffer : buffers) {
				httpExchange.getResponseBody().write(buffer.x, 0, buffer.y);
			}
			httpExchange.getResponseBody().close();
		} catch (IOException e2) {
			send(httpExchange, 404, "404");
		}
	}

	protected static class Parameters
	{

		private ArrayList<Tuple<String, String>> parameters;

		public Parameters(String rawQuery)
		{
			this.parameters = getParams(rawQuery);
		}

		private static ArrayList<Tuple<String, String>> getParams(String rawQuery)
		{
			ArrayList<Tuple<String, String>> result = new ArrayList<>();
			String[] entries = rawQuery.split("&");
			for (String entry : entries) {
				int index = entry.indexOf("=");
				if (index != -1) {
					result.add(new Tuple<>(
						decode(entry.substring(0, index)),
						decode(entry.substring(index + 1))));
				}
			}
			return result;
		}

		private static String decode(String string)
		{
			try {
				return new URI("?" + string).getQuery();
			} catch (URISyntaxException e) {
				return string;
			}
		}

		public Stream<Tuple<String, String>> getParameters()
		{
			return parameters.stream();
		}

		public Stream<String> getParameters(String key)
		{
			return getParameters()
				.filter(t -> t.x.equals(key))
				.map(t -> t.y);
		}

		public Optional<String> getParameter(String key)
		{
			return getParameters(key)
				.findFirst();
		}

		@Override
		public String toString()
		{
			return parameters.toString();
		}

	}

}
