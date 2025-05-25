package aki;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class mizukiPet extends Application {
    private double dragStartX;
    private double dragStartY;
    private boolean isDragging = false;
    private static final double DRAG_THRESHOLD = 5.0;
    private boolean clickPrevented = false;

    private static ImageView imageView;
    EventListener listener;
    double xoffset = 0;
    double yoffset = 0;
    private final Object positionLock = new Object();

    private PrintWriter writer;
    private Scanner input;
    private String clientId;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        try {
            // 连接到服务器
            Socket socket = new Socket("localhost", 12345);
            writer = new PrintWriter(socket.getOutputStream());
            input = new Scanner(socket.getInputStream());

            // 等待服务器分配ID
            if (input.hasNextLine()) {
                String idLine = input.nextLine();
                if (idLine.startsWith("[Server]YourID:")) {
                    clientId = idLine.substring("[Server]YourID:".length());
                    System.out.println("My client ID: " + clientId);
                }
            }

            // 创建聊天窗口
            new ChatWindow(writer, input, clientId);

            // 创建图片视图
            Image image = new Image(getClass().getResourceAsStream("/gifs0/0.gif"));
            imageView = new ImageView(image);
            imageView.setY(0);
            imageView.setX(0);
            imageView.setLayoutX(7);
            imageView.setLayoutY(32);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);

            listener = new EventListener(imageView);
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, listener);

            entity en = new entity(imageView, listener, primaryStage);
            en.addMessageBox("博士,想我了没有?");

            AnchorPane root = new AnchorPane(en.getMessageBox(), en.getImageView());
            GravityPhysics gravityPhysics = new GravityPhysics(primaryStage, imageView);
            en.setGravityPhysics(gravityPhysics);

            root.setStyle("-fx-background:transparent;");

            // 拖动实现
            root.setOnMousePressed(event -> {
                dragStartX = event.getSceneX();
                dragStartY = event.getSceneY();
                isDragging = false;
                clickPrevented = false;
                xoffset = event.getSceneX();
                yoffset = event.getSceneY();
                event.consume();
            });
            root.setOnMouseDragged(event -> {
                double dx = Math.abs(event.getSceneX() - dragStartX);
                double dy = Math.abs(event.getSceneY() - dragStartY);
                if (dx > DRAG_THRESHOLD || dy > DRAG_THRESHOLD) {
                    isDragging = true;
                    clickPrevented = true;
                    synchronized (positionLock) {
                        primaryStage.setX(event.getScreenX() - xoffset);
                        primaryStage.setY(event.getScreenY() - yoffset);
                        en.savedX = primaryStage.getX();
                        en.savedY = primaryStage.getY();
                        gravityPhysics.updatePosition(primaryStage.getY());
                        en.getGravityPhysics().startFalling();
                    }
                    event.consume();
                }
            });

            root.setOnMouseReleased(event -> {
                if (isDragging) {
                    isDragging = false;
                    event.consume();
                }
            });

            root.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                if (clickPrevented) {
                    clickPrevented = false;
                    event.consume();
                }
            });

            Scene scene = new Scene(root, 300, 420);
            primaryStage.setTitle("Mizuki pet");
            primaryStage.setScene(scene);
            primaryStage.setX(850);
            primaryStage.setY(400);
            primaryStage.setAlwaysOnTop(true);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/miizuki.png")));
            scene.setFill(null);
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                en.end();
                writer.close();
                input.close();
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            primaryStage.show();
            en.setTray(primaryStage);
            Thread thread = new Thread(en);
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}