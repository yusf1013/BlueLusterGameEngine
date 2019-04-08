package inputHandler;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
/*
        Scanner input = new Scanner(System.in);
        System.out.println(input.nextInt());
        System.exit(1);*/

        Group root = new Group();
        Canvas canvas = new Canvas( 800, 800 );
        root.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();
        primaryStage.setTitle("Hello World");
        DisplayDriver displayDriver = new DisplayDriver();
        displayDriver.onUserUpdate(0.00f, gc);
        primaryStage.setScene(new Scene(root, displayDriver.screenWidth, displayDriver.screenHeight));
        primaryStage.show();



        new AnimationTimer()
        {
            double prevTime=System.nanoTime(), timeLim=1.0/120.0d, elapsedTime=0, startTime=prevTime, currentTime=System.nanoTime();
            double temp=0;
            long count=0, c=0;
            public void handle(long currentNanoTime)
            {
                currentTime=System.nanoTime();
                elapsedTime=((double)currentNanoTime-prevTime)/10000f/100000f;
                prevTime=(double)currentNanoTime;
                //if(elapsedTime>timeLim)
                {
                    displayDriver.onUserUpdate((float)(elapsedTime), gc);
                    temp+=elapsedTime;
                    if(temp>0.25)
                    {
                        primaryStage.setTitle("FPS: " + 1.0f/elapsedTime );
                        temp=0;
                        count+= 1.0f/elapsedTime;
                        c++;
                        System.out.println(count/c);
                    }
                    elapsedTime=0;
                    //System.out.println(timeLim);
                }
                //System.out.println("fixie");
            }
        }.start();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
