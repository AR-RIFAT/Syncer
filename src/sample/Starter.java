package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

public class Starter implements Initializable {

    @FXML
    VBox container;
    @FXML
    Button clear, btnHost, btnClient, btnConnect;
    @FXML
    HBox defaultHb, hostHb, clientHb;
    @FXML
    Label labelHostIp, labelHostPort;
    @FXML
    TextField inIpAddress, inPort;

    private String paste = "";
    private int received = 0;
    private int nodeCount = 0;
    private int itemCount = 0;
    private ArrayList<TextArea> contents = new ArrayList<>();
    private ServerSocket serverSocket;
    public static Socket clientSocket;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clear.getStyleClass().add("clear");

        hostHb.setVisible(false);
        clientHb.setVisible(false);

        Thread board = new Thread(){

            @Override
            public void run() {

                int first = 0;
                String check="";

                while(true){
                    try {
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        paste = (String) clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor);
                    } catch (Exception e) {
                        System.out.println("Found problem during accessing clipboard - Line 68");
                    }

                    if(first == 0){
                        check = paste;
                        first+=1;
                        continue;
                    }

                    if(paste.equals("0")) break;
                    if(Helper.close) break;


                    if(!check.equals(paste)){

                        addTextblock(0);

                        check = paste;
                    }

                    if(received != Helper.newText){
                        addTextblock(1);
                        received = Helper.newText;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        board.start();

        clear.setOnAction(e->{
            try {
                container.getChildren().remove(0,nodeCount);
                System.out.println("Cleared");
                nodeCount = 0;
                itemCount = 0;
                contents.clear();
            }catch (Exception ex){
                ex.printStackTrace();
                System.out.println("Nothing to clear");
            }
        });

        btnHost.setOnAction(e->{
            defaultHb.setVisible(false);
            hostHb.setVisible(true);
            Helper.hostOrClient = "host";
            Helper.isHost = true;
            try {
                serverSocket = new ServerSocket(0);
                Helper.port = serverSocket.getLocalPort();
                System.out.println("Port : "+ Helper.port);
                labelHostIp.setText("Ipv4 : "+ InetAddress.getLocalHost().getHostAddress());
                labelHostPort.setText("Port : "+ Integer.toString(Helper.port));
            } catch (IOException e1){
                e1.printStackTrace();
            }

            new ServerThread(serverSocket).start();

        });
        btnClient.setOnAction(e->{
            defaultHb.setVisible(false);
            clientHb.setVisible(true);

            Helper.hostOrClient = "client";
        });

        btnConnect.setOnAction(e->{
            try{
                clientSocket = new Socket(inIpAddress.getText(), Integer.parseInt(inPort.getText()));
                new ReceiverThread(clientSocket).start();
            } catch (IOException ed){
                ed.printStackTrace();
            }
        });


    }

    public void labelHost() throws IOException {
        hostHb.setVisible(false);
        defaultHb.setVisible(true);
        Helper.isHost = false;
        Helper.hostOrClient = "";
        new Socket("127.0.0.1", Helper.port);
    }
    public void labelClient() {
        clientHb.setVisible(false);
        defaultHb.setVisible(true);
        Helper.hostOrClient = "";
    }

    private void addTextblock(int ck) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("New Copy");

                TextArea latest = new TextArea();
                latest.setEditable(false);
                Font f = new Font("Candara", 15);
                latest.setFont(f);
                if(ck==0){
                    latest.setText(paste);
                }else {
                    latest.setText(Helper.received);
                }
                contents.add(latest);

                Button time = new Button(new SimpleDateFormat("    HH : mm : ss     ").format(Calendar.getInstance().getTime()));
                time.setStyle("-fx-border-color: #5499c7; -fx-border-width: 1px;" +
                        "-fx-background-color: #fadbd8; -fx-font-size: 1em; -fx-text-fill: #000000");

                Button b = new Button("Send");
                b.setId(Integer.toString(itemCount));
                b.setMinWidth(82);
                b.setMinHeight(26);
                b.getStyleClass().add("addBobOk");

                b.setOnAction(eb -> {
                    String st = contents.get(Integer.parseInt(b.getId())).getText();
                    System.out.println(st);
                    if (Helper.hostOrClient.equals("host")) {
                        for (Socket socket : Helper.clients) {
                            sendBlock(socket, st);
                        }
                    }
                    if (Helper.hostOrClient.equals("client")) {
                        sendBlock(clientSocket, st);
                    }
                });

                Button b2 = new Button("Copy All");
                b2.setId(Integer.toString(itemCount));
                itemCount++;
                b2.setMinWidth(102);
                b2.setMinHeight(26);
                b2.getStyleClass().add("copyall");

                b2.setOnAction(eb -> {
                    Toolkit.getDefaultToolkit()
                            .getSystemClipboard()
                            .setContents(
                                    new StringSelection(contents.get(Integer.parseInt(b2.getId())).getText()),
                                    null
                            );
                });

                HBox hBox = new HBox(time, b, b2);

                HBox.setMargin(b, new Insets(0, 0, 0, 262));
                HBox.setMargin(b2, new Insets(0, 0, 0, 26));

                VBox vBox = new VBox(hBox, new Label(""), latest, new Label(""));

                javafx.geometry.Insets insp = new Insets(12, 12, 12, 12);

                vBox.setPadding(insp);
                vBox.setStyle("-fx-border-color: #000000; -fx-border-width: 2px;" +
                        "-fx-background-color: #aeb6bf;");

                container.getChildren().add(vBox);
                container.getChildren().add(new Label(""));

                nodeCount += 2;

            }
        });

    }

    private void sendBlock(Socket socket, String st){
        try {
            PrintStream ps = new PrintStream(socket.getOutputStream());
            ps.println(st +"#$#");
            ps.flush();
        } catch (IOException eb2) {
            eb2.printStackTrace();
        }
    }

}

// When host goes back exit all receiver
// give an confirmation of successful connection
// send File