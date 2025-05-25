package aki;

import javafx.scene.input.MouseEvent;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class EventListener implements EventHandler<MouseEvent>{
    private ImageView imageView;
	int gifID = 0;//图片编号
	double time = 3;//播放动画的时间
    int direID=0;
    private void calculateDire()
    {
        Random rand=new Random();
        direID=rand.nextInt(2);
    }
    public EventListener(ImageView imgview)
    {
        imageView=imgview;
    }
    public void handle(MouseEvent e)
    {
        if(gifID!=0)return;
        double x=e.getX();
        double y=e.getY();

        System.out.println(x+" "+y);
        //选择动作
        behavior(x,y);
        loadImg(gifID,time);
    }

    public void behavior(double x,double y)
    {
        if(x>64&y>20&x<140&y<180)
        {
            gifID=1;
            time=4.18;
        }
    }

    public void loadImg(int gifID,double time)
    {
        this.gifID=gifID;
        if(gifID!=0)
        {
            Image newimage;
            calculateDire();
            // String path = "/gifs" + direID + "/" + gifID + ".gif";
            // newimage = GlobalImageCache.getImage(path); // 从缓存获取

            newimage=new Image(this.getClass().getResourceAsStream("/gifs"+direID+"/"+gifID+".gif"));//test
            imageView.setImage(newimage);

            //不同图片的偏移和编辑如下
            if(gifID==1)
            {
                imageView.setFitHeight(236);
                imageView.setFitWidth(236);
                imageView.setLayoutX(0);
                imageView.setLayoutY(0);
                imageView.setPreserveRatio(true);
            }
            if(gifID==2)
            {
                imageView.setFitHeight(190);
                imageView.setFitWidth(190);
                imageView.setLayoutX(0);
                imageView.setLayoutY(98);
                imageView.setPreserveRatio(true);
            }
            if(gifID==3)
            {
                imageView.setFitHeight(210);
                imageView.setFitWidth(210);
                imageView.setLayoutX(0);
                imageView.setLayoutY(148);
                imageView.setPreserveRatio(true);
            }
            if(gifID==4)
            {
                imageView.setFitHeight(236);
                imageView.setFitWidth(236);
                imageView.setLayoutX(20);
                imageView.setLayoutY(5);
                imageView.setPreserveRatio(true);
            }

            
            new Timeline(new KeyFrame(Duration.seconds(time),ae->mainimg(0,direID))).play();
            
        }
    }
    // public void loadImg(int gifID,double time,int dire, Image newImage)
    // {
    //     this.gifID=gifID;
    //     if(gifID!=0)
    //     {
    //         imageView.setImage(newImage);
    //         if(gifID==1)
    //         {
    //             imageView.setImage(newImage);
    //             imageView.setFitHeight(236);
    //             imageView.setFitWidth(236);
    //             imageView.setLayoutX(0);
    //             imageView.setLayoutY(0);
    //             imageView.setPreserveRatio(true);
    //         }
            
    //         new Timeline(new KeyFrame(Duration.seconds(time),ae->mainimg(0,dire))).play();
    //     }
    // }

    // public void mainimg(int key,int diretion) {
    //     Image newimage;
    //     newimage=new Image(this.getClass().getResourceAsStream("/gifs"+diretion+"/"+key+".gif"));
    //     imageView.setImage(newimage);
    //     if(key==0)gifID=0;
    //     if(gifID==0)
    //     {
    //         imageView.setFitHeight(200);
    //         imageView.setFitWidth(200);
    //         imageView.setLayoutX(7);
    //         imageView.setLayoutY(32);
    //         imageView.setPreserveRatio(true);
    //     }
    // }
     public void mainimg(int key,int direction) {
        String path = "/gifs" + direction + "/" + key + ".gif";
        Image newimage = GlobalImageCache.getImage(path); // 从缓存获取
        imageView.setImage(newimage);
        if (key == 0) gifID = 0;
        if(gifID==0)
        {
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            imageView.setLayoutX(7);
            imageView.setLayoutY(32);
            imageView.setPreserveRatio(true);
        }

        if(key==88)
        {
            imageView.setFitHeight(220);
            imageView.setFitWidth(220);
            imageView.setLayoutX(10);
            imageView.setLayoutY(35);
            imageView.setPreserveRatio(true);
        }
    }
    
}
