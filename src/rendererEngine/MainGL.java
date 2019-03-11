package rendererEngine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class MainGL {

    // The window handle
    private long window;
    static int WIDTH = 600;
    static int HEIGHT = 600;


    public void run() {
        //System.out.println("\n\nHello LWJGL " + Version.getVersion() + "!");

        try {
            init();
            loop();

            // Release window and window callbacks
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable


        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "Hello World!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Center our window
        glfwSetWindowPos(
                window,
                (vidmode.width() - WIDTH) / 2,
                (vidmode.height() - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        System.nanoTime();
        double prevTime=System.nanoTime(), elapsedTime=0, currentTime, timedif=0, frameRatePrintRate=0.5;
        int frameCount=0;
        DisplayDriverGL ddgl = new DisplayDriverGL(window);
        glEnable(GL_DEPTH_TEST);




        while (!glfwWindowShouldClose(window)) {

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            currentTime=System.nanoTime();
            elapsedTime=(currentTime -prevTime)/10000f/100000f;
            prevTime= currentTime;
            timedif +=elapsedTime;

            ddgl.onUserUpdate((float) elapsedTime);
            frameCount++;
            if(timedif>frameRatePrintRate)
            {
                timedif=0;
                //System.out.println(frameCount*1/frameRatePrintRate);
                frameCount=0;
            }



            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

        }
        glfwSetWindowShouldClose(window, false);
    }

    public static void main(String[] args) {
        new MainGL().run();
    }

}
