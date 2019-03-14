package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import mathHandler.ThreeDObjectTransformations;
import mathHandler.VectorGeometry;
import rendererEngine.Camera;
import threeDItems.Matrix4by4;
import threeDItems.Mesh;
import threeDItems.Triangle;
import threeDItems.Vec3d;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;



public class DisplayDriverGUI extends VectorGeometry {

    Matrix4by4 matProj, matRotZ=new Matrix4by4(), matRotX=new Matrix4by4();
    float fTheta=0, screenHeight=550, screenWidth=550, walkSpeed=20f;
    Camera camera = new Camera(-3,5,-5);
    ThreeDObjectTransformations trans = new ThreeDObjectTransformations();
    Vec3d light_direction = new Vec3d(-3,5,-5);
    List<Triangle> triToRaster = new Vector<>();
    static double cursorX, cursorY, mouseSensitivity=0.5f, height=550, width=550;

    public DisplayDriverGUI()
    {
        camera.yaw=30*3.14159f/180f;
        camera.pitch=30*3.14159f/180f;
        matProj = makeProjectionMatrix(90.0f, (float)screenHeight / (float)screenWidth, 0.1f, 1000.0f);
        System.out.println("Object has been loaded successfullyyyy");
        //camera.cameraLookingAtVector=new Vec3d(0,-1,0);
    }

    public void drawMesh (Mesh m, float fElapsedTime, GraphicsContext gc)
    {
        addMeshForDrawing(m, fElapsedTime);
        drawQueuedTriangles(gc);
    }


    boolean onUserUpdate(float fElapsedTime, GraphicsContext gc, List<Mesh> meshList)
    {
        for(Mesh m:meshList)
        {
            addMeshForDrawing(m, fElapsedTime);
        }
        drawQueuedTriangles(gc);
        /*handleUserInputs(fElapsedTime);
        meshCube.runScript();*/
        return true;
    }

    public boolean addMeshForDrawing(Mesh meshCube, float fElapsedTime)
    {
        Matrix4by4 matWorld = meshCube.getWorldMat();

        Matrix4by4 matView = camera.createViewMat();
        for (int i=0; i<meshCube.tris.size(); i++)
        {
            Triangle triTranslated=new Triangle(), triProjected = new Triangle();
            triTranslated=trans.transform(matWorld, meshCube.tris.elementAt(i));

            Vec3d normal, line1, line2;
            line1=vectorSub(triTranslated.p[1], triTranslated.p[0]);
            line2=vectorSub(triTranslated.p[2], triTranslated.p[0]);
            normal = normaliseVector(crossProduct(line1, line2));

            // Get Ray from triangle to camera
            Vec3d vCameraRay = vectorSub(triTranslated.p[0], camera.position);

            //if (dotProduct(normal, vCameraRay) < 0.0f)
            {
                Triangle arr[] = null;
                triTranslated= trans.transform(matView, triTranslated);

                arr=zClip(triTranslated, i);

                if(arr==null || arr.length==0)
                    continue;
                for(int mor=0; mor<arr.length; mor++)
                {
                    triToRaster.add(projectTriangle(arr[mor], normal));
                }
            }
        }
        return true;
    }

    public boolean drawQueuedTriangles(GraphicsContext gc)
    {
        triToRaster.sort(Comparator.comparing(Triangle::getMidPointz).reversed());

        for(int i=0; i<triToRaster.size(); i++)
        {
            /*fillTriangle(triToRaster.get(i).p[0].x, triToRaster.get(i).p[0].y,
                    triToRaster.get(i).p[1].x, triToRaster.get(i).p[1].y,
                    triToRaster.get(i).p[2].x, triToRaster.get(i).p[2].y,  triToRaster.get(i).getColor());*/
            Triangle triProjected = triToRaster.get(i);
            triProjected.p[0].y*=-1; triProjected.p[1].y*=-1; triProjected.p[2].y*=-1;
            triProjected.p[0].x += 1.0f; triProjected.p[0].y += 1.0f;
            triProjected.p[1].x += 1.0f; triProjected.p[1].y += 1.0f;
            triProjected.p[2].x += 1.0f; triProjected.p[2].y += 1.0f;
            triProjected.p[0].x *= 0.5f * (float)screenWidth;
            triProjected.p[0].y *= 0.5f * (float)screenHeight;
            triProjected.p[1].x *= 0.5f * (float)screenWidth;
            triProjected.p[1].y *= 0.5f * (float)screenHeight;
            triProjected.p[2].x *= 0.5f * (float)screenWidth;
            triProjected.p[2].y *= 0.5f * (float)screenHeight;
            //triProjected.p[0].y*=-1; triProjected.p[1].y*=-1; triProjected.p[2].y*=-1;
            triToRaster.set(i, triProjected);

            fillTriangle(triToRaster.get(i).p[0].x, triToRaster.get(i).p[0].y, triToRaster.get(i).p[0].z,
                    triToRaster.get(i).p[1].x, triToRaster.get(i).p[1].y, triToRaster.get(i).p[1].z,
                    triToRaster.get(i).p[2].x, triToRaster.get(i).p[2].y, triToRaster.get(i).p[2].z, triToRaster.get(i).getColor(), gc);

        }
        triToRaster.clear();
        return  true;
    }

    public Triangle projectTriangle(Triangle triTranslated, Vec3d normal)
    {
        Triangle triProjected=trans.transform(matProj, triTranslated);
        triProjected.p[0] = vectorDiv(triProjected.p[0], triProjected.p[0].w);
        triProjected.p[1] = vectorDiv(triProjected.p[1], triProjected.p[1].w);
        triProjected.p[2] = vectorDiv(triProjected.p[2], triProjected.p[2].w);
        Color color = triProjected.getColor();
        light_direction = normaliseVector(light_direction);
        float dp = Math.max(0.1f, (float) Math.abs((double) dotProduct(light_direction, normal)));
        //float dp=1.0f;
        triProjected.setColor(new Color(color.getRed() * dp, color.getGreen() * dp, color.getBlue() * dp, color.getOpacity() * 1));
        return triProjected;
    }

    public Triangle[] zClip(Triangle tri, int indind)
    {
        Vector <Vec3d> vec = new Vector<>();
        vec.add(tri.p[0]);
        vec.add(tri.p[1]);
        vec.add(tri.p[2]);

        int count=0;

        if(tri.p[0].z<0.1)
            count++;
        if(tri.p[1].z<0.1)
            count++;
        if(tri.p[2].z<0.1)
            count++;
        if(count==3)
            return null;

        if(count==1)
        {
            int lowestOne;
            if(tri.p[0].z<tri.p[1].z)
                lowestOne=0;
            else
                lowestOne=1;
            if(tri.p[lowestOne].z>tri.p[2].z)
                lowestOne=2;
            Vec3d tempV=tri.p[lowestOne];
            vec.remove(lowestOne);
            float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).z, tempV.x, tempV.z);
            float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).z, tempV.y, tempV.z);
            Vec3d new2 = new Vec3d(newX, newY, 0.1f);
            newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).z, tempV.x, tempV.z);
            newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).z, tempV.y, tempV.z);
            Vec3d new3 = new Vec3d(newX, newY, 0.1f);
            vec.add(new2);
            vec.add(new3);
            Triangle triArr[] = new Triangle[2];
            triArr[0]=new Triangle(vec.elementAt(0), vec.elementAt(1), vec.elementAt(2));
            triArr[0].setColor(tri.getColor());
            triArr[1]=new Triangle(vec.elementAt(1), vec.elementAt(2), vec.elementAt(3));
            triArr[1].setColor(tri.getColor());
            return  triArr;
        }
        else if(count==2)
        {
            tri.p[0].z*=-1; tri.p[1].z*=-1; tri.p[2].z*=-1;
            int lowestOne;
            if(tri.p[0].z<tri.p[1].z)
                lowestOne=0;
            else
                lowestOne=1;
            if(tri.p[lowestOne].z>tri.p[2].z)
                lowestOne=2;
            tri.p[0].z*=-1; tri.p[1].z*=-1; tri.p[2].z*=-1;
            Vec3d tempV=tri.p[lowestOne];
            vec.remove(lowestOne);
            float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).z, tempV.x, tempV.z);
            float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).z, tempV.y, tempV.z);
            Vec3d new2 = new Vec3d(newX, newY, 0.1f);
            newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).z, tempV.x, tempV.z);
            newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).z, tempV.y, tempV.z);
            Vec3d new3 = new Vec3d(newX, newY, 0.1f);
            vec.add(lowestOne, new3);
            vec.add(lowestOne, new2);

            Vector<Vec3d> vec2= new Vector<>();
            vec2.add(new2);
            vec2.add(new3);
            vec2.add(lowestOne, tempV);

            Triangle triArr[] = new Triangle[1];
            triArr[0]= new Triangle(vec2.elementAt(0), vec2.elementAt(1), vec2.elementAt(2));
            triArr[0].setColor(tri.getColor());
            return  triArr;
        }
        Triangle arr[] = new Triangle[1];
        arr[0]=tri;
        return  arr;
    }

    public float zcalc(float xoy1, float z1, float xoy3, float z3)
    {
        if(z1==z3) {
            System.out.println("You died");
        }
        return (((xoy1-xoy3)*(0.1f-z3)/(z1-z3))+xoy3);
    }

    public static void fillTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Color fill, GraphicsContext gc)
    {
        gc.setStroke(Color.BLACK);
        gc.setFill(fill);
        gc.setLineWidth(1);
        gc.fillPolygon(new double[]{x1, x2, x3},
                new double[]{y1,y2,y3}, 3);
        /*gc.fillPolygon(new double[]{0, width/2.0, width},
                new double[]{height/2.0,height,height/2.0}, 3);*/

        /*glColor3d(fill.getRed(), fill.getGreen(), fill.getBlue());
        glBegin(GL_TRIANGLE_STRIP);
        glVertex3f(x1, y1, z1);
        glVertex3f(x2, y2, z2);
        glVertex3f(x3, y3, z3);
        glEnd();*/
    }

}
