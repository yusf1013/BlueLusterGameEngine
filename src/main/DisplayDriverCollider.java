package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class DisplayDriverCollider extends DisplayDriverGUI {

    public void fillTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Color fill, GraphicsContext gc)
    {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
            gc.setFill(Color.RED);
            gc.strokePolygon(new double[]{x1, x2, x3},
                    new double[]{y1,y2,y3}, 3);
    }

}
