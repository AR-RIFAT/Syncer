package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxml/starter.fxml"));
        primaryStage.setTitle("Syncer");
        Scene scene = new Scene(root, 640, 402);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.NONE, "All Clipboard Data will be lost. Confirm ?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {

                    System.out.println("Closing...");
                    Helper.close = true;

                    if(Helper.hostOrClient.equals("host")){
                        Helper.isHost = false;
                        try {
                            new Socket("127.0.0.1", Helper.port);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        for (Socket socket : Helper.clients) {
                            endReceiverThread(socket);
                        }
                    }
                    if(Helper.hostOrClient.equals("client")){
                        endReceiverThread(Starter.clientSocket);
                    }

                    primaryStage.close();
                }

            }
        });
    }

    private void endReceiverThread(Socket socket){
        try {
            PrintStream ps = new PrintStream(socket.getOutputStream());
            ps.println("#$$$#");
            ps.flush();
        } catch (IOException eb2) {
            eb2.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
