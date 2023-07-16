package P12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    public static void main(String[] args) {
        int portNumber = 10000;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("服务器正在监听端口" + portNumber);

            //接收客户端的请求
            Socket accept = serverSocket.accept();
            System.out.println("客户端已链接");

            //获取输入流和输出流
            final BufferedReader in = new BufferedReader(new InputStreamReader(accept.getInputStream()));
            final PrintStream out = new PrintStream(accept.getOutputStream());

            String line;
            while ((line = in.readLine()) != null){
                System.out.println("收到客户端的行: " + line);

                out.println("服务器已收到你的行： " + line);
            }
            in.close();
            out.close();
            accept.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
