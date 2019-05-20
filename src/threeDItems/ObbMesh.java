package threeDItems;

import mathHandler.VectorGeometry;

import java.util.ArrayList;
import java.util.Vector;

public class ObbMesh extends Mesh{

    public float initTransX, initTransY, initTransZ;
    public float initScaleX=1, initScaleY=1, initScaleZ=1;
    public Vector<Vec3d> p = new Vector<>(), vecXY, vecXZ, vecYZ;

    public ObbMesh(boolean bool)
    {
        this();
        tris=new ArrayList<>();
    }
    public ObbMesh()
    {
        super();
        vecXY= new Vector<>();
        vecXY.add(new Vec3d(-0.5f, -0.5f, 0.0f));
        vecXY.add(new Vec3d(-0.5f, 0.5f, 0.0f));
        vecXY.add(new Vec3d(0.5f, 0.5f, 0.0f));
        vecXY.add(new Vec3d(0.5f, -0.5f, 0.0f));

        vecXZ= new Vector<>();
        vecXZ.add(new Vec3d(-0.5f, 0.0f, -0.5f));
        vecXZ.add(new Vec3d(-0.5f, 0.0f, 0.5f));
        vecXZ.add(new Vec3d(0.5f, 0.0f, 0.5f));
        vecXZ.add(new Vec3d(0.5f, 0.0f, -0.5f));

        vecYZ= new Vector<>();
        vecYZ.add(new Vec3d(0.0f, -0.5f, -0.5f));
        vecYZ.add(new Vec3d(0.0f, -0.5f, 0.5f));
        vecYZ.add(new Vec3d(0.0f, 0.5f, 0.5f));
        vecYZ.add(new Vec3d(0.0f, 0.5f, -0.5f));
    }

    public Matrix4by4 getWorldMat()
    {
        Matrix4by4 matRotZ, matRotX, matRotY;
        matRotZ = VectorGeometry.makeZRotationMatrix(zTheta);
        matRotX = VectorGeometry.makeXRotationMatrix(xTheta);
        matRotY = VectorGeometry.makeYRotationMatrix(yTheta);

        Matrix4by4 matTrans, scale, initTrans, initScale;
        matTrans = VectorGeometry.makeTranslation(xTranslation, yTranslation, zTranslation);
        initTrans=VectorGeometry.makeTranslation(initTransX, initTransY, initTransZ);
        initScale = VectorGeometry.scale(initScaleX, initScaleY, initScaleZ);
        scale = VectorGeometry.scale(xScale, yScale, zScale);


        Matrix4by4 matWorld;
        matWorld = VectorGeometry.makeIdentity();

        matWorld = VectorGeometry.multiplyMatrix(matWorld, initScale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, initTrans);

        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotZ);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotX);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotY);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, scale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matTrans);

        return matWorld;
    }

    public Matrix4by4 getWorldMatWithoutRotation()
    {
        Matrix4by4 matTrans, scale, initTrans, initScale;
        matTrans = VectorGeometry.makeTranslation(xTranslation, yTranslation, zTranslation);
        initTrans=VectorGeometry.makeTranslation(initTransX, initTransY, initTransZ);
        initScale = VectorGeometry.scale(initScaleX, initScaleY, initScaleZ);
        scale = VectorGeometry.scale(xScale, yScale, zScale);


        Matrix4by4 matWorld;
        matWorld = VectorGeometry.makeIdentity();

        matWorld = VectorGeometry.multiplyMatrix(matWorld, initScale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, initTrans);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, scale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matTrans);

        return matWorld;
    }

    @Override
    public String getStats() {
        return super.getStats()+"ObbMesh{" +
                "initTransX=" + initTransX +
                ", initTransY=" + initTransY +
                ", initTransZ=" + initTransZ +
                ", initScaleX=" + initScaleX +
                ", initScaleY=" + initScaleY +
                ", initScaleZ=" + initScaleZ +
                '}';
    }
}
