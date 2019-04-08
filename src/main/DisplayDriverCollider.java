package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DisplayDriverCollider extends DisplayDriverGUI {

    public void fillTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Color fill, GraphicsContext gc)
    {
        gc.setStroke(Color.BLACK);
        //System.out.println("fixie");
        //gc.setFill(fill);
        gc.setLineWidth(1);
        /*if(fill==Color.TRANSPARENT)
        {*/
            gc.setFill(Color.RED);
            gc.strokePolygon(new double[]{x1, x2, x3},
                    new double[]{y1,y2,y3}, 3);

            /*gc.setFill(fill);
            gc.fillOval(100,0,100,100);*/
        /*}
        else  {
            gc.setFill(fill);
            gc.fillPolygon(new double[]{x1, x2, x3},
                new double[]{y1,y2,y3}, 3);
            System.out.println("Drawing gray: ");
        }*/
    }

    public void useless()
    {
        int i=0;
        i++;
    }
}
