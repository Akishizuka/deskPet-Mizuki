package aki;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatWindow {
    private TextArea chatArea;
    private TextField chatInput;
    private PrintWriter writer;
    private Scanner input;
    private String clientId;
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("^\\[(.*?)\\](.*)$");

    public ChatWindow(PrintWriter writer, Scanner input, String clientId) {
        this.writer = writer;
        this.input = input;
        this.clientId = clientId;
        initUI();
        startReceivingMessages();
    }

    private void initUI() {
        // 美化聊天显示区域
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setStyle("-fx-background-color: #e0f7fa; " + // 浅蓝色背景
                "-fx-border-color: #0277bd; " + // 深蓝色边框
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 10px; " +
                "-fx-padding: 15px; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #01579b; " + // 深蓝色文字
                "-fx-highlight-fill: #b3e5fc; " + // 高亮背景色
                "-fx-highlight-text-fill: #01579b; " + // 高亮文字色
                "-fx-wrap-text: true;");

        // 美化输入框
        chatInput = new TextField();
        chatInput.setStyle("-fx-background-color: #b3e5fc; " + // 浅蓝色背景
                "-fx-border-color: #0277bd; " + // 深蓝色边框
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 10px; " +
                "-fx-padding: 12px; " +
                "-fx-font-family: 'Segoe UI'; " +
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #01579b; " + // 深蓝色文字
                "-fx-prompt-text-fill: #0277bd;"); // 提示文字颜色

        chatInput.setOnAction(e -> {
            String message = chatInput.getText();
            if (!message.isEmpty()) {
                chatArea.appendText("You: " + message + "\n");
                writer.println(message);
                writer.flush();
                chatInput.clear();
            }
        });

        VBox root = new VBox(chatArea, chatInput);
        root.setSpacing(15);
        root.setPadding(new javafx.geometry.Insets(20));
        root.setStyle("-fx-background-color:rgb(63, 8, 90); " + // 蓝色背景
                "-fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 0);");

        Scene scene = new Scene(root, 300, 420);
        scene.setFill(Color.TRANSPARENT);

        // 添加小部分紫色调，例如窗口标题栏
        Stage chatStage = new Stage();
        chatStage.setTitle("Chat Window");

        Image icon = new Image(getClass().getResourceAsStream("/images/micon.png"));
        chatStage.getIcons().add(icon);

        chatStage.setScene(scene);
        chatStage.setX(1200);
        chatStage.setY(400);
        chatStage.setAlwaysOnTop(true);
        chatStage.getScene().getRoot().setStyle("-fx-background-color:rgb(129, 197, 237); " +
                "-fx-border-color: #7e57c2; " + // 紫色边框
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 10px;");
        chatStage.show();
    }

    private void startReceivingMessages() {
        Thread receiveThread = new Thread(() -> {
            while (input.hasNextLine()) {
                String rawMessage = input.nextLine();
                Matcher matcher = MESSAGE_PATTERN.matcher(rawMessage);

                if (matcher.matches()) {
                    String senderId = matcher.group(1);
                    String messageContent = matcher.group(2);

                    if (senderId.equals(clientId)) {
                        continue;
                    } else {
                        String displayMessage = "Other: " + messageContent;
                        Platform.runLater(() -> {
                            chatArea.appendText(displayMessage + "\n");
                        });
                    }
                } else {
                    Platform.runLater(() -> {
                        chatArea.appendText("System: " + rawMessage + "\n");
                    });
                }
            }
        });
        receiveThread.start();
    }
}