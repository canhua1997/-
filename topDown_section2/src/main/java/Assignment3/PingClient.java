package Assignment3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;



public class PingClient implements Runnable{
    public static void main(String[] args) {
        String host = null;
        int port = 0;
        try {
            host =args[0];
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("please give port number as integer.");
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Need two argument: remoteHost remotePort");
            System.exit(-1);
        }

        System.out.println("Contacting host "+ host + " at port" + port);
        PingClient client = new PingClient(host,port);
        client.run();
    }

    //create UDP socket;

    DatagramSocket socket;

    static final int MAX_PING_LEN = 1024;

    String remoteHost;

    int remotePort;

    static final int NUM_PINGS=10;

    int numReplies = 0;

    static boolean[] replies = new boolean[NUM_PINGS];

    static long[] rtt = new long[NUM_PINGS];

    static final int TIMEOUT = 1000; //milliseconds

    static final int REPLY_TIMEOUT = 5000;

    public PingClient(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public void run() {
        createSocket();
        try {
            socket.setSoTimeout(TIMEOUT);
        } catch (SocketException e) {
            System.out.println("Error setting timeout: " + e);
        }

        for (int i = 0; i < NUM_PINGS; i++) {
            Date now =new Date();
            String message = "PING " + i + " "+now.getTime()+" ";
            rtt[i] = 1000000;

            PingMessage ping =null;
            try {
                ping = new PingMessage(InetAddress.getByName(remoteHost),remotePort,message.getBytes());
            } catch (UnknownHostException e) {
                System.out.println("Cannot find host: "+e);
            }
            sendPing(ping);

            try {
                PingMessage reply = receivePing();
                handleReply(reply.getMessage());
            } catch (SocketTimeoutException e) {
                System.out.println("Error setting timeout: " + e);
            }
        }

        try {
            socket.setSoTimeout(REPLY_TIMEOUT);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (numReplies < NUM_PINGS) {
            try {
                PingMessage reply = receivePing();
                handleReply(reply.getMessage());
            } catch (SocketTimeoutException e) {
                numReplies = NUM_PINGS;
            }
        }

        System.out.println("received packets: " + Arrays.toString(replies));
        System.out.println("round-trip time: " +  Arrays.toString(rtt));

    }

    private void handleReply(String message) {
        String[] tem = message.split(" ");
        int pingNumber = Integer.parseInt(tem[1]);
        long then = Long.parseLong(tem[2]);
        replies[pingNumber] = true;
        Date now = new Date();
        rtt[pingNumber] = now.getTime() - then;
        numReplies++;
    }

    public void createSocket(){
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("Error creating socket: " + e);
        }
    }

    public void createSocket(int port){
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("Error creating socket: " + e);
        }
    }


    public void sendPing(PingMessage ping){
        InetAddress host = ping.getHost();
        int port = ping.getPort();
        String message = ping.getMessage();

        try {
            DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), host, port);
            socket.send(datagramPacket);
            System.out.println("Sent message to "+ host + ":" + port);
        } catch (IOException e) {
            System.out.println("Error sending packet: " + e);
        }
    }

    public PingMessage receivePing() throws SocketTimeoutException {
        byte[] recBuf = new byte[MAX_PING_LEN];
        PingMessage pingMessage = null;
        try {
            DatagramPacket datagramPacket = new DatagramPacket(recBuf, MAX_PING_LEN);
            socket.receive(datagramPacket);
            System.out.println("Received message from "+ datagramPacket.getAddress() + ":" + datagramPacket.getPort());
            pingMessage = new PingMessage(
                    datagramPacket.getAddress(),
                    datagramPacket.getPort(),
                    datagramPacket.getData()
            );

        } catch (SocketTimeoutException e) {
            throw e;
        } catch (IOException e ){
            System.out.println("Error reading from socket: "+ e);
        }
        return pingMessage;
    }
}
