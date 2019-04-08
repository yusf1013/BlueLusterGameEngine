package threeDItems;

import mathHandler.VectorGeometry;

public class ObbMesh extends Mesh{

    public float initTransX, initTransY, initTransZ;
    public float initScaleX=1, initScaleY=1, initScaleZ=1;

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

        /*System.out.println("Ghost");
        System.out.println(initTransX + " " + initTransY + " " + initTransZ + "\n" );
        System.out.println(xTranslation + " " + yTranslation + " " + zTranslation + "\n" );
        System.out.println(initTrans);
        System.out.println(matTrans);
        System.out.println("Ghost2");
        System.out.println(initScaleX + " " + initScaleY + " " + initScaleZ + "\n" );
        System.out.println(xScale + " " + yScale + " " + zScale + "\n" );
        System.out.println(initScale);
        System.out.println(scale);*/

        Matrix4by4 matWorld;
        matWorld = VectorGeometry.makeIdentity();

        matWorld = VectorGeometry.multiplyMatrix(matWorld, initScale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, initTrans);

        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotZ);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotX);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotY);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, scale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matTrans);

        //System.out.println("In OBB:-");
        /*System.out.println(initScale+"\n"+initTrans+"\n"+matRotZ+"\n"+matRotX+"\n"+matRotY+"\n"+scale
        +"\n"+matTrans);*/

        /*
        //Old shit
        matWorld = VectorGeometry.multiplyMatrix(matRotZ, matRotX);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotY);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, scale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matTrans);*/
        //matWorld = VectorGeometry.multiplyMatrix(matWorld, initTrans);
        return matWorld;
    }
}
