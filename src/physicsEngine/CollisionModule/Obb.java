package physicsEngine.CollisionModule;

import dataHandler.ModelLoader;
import javafx.scene.paint.Color;
import mathHandler.ThreeDObjectTransformations;
import mathHandler.VectorGeometry;
import threeDItems.*;

import java.io.FileNotFoundException;
import java.util.Vector;

public class Aabb extends Collider{
    public Vec3d min= new Vec3d();
    public Vec3d max = new Vec3d();
    ObbMesh cube = new ObbMesh();
    int id;

    public Aabb (Vec3d min, Vec3d max, int id)
    {
        this.min=min;
        this.max=max;
        this.id=id;
        try{
            cube = new ModelLoader().obbMeshLoader("src\\resources\\","cube.obj", false);
        } catch (FileNotFoundException e) {
            try {
                cube = new ModelLoader().obbMeshLoader("resources\\","cube.obj", false);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }

    public Aabb(Triangle tri)
    {
        min.x=findMin(tri.p[0].x, tri.p[1].x, tri.p[2].x);
        min.y=findMin(tri.p[0].y, tri.p[1].y, tri.p[2].y);
        min.z=findMin(tri.p[0].z, tri.p[1].z, tri.p[2].z);

        max.x=findMax(tri.p[0].x, tri.p[1].x, tri.p[2].x);
        max.y=findMax(tri.p[0].y, tri.p[1].y, tri.p[2].y);
        max.z=findMax(tri.p[0].z, tri.p[1].z, tri.p[2].z);
    }

    public Aabb(Mesh mesh)
    {
        this(mesh.tris.get(0));
        Vector<Triangle> vec = mesh.tris;
        this.id = mesh.id;
        System.out.println("In big constructor");

        //cube = new ModelLoader().meshLoader("cube.obj", false);

        try{
            cube = new ModelLoader().obbMeshLoader("src\\resources\\","cube.obj", false);
        } catch (FileNotFoundException e) {
            try {
                cube = new ModelLoader().obbMeshLoader("resources\\","cube.obj", false);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }

        Vec3d tMin = new Vec3d(), tMax = new Vec3d();
        for(Triangle tri: vec)
        {
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

    public Mesh getMesh(Mesh mesh)
    {
        if(mesh.id != this.id)
            throw new IllegalArgumentException("Different mesh is being used");
        //System.out.println("In get mesh: " + min + "\n" + max);
        return fitMesh(mesh, cube, max, min);

    }

    public Mesh fitMesh(Mesh mesh, ObbMesh cube, Vec3d max, Vec3d min)
    {
        //clearCube(cube);

        VectorGeometry vg = new VectorGeometry();
        //old reliable shit
        /*cube.xScale=tDif.x*mesh.xScale;
        cube.yScale=tDif.y*mesh.yScale;
        cube.zScale=tDif.z*mesh.zScale;
        cube.xTranslation += mesh.xScale * min.x + mesh.xTranslation+0.5f*cube.xScale;
        cube.yTranslation += mesh.yScale * min.y + mesh.yTranslation+0.5f*cube.yScale;
        cube.zTranslation += mesh.zScale * min.z + mesh.zTranslation+0.5f*cube.zScale;*/


        //new piece of shit
        initialFit(max, min);
        cube.xTranslation = mesh.xTranslation;
        cube.yTranslation = mesh.yTranslation;
        cube.zTranslation = mesh.zTranslation;

        cube.xTheta=mesh.xTheta;
        cube.yTheta=mesh.yTheta;
        cube.zTheta=mesh.zTheta;

        cube.xScale=mesh.xScale;
        cube.yScale=mesh.yScale;
        cube.zScale=mesh.zScale;

        cube.xTheta=mesh.xTheta;
        cube.yTheta=mesh.yTheta;
        cube.zTheta=mesh.zTheta;
        cube.setColor(Color.TRANSPARENT);

        return cube;
    }

    public void initialFit(Vec3d max, Vec3d min)
    {
        VectorGeometry vg = new VectorGeometry();
        Vec3d tDif = vg.vectorSub(max, min);
        cube.initScaleX=tDif.x;
        cube.initScaleY=tDif.y;
        cube.initScaleZ=tDif.z;
        cube.initTransX = min.x +0.5f*cube.initScaleX;
        cube.initTransY = min.y + 0.5f*cube.initScaleY;
        cube.initTransZ = min.z + 0.5f*cube.initScaleZ;
    }

    public void clearCube(ObbMesh cube)
    {
        cube.xTheta=0;
        cube.yTheta=0;
        cube.zTheta=0;

        cube.xScale=1;
        cube.yScale=1;
        cube.zScale=1;

        cube.xTranslation=0;
        cube.yTranslation=0;
        cube.zTranslation=0;

        cube.initScaleZ=1;
        cube.initScaleY=1;
        cube.initScaleX=1;

        cube.initTransZ=0;
        cube.initTransY=0;
        cube.initTransZ=0;
    }

}
