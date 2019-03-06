package sample;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ReceiverThread extends Thread {

    private Socket socket;

    ReceiverThread(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {

        StringBuilder temp = new StringBuilder();

        boolean end = false;

        try {
            Scanner scan = new Scanner(socket.getInputStream());

            while (true) {

                if (scan.hasNextLine()) {
                    while (true){
                        String ss = scan.nextLine();
                        if(ss.equals("#$$$#")){
                            end = true;
                            break;
                        }
                        try{
                            if("#$#".equals(ss.substring(ss.length()-3, ss.length()))){
                                temp.append(ss.substring(0,ss.length()-3)).append("\n");
                                break;
                            }
                        }catch (Exception e){
                            temp.append("\n");
                            continue;
                        }
                        temp.append(ss).append("\n");
                    }
                    if(end){
                        break;
                    }
                    Helper.received = temp.toString();
                    temp.setLength(0);
                    Helper.newText += 1;

                }

                if (Helper.close) break;

                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}