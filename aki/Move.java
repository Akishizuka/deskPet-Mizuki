package aki;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Move extends Thread{
    private long time;
    private ImageView imageView;
    private int direID;
    double x;
    double maxx;
    double width;
    Rectangle2D screenBounds=Screen.getPrimary().getVisualBounds();
    Stage stage;
    private EventListener listener;
    boolean exit;
    private final Object positionLock=new Object();

    public Move(long time,ImageView imgview,int dire, Stage primaryStage,EventListener el)
    {
        this.time=time;
        imageView=imgview;
        direID=dire;
        stage=primaryStage;
        listener=el;
    }
    public void run()
    {
        imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, 
            e->{exit=true;listener.mainimg(0,direID);});
        while(!exit)
        {

            width=imageView.getBoundsInLocal().getMaxX();
            x=stage.getX();
            maxx=screenBounds.getMaxX();
            double speed=2;
            //根据方向判断在屏幕边缘的情况什么时候停什么时候可以向反方向走
            if(x+speed+width>=maxx&&direID==0)
            {
                this.interrupt();
                listener.mainimg(0,direID);
                return;
            }
            else if(x-speed<=0&&direID==1)
            {
                this.interrupt();
                listener.mainimg(0,direID);
                return;
            }
            else if(time<=0)
            {
                this.interrupt();
                listener.mainimg(0,direID);
                return;
            }
            synchronized(positionLock)
            {
            if(direID==1)
            {
                stage.setX(x-speed);
            }
            else if(direID==0)
            {
                stage.setX(x+speed);
            }
            }
            time-=10;
            try{
                Thread.sleep(10);
            }catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
