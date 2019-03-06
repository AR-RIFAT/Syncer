package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {

    private ServerSocket serverSocket;

    ServerThread(ServerSocket ss){
        this.serverSocket = ss;
    }

    @Override
    public void run() {
        while (true){
            try {
                System.out.println("Waiting...");
                Socket socket = serverSocket.accept();
                System.out.println("Accepted connection : " + socket);

                if(!Helper.isHost) break;

                Helper.clients.add(socket);


                new ReceiverThread(socket).start();

            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
