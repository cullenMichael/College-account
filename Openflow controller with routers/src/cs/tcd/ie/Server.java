package cs.tcd.ie;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

import tcdIO.Terminal;

public class Server extends Node {
	static final int DEFAULT_PORT = 50000;
	public static int counter = 0;
	InetSocketAddress dstAddress;
	static final String DEFAULT_DST_NODE = "localhost";
	public static byte serverACK = 1;
	public String[] received;
	public static int[] ports;
	public static int[] clientack;
	Terminal terminal;
	int ack;

	/*
	 * 
	 */
	Server(Terminal terminal, int port, String[] rec) {
		try {
			this.terminal = terminal;
			socket = new DatagramSocket(port);
			this.received = rec;
			listener.go();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		try {
			
			this.notify();
			StringContent content = new StringContent(packet);
			terminal.println(content.toString());
			
			
			int incomes = packet.getPort();
		

			int count = 0;
			for (int i = 0; i < 10; i++) {
				if (received[i] != null) {
					count++;
				}
			}
			if (clientack[ack] >= 9) {
				clientack[ack] = 0;
				serverACK = 0;
				count = 0;
			}
			
			
			
			byte[] income = packet.getData();
			
	//		if (income[3] != clientack[ack]){
				//terminal.println("SERVER DOES NOT EXPECT THIS PACKET!");
				income[0] = 1;
				
//			}
			
			byte[] head = new byte[10];
			System.arraycopy(income, 0, head, 0, head.length);
			if (head[6] != 0) {
				int value = head[6];
				received[value] = content.toString();
			} else {
				received[serverACK] = content.toString();
			}
			byte sender = (byte) clientack[ack]++;
			head[4] = sender;
			dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, 40000);
			packet = new DatagramPacket(head, head.length, dstAddress);
			//socket.send(packet);
			serverACK++;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void start() throws Exception {
		boolean x = true;
		while (x) {
			this.wait();
		}
	}

	/*
	 * 
	 */
	public static void main(String[] args) {
		try {
	
			clientack = new int[10];
			String[] rec = new String[10];
			Terminal terminal = new Terminal("Server");
			(new Server(terminal, DEFAULT_PORT, rec)).start();
			terminal.println("Program completed");

		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}