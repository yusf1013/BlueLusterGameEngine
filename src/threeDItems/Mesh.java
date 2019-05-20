package threeDItems;

import javafx.scene.paint.Color;
import mathHandler.VectorGeometry;
import physicsEngine.CollisionModule.BoundingVolume;
import physicsEngine.CollisionModule.Obb;
import physicsEngine.CollisionModule.ObjectSliceAndMerge;

import java.util.ArrayList;
import java.util.Vector;

public class Mesh {
    public ArrayList<Triangle> tris= null;
    public float xTheta=0, yTheta=0, zTheta=0, xTranslation=0, yTranslation=0, zTranslation=0, xScale=1, yScale=1, zScale=1;
    public boolean isScripted=false, isRigidBody=false;
    public int id;
    public Obb obb;
    //public ObjectSliceAndMerge obj=null;
    public ArrayList<ObbMesh> obbList = new ArrayList<>();
    public Vec3d min=new Vec3d(1000000, 1000000,1000000), max=new Vec3d(-1000000, -1000000,-1000000);

    public Mesh()
    {

    }
    public Mesh(boolean bool)
    {
        tris=new ArrayList<>();
    }

    public void addTri(Triangle tri)
    {
        Vec3d tMin = new Vec3d(), tMax = new Vec3d();
        tMin.x=findMin(tri.p[0].x, tri.p[1].x, tri.p[2].x);
        tMin.y=findMin(tri.p[0].y, tri.p[1].y, tri.p[2].y);
        tMin.z=findMin(tri.p[0].z, tri.p[1].z, tri.p[2].z);

        tMax.x=findMax(tri.p[0].x, tri.p[1].x, tri.p[2].x);
        tMax.y=findMax(tri.p[0].y, tri.p[1].y, tri.p[2].y);
        tMax.z=findMax(tri.p[0].z, tri.p[1].z, tri.p[2].z);

        min.x=Math.min(min.x, tMin.x);
        min.y=Math.min(min.y, tMin.y);
        min.z=Math.min(min.z, tMin.z);

        max.x=Math.max(max.x, tMax.x);
        max.y=Math.max(max.y, tMax.y);
        max.z=Math.max(max.z, tMax.z);
        tris.add(tri);
    }

    public void setTris(ArrayList<Triangle> list)
    {
        //tris=new ArrayList<>();
        min=new Vec3d(1000000, 1000000,1000000);
        max=new Vec3d(-1000000, -1000000,-1000000);

        for(Triangle tri:list)
        {
            addTri(tri);
        }
    }

    public Matrix4by4 getWorldMat()
    {
        updateObbList();
        Matrix4by4 matRotZ, matRotX, matRotY;
        matRotZ = VectorGeometry.makeZRotationMatrix(zTheta);
        matRotX = VectorGeometry.makeXRotationMatrix(xTheta);
        matRotY = VectorGeometry.makeYRotationMatrix(yTheta);

        Matrix4by4 matTrans, scale;
        matTrans = VectorGeometry.makeTranslation(xTranslation, yTranslation, zTranslation);
        scale = VectorGeometry.scale(xScale, yScale, zScale);

        /*Matrix4by4 matWorld = VectorGeometry.multiplyMatrix(matRotZ, matRotX);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotY);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, scale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matTrans);*/

        Matrix4by4 matWorld = VectorGeometry.makeIdentity();
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotZ);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotX);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matRotY);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, scale);
        matWorld = VectorGeometry.multiplyMatrix(matWorld, matTrans);

        return matWorld;
    }

    public void updateObbList()
    {
        for(ObbMesh m: obbList)
        {
            m.xTheta=xTheta;
            m.yTheta=yTheta;
            m.zTheta=xTheta;
            m.xTranslation=xTranslation;
            m.yTranslation=yTranslation;
            m.zTranslation=zTranslation;
            m.xScale=xScale;
            m.yScale=yScale;
            m.zScale=zScale;
        }
    }

    public void setColor(Color color)
    {
        for(Triangle  tri: tris)
        {
            tri.setColor(color);
        }
    }

    public void printColor()
    {
       System.out.println(tris.get(0).getColor());
    }

    public float findMin(float f1, float f2, float f3)
    {
        float  temp=Math.min(f1, f2);
        temp = Math.min(f3, temp);
        return temp;
    }

    public float findMax(float f1, float f2, float f3)
    {
        float  temp=Math.max(f1, f2);
        temp = Math.max(f3, temp);
        return temp;
    }

    @Override
    public String toString() {
        String ts = "Mesh-\n";
        for(int i=0; i<tris.size(); i++)
        {
            ts+= tris.get(i).toString();
        }
        //ystem.out.println("fixie");
        return  ts;
    }

    public Obb getObb()
    {
        return new Obb(min, max, id);
    }

    public String getStats() {
        return "Mesh{" +
                "xTheta=" + xTheta +
                ", yTheta=" + yTheta +
                ", zTheta=" + zTheta +
                ", xTranslation=" + xTranslation +
                ", yTranslation=" + yTranslation +
                ", zTranslation=" + zTranslation +
                ", xScale=" + xScale +
                ", yScale=" + yScale +
                ", zScale=" + zScale +
                ", isScripted=" + isScripted +
                ", isRigidBody=" + isRigidBody +
                ", id=" + id +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
