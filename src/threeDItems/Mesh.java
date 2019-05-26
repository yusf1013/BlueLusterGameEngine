package threeDItems;

import javafx.scene.paint.Color;
import mathHandler.VectorGeometry;
import physicsEngine.CollisionModule.Obb;

import java.util.ArrayList;

public class Mesh {
    public ArrayList<Triangle> tris= null;
    protected float xTheta=0;
    protected float yTheta=0;
    protected float zTheta=0;
    protected float xTranslation=0;
    protected float yTranslation=0;
    protected float zTranslation=0;
    protected float xScale=1;
    protected float yScale=1;
    protected float zScale=1;
    public boolean isFixed=false, flagNew=false;
    public boolean isScripted=false, isRigidBody=false;
    public int id;
    public Obb obb;
    public String name;
    //public ObjectSliceAndMerge obj=null;
    public ArrayList<ObbMesh> obbList = new ArrayList<>();
    public Vec3d min=new Vec3d(1000000, 1000000,1000000), max=new Vec3d(-1000000, -1000000,-1000000);

    public Mesh()
    {

    }
    public Mesh(String name)
    {
        this.name=name;
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

    public ArrayList<Triangle> pointTris(ArrayList<Triangle> list)
    {
        min=new Vec3d(1000000, 1000000,1000000);
        max=new Vec3d(-1000000, -1000000,-1000000);

        for(Triangle tri: list)
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
        }
        return list;
    }

    public Matrix4by4 getWorldMat()
    {
        updateObbList();
        Matrix4by4 matRotZ, matRotX, matRotY;
        matRotZ = VectorGeometry.makeZRotationMatrix(getzTheta());
        matRotX = VectorGeometry.makeXRotationMatrix(getxTheta());
        matRotY = VectorGeometry.makeYRotationMatrix(getyTheta());

        Matrix4by4 matTrans, scale;
        matTrans = VectorGeometry.makeTranslation(getxTranslation(), getyTranslation(), getzTranslation());
        scale = VectorGeometry.scale(getxScale(), getyScale(), getzScale());

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
            m.setxTheta(getxTheta());
            m.setyTheta(getyTheta());
            m.setzTheta(getzTheta());
            m.setxTranslation(getxTranslation());
            m.setyTranslation(getyTranslation());
            m.setzTranslation(getzTranslation());
            m.setxScale(getxScale());
            m.setyScale(getyScale());
            m.setzScale(getzScale());
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
                "xTheta=" + getxTheta() +
                ", yTheta=" + getyTheta() +
                ", zTheta=" + getzTheta() +
                ", xTranslation=" + getxTranslation() +
                ", yTranslation=" + getyTranslation() +
                ", zTranslation=" + getzTranslation() +
                ", xScale=" + getxScale() +
                ", yScale=" + getyScale() +
                ", zScale=" + getzScale() +
                ", isScripted=" + isScripted +
                ", isRigidBody=" + isRigidBody +
                ", id=" + id +
                ", min=" + min +
                ", max=" + max +
                '}';
    }

    public float getxTheta() {
        return xTheta;
    }

    public float getyTheta() {
        return yTheta;
    }

    public float getzTheta() {
        return zTheta;
    }

    public void setxTheta(float xTheta) {
        flagNew=false;
        this.xTheta = xTheta;
    }

    public void setyTheta(float yTheta) {
        flagNew=false;
        this.yTheta = yTheta;
    }

    public void setzTheta(float zTheta) {
        flagNew=false;
        this.zTheta = zTheta;
    }

    public float getxTranslation() {
        return xTranslation;
    }

    public void setxTranslation(float xTranslation) {
        flagNew=false;
        this.xTranslation = xTranslation;
    }

    public float getyTranslation() {
        return yTranslation;
    }

    public void setyTranslation(float yTranslation) {
        flagNew=false;
        this.yTranslation = yTranslation;
    }

    public float getzTranslation() {
        return zTranslation;
    }

    public void setzTranslation(float zTranslation) {
        flagNew=false;
        this.zTranslation = zTranslation;
    }

    public float getxScale() {
        return xScale;
    }

    public void setxScale(float xScale) {
        flagNew=false;
        this.xScale = xScale;
    }

    public float getyScale() {
        return yScale;
    }

    public void setyScale(float yScale) {
        flagNew=false;
        this.yScale = yScale;
    }

    public float getzScale() {
        return zScale;
    }

    public void setzScale(float zScale) {
        flagNew=false;
        this.zScale = zScale;
    }
}
