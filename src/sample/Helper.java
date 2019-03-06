package sample;

import java.net.Socket;
import java.util.ArrayList;

public class Helper {
    public static boolean close = false;
    public static boolean isHost = false;

    public static int port = 0;
    public static int newText = 0;

    public static String hostOrClient = "";
    public static String received = "";

    public static ArrayList<Socket> clients = new ArrayList<>();
}
