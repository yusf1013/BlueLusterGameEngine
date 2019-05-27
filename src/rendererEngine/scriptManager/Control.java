package rendererEngine.scriptManager;
import mathHandler.VectorGeometry;
import rendererEngine.Camera;
import rendererEngine.itemBag.ItemBag;
import threeDItems.Mesh;
import threeDItems.Vec3d;

import java.util.HashMap;


public class Control {
    public static Camera camera = ItemBag.camera;
    private static float x,y,z,xt,yt,zt;
    private static float tx,ty,tz,txt,tyt,tzt, pitch, yaw;
    private static Mesh bMesh;
    private static Vec3d position;
    private static HashMap<String, Integer> keyMap;
    private static char forwardKey='y', backwardKey='h', leftKey='g', rightKey='j';

    public static void bindCamera(Mesh mesh)
    {
        if(ItemBag.isCameraBound)
        {
            System.out.println("BIND CAMERA RETURNING ");
            return;
        }

        System.out.println("BIND CAMERA PROCEEDING");
        ItemBag.isCameraBound=true;
        x=mesh.getxTranslation();
        y=mesh.getyTranslation();
        z=mesh.getzTranslation();
        xt=mesh.getxTheta();
        yt=mesh.getyTheta();
        zt=mesh.getzTheta();
        bMesh=mesh;
        /*if(ItemBag.camMesh!=null)
        {
            camera.position=ItemBag.camMesh.position;
            System.out.println("CAM RESULT: " + camera.position);
        }*/

        position=camera.position;
        pitch= camera.getPitch();
        yaw= camera.getYaw();
        System.out.println("END OF BIND CAMERA: " + ItemBag.camera.position);
    }

   /* public static void populateMap()
    {
        keyMap.put("w", 87);
        keyMap.put("W", 87);
    }*/

    protected static void boundedCameraControls()
    {
        tx=bMesh.getxTranslation();
        //System.out.println("Bounded cam controls: " + ItemBag.camera.position + ", " + ItemBag.camMesh.position);
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
            mesh.move(-leftRight.x, -leftRight.y, -leftRight.z);

        if(isKeyPressed(forwardKey))
            mesh.move(vForward.x, vForward.y, vForward.z);
        if(isKeyPressed(backwardKey))
            mesh.move(-vForward.x, -vForward.y, -vForward.z);

        if(isKeyPressed(263))
            bMesh.setyTheta(bMesh.getyTheta() - mouseSensitivity);
        if(isKeyPressed(262))
            bMesh.setyTheta(bMesh.getyTheta() + mouseSensitivity);

    }

}
