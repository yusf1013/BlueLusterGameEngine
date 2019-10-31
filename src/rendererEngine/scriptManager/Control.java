package rendererEngine.scriptManager;
import dataHandler.ModelLoader;
import javafx.scene.paint.Color;
import mathHandler.VectorGeometry;
import rendererEngine.Camera;
import rendererEngine.itemBag.ItemBag;
import threeDItems.Mesh;
import threeDItems.Triangle;
import threeDItems.Vec3d;

import java.io.FileNotFoundException;
import java.util.HashMap;



public class Control {
    public static Camera camera = ItemBag.camera;
    private static float x,y,z,xt,yt,zt;
    private static float tx,ty,tz,txt,tyt,tzt, pitch, yaw;
    private static Mesh bMesh;
    private static Vec3d position;
    private static HashMap<String, Integer> keyMap;
    public static char forwardKey='y', backwardKey='h', leftKey='g', rightKey='j';


    public static void bindCamera(Mesh mesh)
    {
        if(ItemBag.isCameraBound)
        {
            return;
        }

        ItemBag.isCameraBound=true;
        /*x=mesh.getxTranslation();
        y=mesh.getyTranslation();
        z=mesh.getzTranslation();*/
        //new shit please delete this
        //if(mesh.getxTranslation()<0)
        x=mesh.getxTranslation()-camera.position.x;
        //if(mesh.getyTranslation()<0)
        y=mesh.getyTranslation()-camera.position.y;
        //if(mesh.getzTranslation()<0)
        z=mesh.getzTranslation()-camera.position.z;

        xt=mesh.getxTheta();
        yt=mesh.getyTheta();
        zt=mesh.getzTheta();
        bMesh=mesh;
        position=camera.position;
        pitch= camera.getPitch();
        yaw= camera.getYaw();
    }

    protected static void boundedCameraControls()
    {
        tx=bMesh.getxTranslation();
        ty=bMesh.getyTranslation();
        tz=bMesh.getzTranslation();
        txt=bMesh.getxTheta();
        tyt=bMesh.getyTheta();
        tzt=bMesh.getzTheta();
        Vec3d vec = VectorGeometry.multiplyMatrixAndVector(bMesh.getWorldMat(), position);
        camera.position=vec;
        camera.position.x-=x;
        camera.position.y-=y;
        camera.position.z-=z;
        //camera.position=new Vec3d(position.x+tx-x, position.y+ty-y, position.z+tz-z);
        camera.setPitch(pitch+txt-xt);
        camera.setYaw(yaw+tyt-yt);
    }

    public static boolean isKeyPressed(int keyCode)
    {
        return ItemBag.keys[keyCode];
    }

    public static boolean isKeyPressed(char keyCode)
    {
        int intCode=Character.toUpperCase(keyCode);
        return isKeyPressed(intCode);
    }

    public static void walkControlScheme(Mesh mesh, float walkSpeed, float mouseSensitivity)
    {
        VectorGeometry vg = new VectorGeometry();
        Vec3d vForward = vg.vectorMul(camera.vLookDir, walkSpeed);
        Vec3d leftRight = vg.vectorMul(vg.normaliseVector(vg.crossProduct(camera.vLookDir, camera.vUp)), walkSpeed);
        if(isKeyPressed(leftKey))
            mesh.move(leftRight.x, leftRight.y, leftRight.z);
        if(isKeyPressed(rightKey))
        {
            mesh.move(-leftRight.x, -leftRight.y, -leftRight.z);
        }

        if(isKeyPressed(forwardKey))
            mesh.move(vForward.x, vForward.y, vForward.z);
        if(isKeyPressed(backwardKey))
            mesh.move(-vForward.x, -vForward.y, -vForward.z);

        if(isKeyPressed(263))
            mesh.setyTheta(mesh.getyTheta() - mouseSensitivity);
        if(isKeyPressed(262))
            mesh.setyTheta(mesh.getyTheta() + mouseSensitivity);

    }

    public static void walkControlScheme(Mesh mesh, float walkSpeedh, float walkSpeedf, float mouseSensitivity)
    {
        VectorGeometry vg = new VectorGeometry();
        //Vec3d vForwardh = vg.vectorMul(camera.vLookDir, walkSpeedh);
        Vec3d leftRighth = vg.vectorMul(vg.normaliseVector(vg.crossProduct(camera.vLookDir, camera.vUp)), walkSpeedh);
        Vec3d vForwardf = vg.vectorMul(camera.vLookDir, walkSpeedf);
        //Vec3d leftRightf = vg.vectorMul(vg.normaliseVector(vg.crossProduct(camera.vLookDir, camera.vUp)), walkSpeedf);

        if(isKeyPressed(leftKey))
            mesh.move(leftRighth.x, leftRighth.y, leftRighth.z);
        if(isKeyPressed(rightKey))
            mesh.move(-leftRighth.x, -leftRighth.y, -leftRighth.z);

        if(isKeyPressed(forwardKey))
            mesh.move(vForwardf.x, vForwardf.y, vForwardf.z);
        if(isKeyPressed(backwardKey))
            mesh.move(-vForwardf.x, -vForwardf.y, -vForwardf.z);

        if(isKeyPressed(263))
            mesh.setyTheta(mesh.getyTheta() - mouseSensitivity);
        if(isKeyPressed(262))
            mesh.setyTheta(mesh.getyTheta() + mouseSensitivity);

    }

    public static void setColor(Mesh mesh, float red, float green, float blue)
    {
        for(Triangle tri: mesh.tris)
        {
            tri.setColor(new Color(red/255, green/255, blue/255, 1));
        }
    }

    public static void gameOver()
    {
        ItemBag.gameOver=true;
    }

    public static boolean isGameOver()
    {
        return ItemBag.gameOver;
    }

    public static int addMesh(String name, int id)
    {
        try {
            Mesh mesh = new ModelLoader().meshLoader("D:\\ideaIntellij\\olcge\\src\\resources", name, false);
            ItemBag.addMesh(mesh, id);
            return mesh.id;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void removeMesh(int id)
    {
        ItemBag.removeMesh(id);
    }

    /*public static void cameraControls(float walkSpeed, float fElapsedTime, )
    {
        VectorGeometry v = new VectorGeometry();
        if(KeyboardHandler.isKeyDown(GLFW_KEY_UP))
            camera.position.y+=walkSpeed*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_DOWN))
            camera.position.y-=walkSpeed*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A))
            camera.position=v.vectorAdd(camera.position, v.vectorMul(v.normaliseVector(v.crossProduct(camera.vLookDir, camera.vUp)), fElapsedTime*walkSpeed));
        if(KeyboardHandler.isKeyDown(GLFW_KEY_D))
            camera.position=v.vectorSub(camera.position, v.vectorMul(v.normaliseVector(v.crossProduct(camera.vLookDir, camera.vUp)), fElapsedTime*walkSpeed));
        Vec3d vForward = v.vectorMul(camera.vLookDir, walkSpeed * fElapsedTime);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_W))
            camera.position=v.vectorAdd(camera.position, vForward);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_S))
            camera.position=v.vectorSub(camera.position, vForward);
       *//* if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT))
            camera.setYaw(camera.getYaw() - walkSpeed/4f*fElapsedTime);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT))
            camera.setYaw(camera.getYaw() + walkSpeed/4f*fElapsedTime);*//*
        if(KeyboardHandler.isKeyDown(GLFW_KEY_R))
            camera.setPitch(camera.getPitch() - walkSpeed/4f*fElapsedTime);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_F))
            camera.setPitch(camera.getPitch() + walkSpeed/4f*fElapsedTime);

        double newCursorY=getCursorPosY(window), newCursorX=getCursorPosX(window);
        if(cursorY != newCursorY || cursorX!= newCursorX ) {
            double yDifference = (cursorY - newCursorY) / (double) MainGL.HEIGHT;
            double xDifference = (cursorX - newCursorX) / 400d;
            if((camera.getPitch() - mouseSensitivity * yDifference) <3.14/2f && (camera.getPitch() - mouseSensitivity * yDifference) >-3.14/2f)
                camera.setPitch(camera.getPitch() - (float)mouseSensitivity * (float)yDifference);
            camera.setYaw(camera.getYaw() - (float)mouseSensitivity * (float)xDifference);

        }
        if(!(newCursorY >0 && newCursorY<MainGL.HEIGHT && newCursorX>1 && newCursorX<MainGL.WIDTH)) {
            *//*glfwSetCursorPos(window, MainGL.WIDTH/2, MainGL.HEIGHT/2);
            cursorY=MainGL.HEIGHT/2;
            cursorX=MainGL.WIDTH/2;*//*
            cursorY=MainGL.HEIGHT/2;
            cursorX=MainGL.WIDTH/2;
            glfwSetCursorPos(window, cursorX, cursorY);
            *//*cursorY=newCursorY-10;
            cursorX=newCursorX-10;*//*
        }
        else
        {
            cursorY = newCursorY;
            cursorX=newCursorX;
        }
    }
*/


}
