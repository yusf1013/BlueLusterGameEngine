package threeDItems;

import mathHandler.VectorGeometry;

import java.util.Vector;

public class Mesh {
    public Vector<Triangle> tris= new Vector<Triangle>();
    public float xTheta=0, yTheta=0, zTheta=0, xTranslation=0, yTranslation=0, zTranslation=0, xScale=1, yScale=1, zScale=1;
    public float x=0,y=0,z=0;
    public boolean isScripted=false;
    public int id;

    public void addTri(Triangle t)
    {
        x=(x+(t.p[0].x+ t.p[1].x+ t.p[2].x)/3f)/2f;
        y=(y+(t.p[0].y+ t.p[1].y+ t.p[2].y)/3f)/2f;
        z=(z+(t.p[0].z+ t.p[1].z+ t.p[2].z)/3f)/2f;
        tris.add(t);
    }

    @Override
    public String toString() {
        String ts = "Mesh-\n";
        for(int i=0; i<tris.size(); i++)
        {
            ts+= tris.elementAt(i).toString();
        }
        return  ts;
    }

    public void runScript()
    {

    }

    public Mat4x4 getWorldMat()
    {
        Mat4x4 matRotZ, matRotX, matRotY;
        //fTheta += 1.0f * fElapsedTime;
        matRotZ = VectorGeometry.makeZRotationMatrix(zTheta);
        matRotX = VectorGeometry.makeXRotationMatrix(xTheta);
        matRotY = VectorGeometry.makeYRotationMatrix(yTheta);

        Mat4x4 matTrans, scale;
        matTrans = VectorGeometry.makeTranslation(xTranslation, yTranslation, zTranslation);
        scale = VectorGeometry.scale(xScale, yScale, zScale);

        Mat4x4 matWorld = VectorGeometry.multiplyMatrix(matRotZ, matRotX);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotY);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, scale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matTrans);

        return matWorld;
    }

}
