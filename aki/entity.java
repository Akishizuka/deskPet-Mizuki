package aki;

import java.awt.CheckboxMenuItem;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;

import javafx.scene.shape.Polygon;  // 核心导入
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;  // 用于屏幕边界矩形
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;



public class entity implements Runnable{
    private ImageView imageView;
    private EventListener listener;
    private VBox messageBox;
    private CheckboxMenuItem walkable;
    private CheckboxMenuItem autoplay;
    //private CheckboxMenuItem say;

    private Stage primaryStage;
    private GravityPhysics gravityPhysics;
    double savedX;
    double savedY;

    Thread thread;
    double x;

    String[]sayStrings={
        "博士~"
    };
    public entity(ImageView view,EventListener el,Stage s)
    {
        imageView=view;
        listener=el;
        primaryStage=s;
    }

    
    //添加系统托盘
    public void setTray(Stage stage)
    {
        //写个菜单
        SystemTray tray=SystemTray.getSystemTray();
        BufferedImage image;
        try{
            PopupMenu popupMenu=new PopupMenu();
            popupMenu.setFont(new Font("微软雅黑",Font.PLAIN,18));
            walkable=new CheckboxMenuItem("自行走动");
            autoplay=new CheckboxMenuItem("自娱自乐");
            //以上两个不能同时进行
            CheckboxMenuItem gravity = new CheckboxMenuItem("启用重力");
            walkable.addItemListener(itemListener->
            {
                if(walkable.getState())
                {
                    autoplay.setEnabled(false);
                    //say.setEnabled(false);
                }
                else
                {
                    autoplay.setEnabled(true);
                    //say.setEnabled(true);
                }
            });
            autoplay.addItemListener(itemListener->
            {
                if(autoplay.getState())
                {
                    walkable.setEnabled(false);
                }
                else
                {
                    walkable.setEnabled(true);
                }
            });

            gravity.addItemListener(itemListener->{
                if(gravity.getState())
                {
                    enableGravity();
                }
                else
                {
                    disableGravity();
                }
            });

            MenuItem itemShow=new MenuItem("显示");
			itemShow.addActionListener(e -> Platform.runLater(() -> {
                stage.setX(savedX);
                stage.setY(savedY);
                stage.show();}));

            MenuItem itemHide=new MenuItem("隐藏");

            itemHide.addActionListener(e->{
                savedX = stage.getX();
                savedY = stage.getY();
                Platform.setImplicitExit(false);
                Platform.runLater(()->stage.hide());});


            MenuItem itemExit=new MenuItem("退出");
            itemExit.addActionListener(e->{end();});


            popupMenu.add(walkable);
            popupMenu.add(autoplay);
            popupMenu.add(gravity);
            popupMenu.addSeparator();
            popupMenu.add(itemShow);
            popupMenu.add(itemHide);
            popupMenu.add(itemExit);
            image=ImageIO.read(getClass().getResourceAsStream("/images/miizuki.png"));
            if(image==null)
            {
                System.out.println("加载托盘失败");
            }
            TrayIcon trayIcon=new TrayIcon(image,"桌面水月！",popupMenu);
            trayIcon.setToolTip("桌面水月！");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void end() {
        listener.mainimg(88,0);
        Platform.runLater(()->setMsg("再见博士~"));
        new Timeline(new KeyFrame(Duration.seconds(1.0), ae->System.exit(0))).play();
    }


    public void addMessageBox(String message)
    {
        Label bubble=new Label(message);
        bubble.setPrefWidth(100);
        bubble.setWrapText(true);
        bubble.setStyle(
            "-fx-background-color:rgb(87, 175, 239);" +  // 背景色
            "-fx-background-radius: 10;" +      // 圆角
            "-fx-padding: 5 10 5 10;" +         // 内边距（上右下左）
            "-fx-border-color:rgb(255, 255, 255);" +      // 边框颜色
            "-fx-border-radius: 10;" +          // 边框圆角
            "-fx-border-width: 1;"              // 边框宽度
        );
        bubble.setPadding(new Insets(7));//标签内间距宽度
        bubble.setFont(new javafx.scene.text.Font(14));
        Polygon triangle=new Polygon(
            0.0,0.0,
            8.0,10.0,
            16.0,0.0
        );
        triangle.setFill(Color.DARKTURQUOISE);
        messageBox = new VBox();

        messageBox.getChildren().addAll(bubble,triangle);
        messageBox.setAlignment(Pos.BOTTOM_CENTER);
        messageBox.setStyle("-fx-background:transparent;");
        //设置对于父容器的位置
        messageBox.setLayoutX(0);
      	messageBox.setLayoutY(0);
      	messageBox.setVisible(true);
        //设置气泡时间
        new Timeline(new KeyFrame(
            Duration.seconds(5),
            ae->{messageBox.setVisible(false);}
        )).play();
    }

    public void setMsg(String msg) {
       Label label=(Label)messageBox.getChildren().get(0);
       label.setText(msg);
       messageBox.setVisible(true);
       new Timeline(new KeyFrame(
        Duration.seconds(4),
        ae->{messageBox.setVisible(false);}
       )).play();
    }

    public void run()
    {
        while(true)
        {
            Random rand=new Random();
            long time=(rand.nextInt(15)+10)*1000;
            System.out.println("Waiting time:"+time);
            if(walkable.getState()&&listener.gifID==0)
            {
                walk();
            }
            else if(autoplay.getState()&&listener.gifID==0)
            {
                play();
            }
            // else if(say.getState() && listener.gifID == 0) {
			// 	String str = sayStrings[0];
			// 	Platform.runLater(() ->setMsg(str));
			// }
            try
            {
                Thread.sleep(time);
            }catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    void walk()
    {
        Rectangle2D screenBounds=Screen.getPrimary().getVisualBounds();
        x=primaryStage.getX();
        double maxx=screenBounds.getMaxX();
        double width=imageView.getBoundsInLocal().getWidth();
        Random rand=new Random();
        double speed=10;
        //先加载方向再判断是否能走
        int direID=rand.nextInt(2);
        
        if(direID==0&&x+speed+width>=maxx)
        {
            return;
        }
        else if(direID==1&&x-speed<=0)
        {
            return;
        }
        long time=(rand.nextInt(4)+3)*1000;
        System.out.println("Walking time:"+time);
        String path = "/gifs" + direID + "/move.gif";
        Image newimage = GlobalImageCache.getImage(path);

        imageView.setImage(newimage);


        imageView.setFitHeight(210);
        imageView.setFitWidth(210);
        imageView.setLayoutX(0);
        imageView.setLayoutY(20);
        imageView.setPreserveRatio(true);

        Move move=new Move(time,imageView,direID,primaryStage,listener);
        thread=new Thread(move);
        thread.start();
    }

    void play()
    {
        Random rand=new Random();
        double time=4;
        int gifID;
        gifID=rand.nextInt(3)+2;
        if(gifID==2)
        {
            time=13.34*2;
        }
        if(gifID==3)
        {
            time=10.68*3;
        }
        if(gifID==4)
        {
            time=8.68*2;
        }
        listener.loadImg(gifID, time);

    }
    
    public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public VBox getMessageBox() {
		return messageBox;
	}

	public void setMessageBox(VBox messageBox) {
		this.messageBox = messageBox;
	}

    public void setGravityPhysics(GravityPhysics gravityPhysics)
    {
        this.gravityPhysics=gravityPhysics;
    }
    public void enableGravity() {
        gravityPhysics.isOpen=true;
        gravityPhysics.startFalling();  
    }

    public void disableGravity() {
        gravityPhysics.stopFalling();
    }
    public GravityPhysics getGravityPhysics()
    {
        return gravityPhysics;
    }
}

