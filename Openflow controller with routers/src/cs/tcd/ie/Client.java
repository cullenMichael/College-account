
/**
 * 
 */
package cs.tcd.ie;

import java.net.DatagramSocket;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;

import tcdIO.*;

/**
 *
 * Client class
 * 
 * An instance accepts user input
 *
 */

public class Client extends Node implements Runnable {
	static final int DEFAULT_SRC_PORT1 = 40788;
	static final int SERVER_PORT = 50000;
	static final int DEFAULT_DST_PORT = 40005;
	static final String DEFAULT_DST_NODE = "localhost";
	public static int[] listOfPorts;
	public static Client[] clients;
	public static int port;
	public static int portUsed;
	public static boolean isPicked = false;
	public int ports;
	public static byte ack = -1;
	public byte acks;
	public static String[] waiting;
	public String[] iswaiting;
	public boolean check = false;
	public String s1;
	public String s2;
	int number;
	timer t;
	static timer t1;
	static int[] goTo = { 40000, 40001, 40005, 40009, 40010, 40012, 40015, 40019, 40022, 40023 };
	DatagramSocket temp;
	DatagramSocket temp1;
	Terminal terminal;
	InetSocketAddress dstAddress;
	DatagramPacket packet;
	Thread thread;
	static Client c;
	DatagramPacket p;

	Client(Terminal terminal, String dstHost, int dstPort, int port, byte ack, String[] waiting, timer t1) {
		try {
			this.ports = port;
			this.acks = ack;
			this.iswaiting = waiting;
			this.terminal = terminal;
			this.t = t1;
			dstAddress = new InetSocketAddress(dstHost, dstPort);
			socket = new DatagramSocket(port);
			listener.go();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public void onReceipt(DatagramPacket packet) {

		StringContent content = new StringContent(packet);
		terminal.println("\nReceived: " + content.toString());
		terminal.println("String to send: ");
	}

	public void sending(byte[] a, int q, byte[] b) {

		int v = 0;
		int temp;
		int mod = 10000;
		for (int i = 0; i < 5; i++) {
			temp = b[i] - '0';
			v += temp * mod;
			mod = mod / 10;
		}
		packet = null;
		byte[] payload = a;
		byte[] header = null;
		byte[] buffer = null;
		int portUsed = clients[q].ports;
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(portUsed);
		byte[] serv = new byte[4];
		ByteBuffer.wrap(serv).putInt(v);
		header = new byte[PacketContent.HEADERLENGTH];
		header[1] = bytes[2];
		header[2] = bytes[3];
		header[3] = this.acks;
		header[7] = serv[2];
		header[8] = serv[3];
		buffer = new byte[header.length + payload.length];
		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(payload, 0, buffer, header.length, payload.length);
		packet = new DatagramPacket(buffer, buffer.length, dstAddress);
		try {
			check = false;
			terminal.println("" + this.ports);
			socket.send(packet);
			Thread thread = new Thread(t);
			thread.start();
			thread.stop();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			int input = 10;
			listOfPorts = new int[input];
			clients = new Client[input];
			waiting = new String[10];
			port = DEFAULT_SRC_PORT1;
			for (int i = 0; i < input; i++) {
				port = port + 1;
				Terminal t = new Terminal("client" + i);
				c = new Client(t, DEFAULT_DST_NODE, goTo[i], port, ack, waiting, t1);
				clients[i] = c;
				new Thread(c).start();
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		boolean x = true;
		while (x) {
			String name = terminal.getTitle();
			s1 = terminal.readString("String to send: ");
			String l = name.substring(name.length() - 1);
			number = Integer.parseInt(l);
			s2 = terminal.readString("Enter Destination Port: ");
			terminal.println(s1);
			sending(s1.getBytes(), number, s2.getBytes());
		}
	}
}