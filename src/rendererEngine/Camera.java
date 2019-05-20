package rendererEngine;

import threeDItems.Matrix4by4;
import threeDItems.Vec3d;
import mathHandler.VectorGeometry;

public class Camera {
    public Vec3d position;
    public Vec3d vLookDir = new Vec3d(0,0,1);
    public Vec3d vUp=new Vec3d(0,1,0);
    public float  yaw=0;
    public float pitch=0;
    VectorGeometry calculator = new VectorGeometry();
    Vec3d vTarget= new Vec3d(0,0.0f,1.0f);

    public Camera ()
    {
        this(0,0,0);
    }

    public Camera(float x, float y, float z)
    {
        position  = new Vec3d(x,y,z);
    }

    public Matrix4by4 createViewMat()
    {
        //ystem.out.println("fixie");
        Vec3d vTarget= new Vec3d(0,0.0f,1.0f);
        //vLookDir=calculator.multiplyMatrixAndVector(calculator.makeYRotationMatrix(yaw), vTarget);
        Matrix4by4 mat = calculator.multiplyMatrix(calculator.makeXRotationMatrix(pitch), calculator.makeYRotationMatrix(yaw));
        vLookDir=calculator.multiplyMatrixAndVector(mat, vTarget);
        vTarget=calculator.vectorAdd(vLookDir, position);
        Matrix4by4 matCamera = calculator.pointAtMatrix(position, vTarget, vUp);

        Matrix4by4 matView = calculator.quickInverse(matCamera);
        return  matView;

    }

}
