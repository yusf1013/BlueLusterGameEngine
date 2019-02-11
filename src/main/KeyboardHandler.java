package main;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import threeDItems.Mat4x4;
import threeDItems.VectorGeometry;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

//import threeDItems.Mat4x4;

public class KeyboardHandler extends GLFWKeyCallback{

    public static boolean[] keys = new boolean[65536];
    private static float x=0, y=0, z=0, yTheta=0, xTheta=0, mouseSensitivity=1f, zTheta=0;
    private static long window;
    public static VectorGeometry v = new VectorGeometry();
    private static double cursorX, cursorY, tempDif;

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        keys[key] = action != GLFW_RELEASE;
    }

    // boolean method that returns true if a given key
    // is pressed.
    public static boolean isKeyDown(int keycode) {
        return keys[keycode];
    }

    public KeyboardHandler(long window)
    {
        this.window = window;
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwSetCursorPos(window, MainGL.WIDTH/2, MainGL.HEIGHT/2);
        glfwGetCursorPos(window, xBuffer, yBuffer);
        cursorX = xBuffer.get(0);
        cursorY = yBuffer.get(0);
    }

    public static Mat4x4 inputHandler(Mat4x4 worldMat)
    {
        if(isKeyDown(GLFW_KEY_ESCAPE))
            glfwSetWindowShouldClose(window, true);
        if(isKeyDown(GLFW_KEY_W )&& !isKeyDown(GLFW_KEY_SPACE)) {
            z-=0.1f;

        }
        if(isKeyDown(GLFW_KEY_S)&& !isKeyDown(GLFW_KEY_SPACE))
            z+=0.1f;
        if(isKeyDown(GLFW_KEY_W )&& isKeyDown(GLFW_KEY_SPACE)) {
            y+=0.1f;

        }
        if(isKeyDown(GLFW_KEY_S)&& isKeyDown(GLFW_KEY_SPACE))
            y-=0.1f;
        if(isKeyDown(GLFW_KEY_A))
            x+=0.1f;
        if(isKeyDown(GLFW_KEY_D))
            x-=0.1f;

        worldMat = v.multiplyMatrix(worldMat, v.makeTranslation(x,y,z));
        x=0; y=0; z=0;

        double newCursorY=getCursorPosY(window), newCursorX=getCursorPosX(window);
        if(cursorY != newCursorY || cursorX!= newCursorX ) {
            double yDifference = (cursorY - newCursorY) / (double) MainGL.HEIGHT;
            double xDifference = (cursorX - newCursorX) / 400d;
            xTheta += mouseSensitivity * yDifference;
            yTheta -= mouseSensitivity * xDifference;

        }
        if(isKeyDown(GLFW_KEY_Q))
            zTheta-=0.005;
        if(isKeyDown(GLFW_KEY_E))
            zTheta+=0.005;


        Mat4x4 matTrans = v.multiplyMatrix(v.makeYRotationMatrix(yTheta), v.makeXRotationMatrix(xTheta)); // Transform by rotation
        matTrans=v.multiplyMatrix(matTrans, v.makeZRotationMatrix(zTheta));
        worldMat = v.multiplyMatrix(worldMat, matTrans); // Transform by translation
        xTheta=0; yTheta=0; zTheta=0;

        if(!(newCursorY >0 && newCursorY<MainGL.HEIGHT && newCursorX>1 && newCursorX<MainGL.WIDTH)) {
            glfwSetCursorPos(window, MainGL.WIDTH/2, MainGL.HEIGHT/2);
            cursorY=getCursorPosY(window);
            cursorX=getCursorPosX(window);
        }
        else
        {
            cursorY = newCursorY;
            cursorX=newCursorX;
        }
        return worldMat;
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



    /*public static void setWindow(long windowFloat)
    {
        window=windowFloat;
    }
    */


}
