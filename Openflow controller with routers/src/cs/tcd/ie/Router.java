
package cs.tcd.ie;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import tcdIO.Terminal;

public class Router extends Node implements Runnable {

	static int DEFAULT_DST_PORT = 40000;
	static final int GO_To = 50000;
	static final int NEW_PORT = 40700;
	static final int CONTROLLER_PORT = 60000;
	static final String DEFAULT_DST_NODE = "localhost";
	public static byte gatewayACK = 0;
	public StringContent content;
	public static DatagramSocket soc;
	public static byte[] input;
	byte[] in;
	Terminal terminal;
	InetSocketAddress dstAddress;
	byte[] head = new byte[PacketContent.HEADERLENGTH];
	public static Router g;
	static int nextPort;
	static byte id = 0;
	byte ide;
	int next;
	boolean hit = false;
	static Router[] gate;
	int count = 0;
	private int port;
	private int up;
	private int down;
	private int left;
	private int right;
	static int[][] dest = new int[2][36];
	DatagramSocket socket1;
	DatagramSocket socket2;
	DatagramSocket socket3;
	DatagramSocket socket4;
	private DatagramSocket[] portArray;
	private byte index = 1;
	static int client = 40789;

	Router(Terminal terminal, int port, int nextPort, byte id, int up, int down, int left, int right) {
		try {
			this.terminal = terminal;
			this.port = port;
			dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, GO_To);
			this.ide = id;
			this.next = nextPort;
			this.portArray = new DatagramSocket[4];
			this.left = left;
			socket1 = new DatagramSocket(left);
			portArray[0] = socket1;
			this.up = up;
			socket1 = new DatagramSocket(up);
			portArray[1] = socket1;
			this.right = right;
			socket1 = new DatagramSocket(right);
			portArray[2] = socket1;
			this.down = down;
			socket1 = new DatagramSocket(down);
			portArray[3] = socket1;
			calculateNextPort(dest, up, down, left, right);

		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	private void tryPort(DatagramSocket soc, DatagramPacket packet) {

		try {
			soc.setSoTimeout(5);
		} catch (SocketException e1) {
		}
		try {
			soc.receive(packet);
			onReceipt(packet);
		} catch (IOException e) {
		}
	}

	public synchronized void onReceipt(DatagramPacket packet) {
		try {
			terminal.println("packet is here");
			this.notify();
			content = new StringContent(packet);
			terminal.println("" + content.toString());
			int ports = packet.getPort();

			if ((ports >= 40789) && (ports < 60000)) {// from clients
				dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, 60000);
				byte[] input = packet.getData();
				System.arraycopy(input, 0, head, 0, head.length);
				in = content.toString().getBytes();
				byte[] dest = new byte[4];
				dest[2] = input[7];
				dest[3] = input[8];
				byte[] payload = dest;
				byte[] header = new byte[10];
				byte x = this.ide;
				header[7] = x;
				byte[] buffer = new byte[header.length + payload.length];
				System.arraycopy(header, 0, buffer, 0, header.length);
				System.arraycopy(payload, 0, buffer, header.length, payload.length);
				packet = new DatagramPacket(buffer, buffer.length, dstAddress);
				hit = true;
				portArray[0].send(packet);
			} else if (ports == 60000) { // from controller
				byte[] p = packet.getData();
				byte[] nextPort = new byte[4];
				nextPort[2] = p[12];
				nextPort[3] = p[13];
				int value = new BigInteger(nextPort).intValue();
				nextPort[2] = p[16];
				nextPort[3] = p[17];
				int goTo = new BigInteger(nextPort).intValue();
				this.next = goTo;
				for (int i = 0; i < 4; i++) {
					if (portArray[i].getPort() == value) {
						socket1 = portArray[i];
					}
				}
				terminal.println("ports" + value + " " + goTo);
				count++;
				if (p[9] == 1) {
					startSend();
				}
			} else { // from router

				byte[] payload = content.toString().getBytes();
				dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, this.next);
				byte[] header = head;
				// header[9] = 1;
				byte[] buffer = new byte[header.length + payload.length];
				System.arraycopy(header, 0, buffer, 0, header.length);
				System.arraycopy(payload, 0, buffer, header.length, payload.length);
				packet = new DatagramPacket(buffer, buffer.length, dstAddress);
				socket1.send(packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void startSend() {

		dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, this.next);
		byte[] header = head;
		byte[] buffer = new byte[header.length + in.length];
		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(in, 0, buffer, header.length, in.length);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, dstAddress);
		try {
			socket1.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void calculateNextPort(int[][] dest, int up, int down, int left, int right) {

		Random rand = new Random();
		byte[] port = new byte[8];
		byte[] next = new byte[8];
		byte[] lenght = new byte[4];
		int l = left - 2;
		if ((left - 40000) % 4 == 0) {
			lenght[0] = 0;
			if ((left - 40000) % 12 == 0) {
				l = client;
				client = client + 1;
			}
		} else {
			lenght[0] = (byte) ((byte) rand.nextInt(9) + 1);
		}
		System.arraycopy(toByte(left), 2, port, 0, 2);
		System.arraycopy(toByte(l), 2, next, 0, 2);
		int u = up - 10;
		if (((up - 39997) % 4 == 0)) {
			lenght[1] = 0;
			if (up < 40010) {
				u = client;
				client = client + 1;
			}
		} else {
			lenght[1] = (byte) ((byte) rand.nextInt(9) + 1);
		}
		System.arraycopy(toByte(up), 2, port, 2, 2);
		System.arraycopy(toByte(u), 2, next, 2, 2);
		int r = right + 2;
		if ((right - 39998) % 12 == 0) {
			lenght[2] = 0;
			r = client;
			client = client + 1;
		} else {
			lenght[2] = (byte) ((byte) rand.nextInt(9) + 1);
		}
		System.arraycopy(toByte(right), 2, port, 4, 2);
		System.arraycopy(toByte(r), 2, next, 4, 2);
		int d = down + 10;
		if (((down - 40015) % 4 == 0) && (down >= 40015)) {
			lenght[3] = 0;
			if (down > 40014) {
				d = client;
				client = client + 1;
			}
		} else {
			lenght[3] = (byte) ((byte) rand.nextInt(9) + 1);
		}
		System.arraycopy(toByte(down), 2, port, 6, 2);
		System.arraycopy(toByte(d), 2, next, 6, 2);
		byte[] array = new byte[20];
		System.arraycopy(port, 0, array, 0, port.length);
		System.arraycopy(next, 0, array, port.length, next.length);
		System.arraycopy(lenght, 0, array, 16, lenght.length);
		terminal.println("array: " + Arrays.toString(array));
		terminal.println("" + left + " " + l + "\n" + up + " " + u + "\n" + right + " " + r + "\n" + down + " " + d);
		dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, 60000);
		byte[] header = new byte[10];
		header[1] = this.ide;
		byte[] buffer = new byte[header.length + array.length];
		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(array, 0, buffer, header.length, array.length);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, dstAddress);
		try {
			portArray[0].send(packet);
		} catch (IOException e) {
		}
	}

	private byte[] toByte(int x) {
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(x);
		return bytes;
	}

	public static void main(String[] args) {
		try {
			gate = new Router[6];
			int up;
			int down;
			int left;
			int right;
			int count = 0;
			for (int i = 0; i < 6; i++) {
				Terminal terminal = new Terminal("Router" + i);
				id = (byte) ((byte) i + 1);
				left = DEFAULT_DST_PORT + count;
				count++;
				up = DEFAULT_DST_PORT + count;
				count++;
				right = DEFAULT_DST_PORT + count;
				count++;
				down = DEFAULT_DST_PORT + count;
				count++;
				g = new Router(terminal, DEFAULT_DST_PORT, nextPort, id, up, down, left, right);
				gate[i] = g;
				new Thread(g).start();
			}
			System.out.println(Arrays.deepToString(dest));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		boolean x = true;
		while (x) {
			for (int i = 0; i < portArray.length; i++) {
				DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
				tryPort(portArray[i], packet);
			}
		}
	}
}