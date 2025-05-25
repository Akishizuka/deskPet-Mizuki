package aki;


import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GravityPhysics {
    private Stage stage;
    private ImageView imageView;
    private double velocityY = 0;
    private double gravity = 0.1;
    private double bounceFactor = 0.7;
    private boolean isFalling = false;
    boolean isOpen=false;
    private double taskbarHeight = 40;
    private double groundY;
    private final Object positionLock=new Object();
    
    
    public GravityPhysics(Stage stage, ImageView imageView) {
        this.stage = stage;
        this.imageView = imageView;
        initialize();
    }
    
    private void initialize() {
        // 获取屏幕尺寸和任务栏高度
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        groundY = visualBounds.getMaxY()-28;
        System.out.println("Taskbar Height: " + taskbarHeight);
        System.out.println("Ground Y: " + groundY);
    }
    
    public void startFalling() {
        if(!isOpen)return;
        if (isFalling) return;
        
        isFalling = true;
        
        // 创建物理更新线程
        Thread physicsThread = new Thread(() -> {
            while (isFalling) {
                try {
                    // 每10ms更新一次物理状态，约100FPS
                    Thread.sleep(10);
                    
                    // 在JavaFX应用线程中更新UI
                    javafx.application.Platform.runLater(() -> 
                    {
                        synchronized(positionLock)
                        {
                            updatePhysics();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        
        physicsThread.setDaemon(true);
        physicsThread.start();
    }
    
    private void updatePhysics() {
        // 计算宠物底部Y坐标
        double petBottomY = stage.getY() + imageView.getFitHeight();
        

        //输出调试信息
        System.out.println("Pet Bottom Y: " + petBottomY);
        System.out.println("Ground Y: " + groundY);

        // 应用重力加速度
        velocityY += gravity;
        
        // 更新位置
        double newY=stage.getY()+velocityY;
        if(petBottomY+velocityY>=groundY)
        {
            newY=groundY-imageView.getFitHeight();
        }
        newY=Math.round(newY);
        stage.setY(newY);
        
        // 检测碰撞
        if (petBottomY >= groundY) {
            // 反弹逻辑
              // 输出碰撞时的速度和位置
            System.out.println("Collision detected! Velocity Y: " + velocityY);
            System.out.println("Pet Bottom Y: " + petBottomY);
            //反弹逻辑
            if (Math.abs(velocityY) > 1) {
                velocityY = -velocityY * bounceFactor;
                
                // 确保不穿透地面
                double correction = petBottomY - groundY;
                stage.setY(stage.getY() - correction);
                
                // 播放碰撞动画或音效
                playBounceEffect();
            } else {
                // 停止运动
                isFalling = false;
                velocityY = 0;
                
                // 确保准确停在地面上
                double correction = petBottomY - groundY;
                stage.setY(stage.getY() - correction);
            }
        }
    }
    
    private void playBounceEffect() {
        // 这里可以添加碰撞时的视觉或音效效果
        // 例如轻微改变宠物图片或播放音效
    }
    
    public boolean isFalling() {
        return isFalling;
    }
    
    public void stopFalling() {
        isOpen=false;
        isFalling = false;
        velocityY = 0;
    }
    public void updatePosition(double newY) {
    synchronized (positionLock) {
        stage.setY(newY);
        // 重置速度
        velocityY = 0;
    }
    }


}    