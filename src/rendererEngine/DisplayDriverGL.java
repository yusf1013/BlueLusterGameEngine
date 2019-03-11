package rendererEngine;

import dataHandler.ModelLoader;
import dataHandler.scriptClassLoader;
import inputHandler.KeyboardHandler;
import javafx.scene.paint.Color;
import mathHandler.ThreeDObjectTransformations;
import mathHandler.VectorGeometry;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import threeDItems.Mat4x4;
import threeDItems.Mesh;
import threeDItems.Triangle;
import threeDItems.Vec3d;

import java.nio.DoubleBuffer;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;



public class DisplayDriverGL extends VectorGeometry {

    //Mesh meshCube = new Mesh();
    //Vector<Mesh> meshVector = new Vector<>();
    Vector<Mesh> meshVector;
    Mat4x4 matProj;
    float screenHeight=600, screenWidth=600, walkSpeed=20f;
    Camera camera = new Camera();
    ThreeDObjectTransformations trans = new ThreeDObjectTransformations();
    Vec3d light_direction = new Vec3d(0,0,-1);
    long window;
    List<Triangle> triToRaster = new Vector<>();
    private GLFWKeyCallback keyCallback;
    double cursorX, cursorY, mouseSensitivity=0.5f;
    MasterScript ms;

    public DisplayDriverGL(long window)
    {
        ModelLoader modelLoader = new ModelLoader();
        meshVector=modelLoader.loadAll();
        meshVector=modelLoader.loadAll();
        ms=new MasterScript(meshVector);


        //ms=(MasterScript)Class.forName("rendererEngine.MasterScript").newInstance();

        //meshCube=modelLoader.meshLoader("toNotDisplay\\axis.obj", true);

        matProj = makeProjectionMatrix(90.0f, (float)screenHeight / (float)screenWidth, 0.1f, 1000.0f);
        glfwSetKeyCallback(window, keyCallback = new KeyboardHandler(window));
        System.out.println("Object has been loaded successfully");
        this.window=window;

        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, xBuffer, yBuffer);
        cursorX = xBuffer.get(0);
        cursorY = yBuffer.get(0);
    }


    boolean onUserUpdate(float fElapsedTime)
    {
        int i=0;
        for(Mesh meshCube: meshVector)
        {
            drawMesh(meshCube, fElapsedTime);
            i++;
        }
        draw();
        handleUserInputs(fElapsedTime);
        ms.run();
        return true;
    }

    public void draw()
    {
        for(int i=0; i<triToRaster.size(); i++)
        {
            /*fillTriangle(triToRaster.get(i).p[0].x, triToRaster.get(i).p[0].y,
                    triToRaster.get(i).p[1].x, triToRaster.get(i).p[1].y,
                    triToRaster.get(i).p[2].x, triToRaster.get(i).p[2].y,  triToRaster.get(i).getColor());*/
            fillTriangle(triToRaster.get(i).p[0].x, triToRaster.get(i).p[0].y, triToRaster.get(i).p[0].z,
                    triToRaster.get(i).p[1].x, triToRaster.get(i).p[1].y, triToRaster.get(i).p[1].z,
                    triToRaster.get(i).p[2].x, triToRaster.get(i).p[2].y, triToRaster.get(i).p[2].z, triToRaster.get(i).getColor());
        }
        triToRaster.clear();
    }

    public static double getCursorPosX(long windowID) {
        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(windowID, posX, null);
        return posX.get(0);
    }

    public static double getCursorPosY(long windowID) {
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(windowID, null, posY);
        return posY.get(0);
    }

    public void handleUserInputs(float fElapsedTime)
    {

        if(KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE))
        {
            glfwSetWindowShouldClose(window, true);
            KeyboardHandler.keys[GLFW_KEY_ESCAPE]=false;
        }
        if(KeyboardHandler.isKeyDown(GLFW_KEY_UP))
            camera.position.y+=walkSpeed*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_DOWN))
            camera.position.y-=walkSpeed*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A))
            camera.position=vectorAdd(camera.position, vectorMul(normaliseVector(crossProduct(camera.vLookDir, camera.vUp)), fElapsedTime*walkSpeed));
        if(KeyboardHandler.isKeyDown(GLFW_KEY_D))
            camera.position=vectorSub(camera.position, vectorMul(normaliseVector(crossProduct(camera.vLookDir, camera.vUp)), fElapsedTime*walkSpeed));
        Vec3d vForward = vectorMul(camera.vLookDir, walkSpeed * fElapsedTime);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_W))
            camera.position=vectorAdd(camera.position, vForward);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_S))
            camera.position=vectorSub(camera.position, vForward);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT))
            camera.yaw-=walkSpeed/4f*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT))
            camera.yaw+=walkSpeed/4f*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_R))
            camera.pitch-=walkSpeed/4f*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_F))
            camera.pitch+=walkSpeed/4f*fElapsedTime;

        double newCursorY=getCursorPosY(window), newCursorX=getCursorPosX(window);
        if(cursorY != newCursorY || cursorX!= newCursorX ) {
            double yDifference = (cursorY - newCursorY) / (double) MainGL.HEIGHT;
            double xDifference = (cursorX - newCursorX) / 400d;
            if((camera.pitch - mouseSensitivity * yDifference) <3.14/2f && (camera.pitch - mouseSensitivity * yDifference) >-3.14/2f)
                camera.pitch -= mouseSensitivity * yDifference;
            camera.yaw -= mouseSensitivity * xDifference;

        }
        if(!(newCursorY >0 && newCursorY<MainGL.HEIGHT && newCursorX>1 && newCursorX<MainGL.WIDTH)) {
            /*glfwSetCursorPos(window, MainGL.WIDTH/2, MainGL.HEIGHT/2);
            cursorY=MainGL.HEIGHT/2;
            cursorX=MainGL.WIDTH/2;*/
            cursorY=MainGL.HEIGHT/2;
            cursorX=MainGL.WIDTH/2;
            glfwSetCursorPos(window, cursorX, cursorY);
            /*cursorY=newCursorY-10;
            cursorX=newCursorX-10;*/
        }
        else
        {
            cursorY = newCursorY;
            cursorX=newCursorX;
        }
    }

    public boolean drawMesh(Mesh meshCube, float fElapsedTime)
    {
        Mat4x4 matWorld = meshCube.getWorldMat();

        /*System.out.println(meshCube.tris.elementAt(0).toString());
        System.out.println("Done With it");*/

        /*Mat4x4 matRotZ, matRotX;
        //fTheta += 1.0f * fElapsedTime;
        matRotZ = makeZRotationMatrix(fTheta * 0.5f );
        matRotX = makeXRotationMatrix(fTheta );

        Mat4x4 matTrans;
        matTrans = makeTranslation(0.0f, 0.0f, 10.0f);

        matWorld = multiplyMatrix(matRotZ, matRotX);
        matWorld = multiplyMatrix(matWorld, matTrans);*/
        //matWorld = multiplyMatrix(matWorld, mul);

        Mat4x4 matView = camera.createViewMat();
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
        //triToRaster.sort(Comparator.comparing(Triangle::getMidPointz).reversed());

        /*for(int i=0; i<triToRaster.size(); i++)
        {
            *//*fillTriangle(triToRaster.get(i).p[0].x, triToRaster.get(i).p[0].y,
                    triToRaster.get(i).p[1].x, triToRaster.get(i).p[1].y,
                    triToRaster.get(i).p[2].x, triToRaster.get(i).p[2].y,  triToRaster.get(i).getColor());*//*
            fillTriangle(triToRaster.get(i).p[0].x, triToRaster.get(i).p[0].y, triToRaster.get(i).p[0].z,
                    triToRaster.get(i).p[1].x, triToRaster.get(i).p[1].y, triToRaster.get(i).p[1].z,
                    triToRaster.get(i).p[2].x, triToRaster.get(i).p[2].y, triToRaster.get(i).p[2].z, triToRaster.get(i).getColor());
        }
        triToRaster.clear();*/
        return true;
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

    public static void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Color fill)
    {
        glColor3d(fill.getRed(), fill.getGreen(), fill.getBlue());
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glBegin(GL_TRIANGLE_STRIP);
        glVertex2f(x1, y1);
        glVertex2f(x2, y2);
        glVertex2f(x3, y3);
        glEnd();
    }

    public static void fillTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Color fill)
    {
        glColor3d(fill.getRed(), fill.getGreen(), fill.getBlue());
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        glBegin(GL_TRIANGLE_STRIP);
        glVertex3f(x1, y1, z1);
        glVertex3f(x2, y2, z2);
        glVertex3f(x3, y3, z3);
        glEnd();
    }

}
