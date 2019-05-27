package inputHandler;

import mathHandler.VectorGeometry;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import rendererEngine.itemBag.ItemBag;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

//import threeDItems.Matrix4by4;

public class KeyboardHandler extends GLFWKeyCallback{

    public  static boolean[] keys = new boolean[65536];
    private  float x=0, y=0, z=0, yTheta=0, xTheta=0, mouseSensitivity=1f, zTheta=0;
    private  long window;
    public  VectorGeometry v = new VectorGeometry();
    private  double cursorX, cursorY, tempDif;

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        keys[key] = action != GLFW_RELEASE;
        ItemBag.keys[key] = action != GLFW_RELEASE;
    }

    // boolean method that returns true if a given key
    // is pressed.
    public static boolean isKeyDown(int keycode) {
        return keys[keycode];
    }

    public KeyboardHandler(long window)

    {
        this.window = window;
        //ystem.out.println("fixie");
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        //glfwSetCursorPos(window, MainGL.WIDTH/2, MainGL.HEIGHT/2);
        glfwGetCursorPos(window, xBuffer, yBuffer);
        cursorX = xBuffer.get(0);
        cursorY = yBuffer.get(0);
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
