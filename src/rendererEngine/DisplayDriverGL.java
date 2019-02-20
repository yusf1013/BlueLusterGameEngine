package main;

import javafx.scene.paint.Color;
import mathHandler.VectorGeometry;
import org.lwjgl.glfw.GLFWKeyCallback;
import threeDItems.*;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;



public class DisplayDriverGL extends VectorGeometry {

    Mesh meshCube = new Mesh(), meshCube2;
    Mat4x4 matProj = new Mat4x4(), matRotZ=new Mat4x4(), matRotX=new Mat4x4();
    float fTheta=0, fNear, fFar, fFov, fAspectRatio, fFovRad, screenHeight=600, screenWidth=600, walkSpeed=8f, yaw=0;
    Vec3d vCamera = new Vec3d(0,0,0), light_direction = new Vec3d(0,0,-1);
    double lightIntensity=1;
    long window;
    List<Triangle> triToRaster = new Vector<>();
    private GLFWKeyCallback keyCallback;
    Vec3d vLookDir = new Vec3d(), vUp=new Vec3d();


    public DisplayDriverGL(long window)
    {
        //Load model from file
        ModelLoader dd = new ModelLoader();
        //meshCube2=dd.meshLoader("cube2.obj", meshCube);
        meshCube=dd.meshLoader("bmw.obj", meshCube);
        //Projection matrix
        matProj = makeProjectionMatrix(90.0f, (float)screenHeight / (float)screenWidth, 0.1f, 1000.0f);
        glfwSetKeyCallback(window, keyCallback = new KeyboardHandler(window));
        System.out.println("Object has been loaded successfully");
        this.window=window;

    }


    boolean onUserUpdate(float fElapsedTime)
    {

        drawMesh(meshCube, fElapsedTime);
        //drawMesh(meshCube2, fElapsedTime);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_ESCAPE))
            glfwSetWindowShouldClose(window, true);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_UP))
            vCamera.y+=walkSpeed*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_DOWN))
            vCamera.y-=walkSpeed*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_A))
            vCamera=vectorAdd(vCamera, vectorMul(normaliseVector(crossProduct(vLookDir, vUp)), fElapsedTime*walkSpeed));
        if(KeyboardHandler.isKeyDown(GLFW_KEY_D))
            vCamera=vectorSub(vCamera, vectorMul(normaliseVector(crossProduct(vLookDir, vUp)), fElapsedTime*walkSpeed));
        Vec3d vForward = vectorMul(vLookDir, walkSpeed * fElapsedTime);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_W))
            vCamera=vectorAdd(vCamera, vForward);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_S))
            vCamera=vectorSub(vCamera, vForward);
        if(KeyboardHandler.isKeyDown(GLFW_KEY_LEFT))
            yaw-=walkSpeed/4f*fElapsedTime;
        if(KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT))
            yaw+=walkSpeed/4f*fElapsedTime;
        return true;

    }

    public boolean drawMesh(Mesh meshCube, float fElapsedTime)
    {
        Mat4x4 matWorld;
        matWorld = makeIdentity();	// Form World Matrix

        Mat4x4 matRotZ, matRotX;
        //fTheta += 1.0f * fElapsedTime;
        matRotZ = makeZRotationMatrix(fTheta * 0.5f );
        matRotX = makeXRotationMatrix(fTheta );

        Mat4x4 matTrans;
        matTrans = makeTranslation(0.0f, 0.0f, 3.0f);

        matWorld = multiplyMatrix(matRotZ, matRotX);
        matWorld = multiplyMatrix(matWorld, matTrans);

        // Create "Point At" Matrix for camera
        vUp = new Vec3d(0,1,0);
        Vec3d vTarget= new Vec3d(0,0.0f,1.0f);
        vLookDir=multiplyMatrixAndVector(makeYRotationMatrix(yaw), vTarget);
        vTarget=vectorAdd(vLookDir, vCamera);
        Mat4x4 matCamera = pointAtMatrix(vCamera, vTarget, vUp);

        Mat4x4 matView = quickInverse(matCamera);

        for (int i=0; i<meshCube.tris.size(); i++)
        {
            Triangle triProjected=new Triangle(), triTranslated=new Triangle(), triViewed = new Triangle();
            triTranslated.p[0] = multiplyMatrixAndVector(matWorld, meshCube.tris.elementAt(i).p[0]);
            triTranslated.p[1] = multiplyMatrixAndVector(matWorld, meshCube.tris.elementAt(i).p[1]);
            triTranslated.p[2] = multiplyMatrixAndVector(matWorld, meshCube.tris.elementAt(i).p[2]);

            Vec3d normal = new Vec3d(), line1= new Vec3d(), line2= new Vec3d();
            line1=vectorSub(triTranslated.p[1], triTranslated.p[0]);
            line2=vectorSub(triTranslated.p[2], triTranslated.p[0]);
            normal = crossProduct(line1, line2);

            normal = normaliseVector(normal);

            // Get Ray from triangle to camera
            Vec3d vCameraRay = vectorSub(triTranslated.p[0], vCamera);

            if (dotProduct(normal, vCameraRay) < 0.0f)
            {
                Triangle arr[] = null;
                triTranslated.p[0]=multiplyMatrixAndVector(matView, triTranslated.p[0]);
                triTranslated.p[1]=multiplyMatrixAndVector(matView, triTranslated.p[1]);
                triTranslated.p[2]=multiplyMatrixAndVector(matView, triTranslated.p[2]);

                if(triTranslated.p[0].z<0.1 || triTranslated.p[1].z<0.1 || triTranslated.p[2].z<0.1)
                {
                    arr = zClip(triTranslated, i);
                }
                else
                {
                    arr= new Triangle[1];
                    arr[0]=triTranslated;
                }

                if(arr==null || arr.length==0)
                    continue;
                for(int mor=0; mor<arr.length; mor++)
                {
                    triTranslated=arr[mor];
                    triProjected.p[0] = multiplyMatrixAndVector(matProj, triTranslated.p[0]);
                    triProjected.p[1] = multiplyMatrixAndVector(matProj, triTranslated.p[1]);
                    triProjected.p[2] = multiplyMatrixAndVector(matProj, triTranslated.p[2]);

                    triProjected.p[0] = vectorDiv(triProjected.p[0], triProjected.p[0].w);
                    triProjected.p[1] = vectorDiv(triProjected.p[1], triProjected.p[1].w);
                    triProjected.p[2] = vectorDiv(triProjected.p[2], triProjected.p[2].w);



                    Color color = triProjected.getColor();
                    light_direction = normaliseVector(light_direction);
                    float dp = Math.max(0.1f, (float) Math.abs((double) dotProduct(light_direction, normal)));
                    triProjected.setColor(new Color(color.getRed() * dp, color.getGreen() * dp, color.getBlue() * dp, color.getOpacity() * 1));
                    triToRaster.add(new Triangle(triProjected.p[0], triProjected.p[1], triProjected.p[2], triProjected.getColor()));

                }
            }
        }
        triToRaster.sort(Comparator.comparing(Triangle::getMidPointz).reversed());

        for(int i=0; i<triToRaster.size(); i++)
        {
            fillTriangle(triToRaster.get(i).p[0].x, triToRaster.get(i).p[0].y,
                    triToRaster.get(i).p[1].x, triToRaster.get(i).p[1].y,
                    triToRaster.get(i).p[2].x, triToRaster.get(i).p[2].y, triToRaster.get(i).getColor());
        }
        triToRaster.clear();
        return true;
    }

    public Triangle[] zClip(Triangle tri, int indind)
    {
        Vec3d p1=new Vec3d(), p2=new Vec3d(), p3=new Vec3d();
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

    public Triangle[] yClip(Triangle tri, int indind)
    {
        //System.out.println("YClip is called");
        Vec3d p1=new Vec3d(), p2=new Vec3d(), p3=new Vec3d();
        Vector <Vec3d> vec = new Vector<>();
        vec.add(tri.p[0]);
        vec.add(tri.p[1]);
        vec.add(tri.p[2]);

        int count =0, ind1=0, ind2=0, ind3=0;

        if(tri.p[0].y<-1)
            count++;
        if(tri.p[1].y<-1)
            count++;
        if(tri.p[2].y<-1)
            count++;
        //System.out.println("Count: " + count);
        if(count==3)
            return null;
        if(count==1)
        {
            //System.out.println("YCount is 1");
            int lowestOne;
            if(tri.p[0].y<tri.p[1].y)
                lowestOne=0;
            else
                lowestOne=1;
            if(tri.p[lowestOne].y>tri.p[2].y)
                lowestOne=2;
            Vec3d tempV=tri.p[lowestOne];
            vec.remove(lowestOne);
            float newX=ycalc(vec.elementAt(0).x, vec.elementAt(0).y, tempV.x, tempV.y);
            Vec3d new2 = new Vec3d(newX, -1.0f, 0.1f);
            newX=ycalc(vec.elementAt(1).x, vec.elementAt(1).y, tempV.x, tempV.y);
            Vec3d new3 = new Vec3d(newX, -1.0f, 0.1f);
            vec.add(lowestOne, new3);
            vec.add(lowestOne, new2);
            Triangle triArr[] = new Triangle[2];
            triArr[0]=new Triangle(vec.elementAt(0), vec.elementAt(1), vec.elementAt(2));
            triArr[1]=new Triangle(vec.elementAt(1), vec.elementAt(2), vec.elementAt(3));
            return  triArr;
        }
        else if(count==2)
        {
            //System.out.println("YCount is 2");
            tri.p[0].y*=-1; tri.p[1].y*=-1; tri.p[2].y*=-1;
            int lowestOne;
            if(tri.p[0].y<tri.p[1].y)
                lowestOne=0;
            else
                lowestOne=1;
            if(tri.p[lowestOne].y>tri.p[2].y)
                lowestOne=2;
            tri.p[0].y*=-1; tri.p[1].y*=-1; tri.p[2].y*=-1;
            Vec3d tempV=tri.p[lowestOne];
            vec.remove(lowestOne);
            float newX=ycalc(vec.elementAt(0).x, vec.elementAt(0).y, tempV.x, tempV.y);
            Vec3d new2 = new Vec3d(newX, -1.0f, 0.1f);
            newX=ycalc(vec.elementAt(1).x, vec.elementAt(1).y, tempV.x, tempV.y);
            Vec3d new3 = new Vec3d(newX, -1.0f, 0.1f);
            vec.add(lowestOne, new3);
            vec.add(lowestOne, new2);

            Vector<Vec3d> vec2= new Vector<>();
            vec2.add(new2);
            vec2.add(new3);
            vec2.add(lowestOne, tempV);

            Triangle triArr[] = new Triangle[1];
            triArr[0]= new Triangle(vec2.elementAt(0), vec2.elementAt(1), vec2.elementAt(2));
            return  triArr;
        }
        Triangle arr[] = new Triangle[1];
        arr[0]=tri;
        return  arr;
    }

    public float ycalc(float xoy1, float z1, float xoy3, float z3)
    {
        if(z1==z3) {
            System.out.println("You died");

        }
        return (((xoy1-xoy3)*(-1-z3)/(z1-z3))+xoy3);
    }

    public float zcalc(float xoy1, float z1, float xoy3, float z3)
    {
        if(z1==z3) {
            System.out.println("You died");

        }
        return (((xoy1-xoy3)*(0.1f-z3)/(z1-z3))+xoy3);
    }

    /*public static void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Group root, Color fill, Color stroke)
    {
        Polygon tri = new Polygon(x1, y1, x2, y2, x3, y3);
        tri.setFill(fill);
        tri.setStroke(stroke);
        root.getChildren().addAll(tri);
    }*/

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



}
