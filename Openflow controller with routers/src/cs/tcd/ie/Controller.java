package cs.tcd.ie;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Hashtable;
import tcdIO.Terminal;
public class Controller extends Node {
	private static final String DEFAULT_DST_NODE = "localhost";
	public static int port = 60000;
	private Terminal terminal;
	static int[][] route;
	int in = 40000;
	int out = 40001;
	InetSocketAddress dstAddress;
	static Hashtable<Integer, Integer> ports;
	static Hashtable<Integer, int[]> routerPorts;
	static Hashtable<Integer, int[]> routerNext;
	static Hashtable<Integer, int[]> routerTime;
	int timer = 0;
	int[][] graph = new int[3][20];
	int firstGo = 0;
	int i;
	int h = 0;
	boolean hit = false;
	boolean last = false;
	int count;
	int value;
	char[] split;
	int firstport;
	int index = 0;
	int count1 = 0;

	public synchronized void onReceipt(DatagramPacket packet) {
		byte[] info = packet.getData();
		hit = false;
		last = false;
		if (info[1] != 0) {
			index = (int) info[1];
			setup(packet, i);
		} else {
			firstport = packet.getPort();
			nextSocket(packet);
		}
	}

	private int getMin(int[] values, int i) {

		int[] times = routerTime.get(i);
		int min = 0;
		for (int v = 0; v < 4; v++) {
			if (v == 0) {
				min = Integer.MAX_VALUE;
			}
			if ((times[v] < min) && (times[v] != 0)) {
				min = v;
				timer = times[v];
			}
		}
		if (min == Integer.MAX_VALUE) {
			return 0;
		} else {
			times[min] = 0;
			routerTime.put(i, times);
		}
		return min;
	}

	private int getPort(int port) {

		for (int i = 1; i < 20; i++) {
			if (routerNext.containsKey(i)) {
				int[] dest = routerNext.get(i);
				for (int j = 0; j < 4; j++) {
					if (dest[j] == port) {
						return i;
					}
				}
			} else {
				break;
			}
		}
		return 0;
	}

	private void nextSocket(DatagramPacket packet) {

		byte[] c = packet.getData();
		int firstRouter = (int) c[7];
		byte[] byte2 = new byte[4];
		byte2[2] = c[12];
		byte2[3] = c[13];
		int des = new BigInteger(byte2).intValue();
		int[] nextValues = routerNext.get(firstRouter);
		boolean here = false;
		for (int u = 0; u < 4; u++) {
			if (nextValues[u] == des) {
				here = true;
			}
		}
		if (here == true) {
			dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, packet.getPort());
			byte[] bytes = new byte[4];
			ByteBuffer.wrap(bytes).putInt(packet.getPort());
			byte[] bytes1 = new byte[4];
			ByteBuffer.wrap(bytes1).putInt(des);
			byte[] payload = new byte[8];
			System.arraycopy(bytes, 0, payload, 0, bytes.length);
			System.arraycopy(bytes1, 0, payload, bytes.length, bytes1.length);
			terminal.println(Arrays.toString(payload));
			byte[] header = new byte[PacketContent.HEADERLENGTH];
			header[9] = 1;
			last = false;
			byte[] buffer = new byte[header.length + payload.length];
			System.arraycopy(header, 0, buffer, 0, header.length);
			System.arraycopy(payload, 0, buffer, header.length, payload.length);
			packet = new DatagramPacket(buffer, buffer.length, dstAddress);
			try {
				socket.send(packet);
			} catch (IOException e) {
			}
		} else {
			StringContent content = new StringContent(packet);
			String router = content.toString();
			if (router.length() != 0) {
				split = router.toCharArray();
			}
			last = false;
			int port = 0;
			int from = 0;
			int count2 = 0;
			if (firstGo == 0) {
				for (int j = 1; j < 6; j++) {
					int[] p = routerPorts.get(j);
					for (int i = 1; i < 4; i++) {
						int value = getMin(p, j);
						if (value == 0) {
							break;
						} else {
							int[] nextPort = routerNext.get(j);
							int[] isPort = routerPorts.get(j);
							from = isPort[value];
							port = nextPort[value];
						}
						for (int v = j; v < 7; v++) {
							int[] val = routerPorts.get(v);
							for (int k = 0; k < 4; k++) {
								if (val[k] == port) {
									graph[count2][count1] = j;
									count2 = count2 + 1;
									graph[count2][count1] = timer;
									count2 = count2 + 1;
									graph[count2][count1] = v;
									count2 = 0;
									count1++;
									break;
								}
							}
						}
					}

				}
				firstGo++;
			}
			terminal.println("graph" + Arrays.deepToString(graph));
			int ro = c[7];
			int keep = ro;
			int[][] route = new int[3][20];
			int runningTotal = 0;
			int shifter = 0;
			int x1 = 0;
			int j = 0;
			for (int h = 0; h < 5; h++) {
				if (h == 0) {
					boolean hit = false;
					for (int p = 0; p < count1; p++) {

						if (graph[0][p] == ro) {
							hit = true;
							break;
						}
					}
					if (hit == false) {
						int temp = 0;
						for (int t = 0; t < count1; t++) {

							temp = graph[2][t];
							graph[2][t] = graph[0][t];
							graph[0][t] = temp;
						}
					}
					for (int i = 0; i < count1; i++) {
						if ((graph[0][i] == ro)) {
							route[0][x1] = graph[2][i];
							route[1][x1] = graph[0][i];
							route[2][x1] = graph[1][i];
							x1++;
						}
						if (graph[2][i] == ro) {
							route[0][x1] = graph[0][i];
							route[1][x1] = graph[2][i];
							route[2][x1] = graph[1][i];
							x1++;
						}
					}
				}
				ro = route[0][shifter];
				if (ro == 0) {
					int temp = 0;
					for (int t = 0; t < count1; t++) {
						temp = graph[2][t];
						graph[2][t] = graph[0][t];
						graph[0][t] = temp;
					}
				}
				runningTotal = route[2][shifter];
				shifter++;
				int right = 0;
				int step;
				boolean hit = false;
				boolean start = false;
				boolean start1 = false;
				for (int i = 0; i < count1; i++) {
					hit = false;
					if ((graph[0][i] == ro)) {
						start = true;
						right = graph[2][i];
					} else if ((graph[2][i] == ro)) {
						start1 = true;
						right = graph[0][i];
					}
					if ((start == true) || (start1 == true)) {
						for (int l = 0; l < 20; l++) {
							if ((right == route[1][l])) {
								hit = true;
							}
							if (route[0][l] == ro) {
								keep = route[1][l];
							}
						}
					}
					if ((start == true) || (start1 == true)) {
						step = graph[1][i];
						for (j = 0; j < 20; j++) {
							if (graph[2][i] == keep) {
								hit = true;
								break;
							} else if (graph[0][i] == keep) {
								hit = true;
								break;
							}
							if (route[1][j] == right) {
								if (((runningTotal + step) < route[2][j])) {
									if (start == true) {
										route[0][j] = graph[2][i];
										route[1][j] = graph[0][i];
										route[2][j] = step;
									} else {
										route[0][j] = graph[2][i];
										route[1][j] = graph[0][i];
										route[2][j] = step;
									}
								}
								hit = true;
							} else if (route[0][j] == right) {
								if (((runningTotal + step) < route[2][j])) {
									if (start == true) {
										route[0][j] = graph[2][i];
										route[1][j] = graph[0][i];
										route[2][j] = step;
									} else {
										route[1][j] = graph[2][i];
										route[0][j] = graph[0][i];
										route[2][j] = step;
									}
								}
								hit = true;
							}
						}
						if (hit == false) {
							if (start == true) {
								route[0][x1] = graph[2][i];
								route[1][x1] = graph[0][i];
								route[2][x1] = step;
							} else if (start1 == true) {
								route[0][x1] = graph[0][i];
								route[1][x1] = graph[2][i];
								route[2][x1] = step;
							}
							x1++;
							hit = false;
						}
						hit = false;
						start = false;
						start1 = false;
					}
				}
			}
			terminal.println("route " + Arrays.deepToString(route));
			byte[] data = packet.getData();
			content = new StringContent(packet);
			router = content.toString();
			byte[] byte1 = new byte[4];
			byte1[2] = data[12];
			byte1[3] = data[13];
			int destination = new BigInteger(byte1).intValue();
			int destRouter = getPort(destination);
			int count = 0;
			int i;
			int[] fastest = new int[5];
			for (i = 0; i < 6; i++) {
				if (destRouter == route[0][i]) {
					if ((count == 0) || (fastest[count - 1] != destRouter)) {
						fastest[count] = route[0][i];
						count++;
					}
					fastest[count] = route[1][i];
					destRouter = fastest[count];
					i = -1;
					count++;
				}
			}
			terminal.println("fastest " + Arrays.toString(fastest));
			int now;
			int later;
			int portFrom = 0;
			int portTo = 0;
			boolean last = false;
			for (int p = 0; p < 4; p++) {
				now = fastest[p];
				if (p == 0) {
					firstPort(now, destination);
				}
				later = fastest[p + 1];
				if (later == 0) {
					break;
				}
				if (routerPorts.containsKey(now)) {
					int k = 0;
					int[] value = routerPorts.get(now);
					int[] nextValue = routerPorts.get(later);
					int[] returnValues = routerNext.get(now);
					int s = 0;
					int q = 0;
					for (k = 0; k < 16; k++) {
						if (returnValues[s] == nextValue[q]) {
							portFrom = nextValue[q];
							portTo = value[s];
							if (later == destRouter) {
								last = true;
							}
							break;
						}
						if (s == 3) {
							q++;
							s = 0;
						} else {
							s++;
						}
						if (q > 3) {
							q = 0;
						}
					}
					dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, portFrom);
					byte[] bytes = new byte[4];
					ByteBuffer.wrap(bytes).putInt(portFrom);
					byte[] bytes1 = new byte[4];
					ByteBuffer.wrap(bytes1).putInt(portTo);
					byte[] payload = new byte[8];
					System.arraycopy(bytes, 0, payload, 0, bytes.length);
					System.arraycopy(bytes1, 0, payload, bytes.length, bytes1.length);
					terminal.println(Arrays.toString(payload));
					byte[] header = new byte[PacketContent.HEADERLENGTH];
					if (last == true) {
						header[9] = 1;
						last = false;
					}
					byte[] buffer = new byte[header.length + payload.length];
					System.arraycopy(header, 0, buffer, 0, header.length);
					System.arraycopy(payload, 0, buffer, header.length, payload.length);
					packet = new DatagramPacket(buffer, buffer.length, dstAddress);
					try {
						socket.send(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void firstPort(int x, int destination) {
		int[] values = routerPorts.get(x);
		int[] nextValues = routerNext.get(x);
		int i = 0;
		for (i = 0; i < 4; i++) {
			if (nextValues[i] == destination) {
				break;
			}
		}
		int leave = values[i];
		dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, leave);
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(leave);
		byte[] bytes1 = new byte[4];
		ByteBuffer.wrap(bytes1).putInt(destination);
		byte[] payload = new byte[8];
		System.arraycopy(bytes, 0, payload, 0, bytes.length);
		System.arraycopy(bytes1, 0, payload, bytes.length, bytes1.length);
		terminal.println(Arrays.toString(payload));
		byte[] header = new byte[PacketContent.HEADERLENGTH];
		byte[] buffer = new byte[header.length + payload.length];
		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(payload, 0, buffer, header.length, payload.length);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, dstAddress);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Controller(Terminal t, int port, int[][] route) throws SocketException {

		this.terminal = t;
		this.port = port;
		this.route = route;
		socket = new DatagramSocket(port);
		listener.go();
	}

	public void setup(DatagramPacket packet, int router) {

		byte[] in = packet.getData();
		int routerCount = in[1];
		int value;
		int[] ports = new int[4];
		int[] next = new int[4];
		byte[] temp = new byte[4];
		int ind = 0;
		int inde = 0;
		for (int i = 10; i < 26; i = i + 2) {
			if (i < 18) {
				temp[2] = in[i];
				temp[3] = in[i + 1];
				value = new BigInteger(temp).intValue();
				ports[ind] = value;
				ind++;
			} else {
				temp[2] = in[i];
				temp[3] = in[i + 1];
				value = new BigInteger(temp).intValue();
				next[inde] = value;
				inde++;
			}
		}
		int[] times = new int[4];
		int count = 0;
		for (i = 26; i < 30; i++) {
			byte input = in[i];
			int time = (byte) input;
			times[count] = time;
			count++;
		}
		terminal.println(Arrays.toString(ports));
		routerPorts.put(routerCount, ports);
		terminal.println(Arrays.toString(next));
		routerNext.put(routerCount, next);
		terminal.println(Arrays.toString(times));
		routerTime.put(routerCount, times);
	}

	public static void main(String[] args) {

		routerPorts = new Hashtable<Integer, int[]>();
		routerNext = new Hashtable<Integer, int[]>();
		routerTime = new Hashtable<Integer, int[]>();
		route = new int[2][10];
		Terminal terminal = new Terminal("Controller");
		try {
			(new Controller(terminal, port, route)).start();
		} catch (SocketException e) {
		}
	}

	private synchronized void start() {
		boolean x = true;
		while (x) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
