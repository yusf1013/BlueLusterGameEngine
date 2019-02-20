package main;

import dataLoader.ModelLoader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import mathHandler.VectorGeometry;
import threeDItems.Mat4x4;
import threeDItems.Mesh;
import threeDItems.Triangle;
import threeDItems.Vec3d;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;



public class DisplayDriver extends VectorGeometry {

    Mesh meshCube = new Mesh();
    Mat4x4 matProj = new Mat4x4(), matRotZ=new Mat4x4(), matRotX=new Mat4x4();
    float fTheta=0, fNear, fFar, fFov, fAspectRatio, fFovRad, screenHeight=550, screenWidth=370;
    Vec3d vCamera = new Vec3d(0,0,0), light_direction = new Vec3d(0,1,-1);
    double lightIntensity=1;
    List<Triangle> triToRaster = new Vector<>();


    public DisplayDriver()
    {
        //Load model from file
        ModelLoader dd = new ModelLoader();
        meshCube=dd.meshLoader("cube.obj");
        //Projection matrix
        matProj = makeProjectionMatrix(90.0f, (float)screenHeight / (float)screenWidth, 0.1f, 1000.0f);
    }

    void test(GraphicsContext gc)
    {
       // gc.setFill(Color.WHITE);
        //gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        /*gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);*/
        /*gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);*/
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);

    }

    boolean onUserUpdate(float fElapsedTime, GraphicsContext gc)
    {
        // Clear Screen
        //root.getChildren().clear();
        gc.clearRect(0,0,800,800);
        // Set up rotation matrices
        Mat4x4 matRotZ, matRotX;
        fTheta += 1.0f * fElapsedTime;
        matRotZ = makeZRotationMatrix(fTheta * 0.5f );
        matRotX = makeXRotationMatrix(fTheta );

        Mat4x4 matTrans;
        matTrans = makeTranslation(0.0f, 0.0f, 300.0f);

        Mat4x4 matWorld;
        matWorld = makeIdentity();	// Form World Matrix

        /*matWorld = multiplyMatrix(matWorld, matTrans); // My version
        matWorld = multiplyMatrix(matWorld, matRotX); // My version
        matTrans = makeTranslation(0.0f, 0.0f, 15.0f);
        matWorld = multiplyMatrix(matWorld, matTrans); // My version*/

        matWorld = multiplyMatrix(matRotZ, matRotX); // Transform by rotation
        matWorld = multiplyMatrix(matWorld, matTrans); // Transform by translation

        // Draw Triangles
        for (int i=0; i<meshCube.tris.size(); i++)
        {
            Triangle triProjected=new Triangle(), triTranslated=new Triangle();
            triTranslated.p[0] = multiplyMatrixAndVector(matWorld, meshCube.tris.elementAt(i).p[0]);
            triTranslated.p[1] = multiplyMatrixAndVector(matWorld, meshCube.tris.elementAt(i).p[1]);
            triTranslated.p[2] = multiplyMatrixAndVector(matWorld, meshCube.tris.elementAt(i).p[2]);

            // Use Cross-Product to get surface normal
            Vec3d normal = new Vec3d(), line1= new Vec3d(), line2= new Vec3d();
            line1=vectorSub(triTranslated.p[1], triTranslated.p[0]);
            line2=vectorSub(triTranslated.p[2], triTranslated.p[0]);
            normal = crossProduct(line1, line2);

            // You normally need to normalise a normal!
            normal = normaliseVector(normal);

            // Get Ray from triangle to camera
            Vec3d vCameraRay = vectorSub(triTranslated.p[0], vCamera);

            // If ray is aligned with normal, then triangle is visible
            if (dotProduct(normal, vCameraRay) < 0.0f)
            {

                triProjected.p[0]=MultiplyMatrixVector(triTranslated.p[0], matProj);
                triProjected.p[1]=MultiplyMatrixVector(triTranslated.p[1],  matProj);
                triProjected.p[2]=MultiplyMatrixVector(triTranslated.p[2],  matProj);

                // Scale into view
                triProjected.p[0].x += 1.0f; triProjected.p[0].y += 1.0f;
                triProjected.p[1].x += 1.0f; triProjected.p[1].y += 1.0f;
                triProjected.p[2].x += 1.0f; triProjected.p[2].y += 1.0f;
                triProjected.p[0].x *= 0.5f * (float)screenWidth;
                triProjected.p[0].y *= 0.5f * (float)screenHeight;
                triProjected.p[1].x *= 0.5f * (float)screenWidth;
                triProjected.p[1].y *= 0.5f * (float)screenHeight;
                triProjected.p[2].x *= 0.5f * (float)screenWidth;
                triProjected.p[2].y *= 0.5f * (float)screenHeight;

                /*//illumination
                Color color = triProjected.getColor();
                float dp = Math.max(0.1f,Math.abs(dotProduct(normal, normaliseVector(light_direction))));
                triProjected.setColor(new Color(color.getRed()*dp, color.getGreen()*dp, color.getBlue()*dp, color.getOpacity()*1));

                if(dp==0.1f )
                {
                    System.out.println("Tri: " + i + " dp= " +(dotProduct(normal, normaliseVector(light_direction))));
                    System.out.println(triTranslated);
                    System.out.println("Normal: " + normal);
                    continue;
                }*/

                Color color = triProjected.getColor();
                light_direction = normaliseVector(light_direction);
                //float dp = Math.max(0.1f, dotProduct(light_direction, normal));
                float dp = Math.max(0.1f, (float)Math.abs((double)dotProduct(light_direction, normal)));
                triProjected.setColor(new Color(color.getRed()*dp, color.getGreen()*dp, color.getBlue()*dp, color.getOpacity()*1));
                //dotProduct(normal, vCameraRay);

                triToRaster.add(triProjected);
            }
        }

        triToRaster.sort(Comparator.comparing(Triangle::getMidPointz).reversed());

        for(int i=0; i<triToRaster.size(); i++)
        {
            // Rasterize Triangle
            fillTriangle(triToRaster.get(i).p[0].x, triToRaster.get(i).p[0].y,
                    triToRaster.get(i).p[1].x, triToRaster.get(i).p[1].y,
                    triToRaster.get(i).p[2].x, triToRaster.get(i).p[2].y, gc, triToRaster.get(i).getColor(), triToRaster.get(i).getColor());
        }
        triToRaster.clear();
        return true;
    }
    public Vec3d MultiplyMatrixVector(Vec3d i, Mat4x4 m)
    {
        Vec3d o = new Vec3d(0,0,0);
        o.x = i.x * m.m[0][0] + i.y * m.m[1][0] + i.z * m.m[2][0] + m.m[3][0];
        o.y = i.x * m.m[0][1] + i.y * m.m[1][1] + i.z * m.m[2][1] + m.m[3][1];
        o.z = i.x * m.m[0][2] + i.y * m.m[1][2] + i.z * m.m[2][2] + m.m[3][2];
        float w = i.x * m.m[0][3] + i.y * m.m[1][3] + i.z * m.m[2][3] + m.m[3][3];

        if (w != 0.0f)
        {
            o.x /= w; o.y /= w; o.z /= w;
        }
        return o;
    }

    public static void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, GraphicsContext gc, Color fill, Color stroke)
    {
        //gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        Polygon tri = new Polygon(x1, y1, x2, y2, x3, y3);
        double []x=new double[3];
        x[0]=(double)x1; x[1]=(double)x2; x[2]=(double)x3;
        double []y=new double[3];
        y[0]=(double)y1; y[1]=(double)y2; y[2]=(double)y3;
        tri.setFill(fill);
        tri.setStroke(stroke);
        //gc.fillPolygon(x, y, 3);
        gc.strokePolygon(x, y, 3);
        System.out.println(x[0] + " " + x[1] + " " + x[2]);
        System.out.println(y[0] + " " + y[1] + " " + y[2]);
        /*gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);*/
    }

    /*public static void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Group root, Color fill, Color stroke)
    {
        glColor3d(Color.GRAY.getRed(), Color.GRAY.getGreen(), Color.GRAY.getBlue()); // sets color to black.
        glBegin(GL_TRIANGLE_STRIP); // draw in triangle strips
        glVertex2f(x1, y1); // top of the roof
        glVertex2f(x2, y2); // left corner of the roof
        glVertex2f(x3, y3); // right corner of the roof
        glEnd();
    }*/



}
