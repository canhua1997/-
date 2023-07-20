package Assignment3;

import java.net.InetAddress;

public class PingMessage {
    public InetAddress host;
    int port;
    byte[] message;

    public PingMessage(InetAddress host, int port, byte[] message) {
        this.host = host;
        this.port = port;
        this.message = message;
    }

    public InetAddress getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getMessage() {
        return new String(message);
    }
}
