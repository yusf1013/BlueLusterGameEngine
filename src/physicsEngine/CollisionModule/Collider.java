package physicsEngine.CollisionModule;

import mathHandler.VectorGeometry;
import threeDItems.Matrix4by4;
import threeDItems.Mesh;
import threeDItems.ObbMesh;
import threeDItems.Vec3d;

import java.util.ArrayList;
import java.util.Vector;

public class Collider {

    ArrayList<Vec2d> alxy = new ArrayList<>(), alyz = new ArrayList<>(), alxz = new ArrayList<>();
    public boolean detectCollision(Mesh m1, Mesh m2)
    {
        if(m1==null || m2==null)
        {
            if(m1==null)
               System.out.println("m1 os null");
            if(m2==null)
               System.out.println("m2 is null");
            return false;
        }

        if(m1.obb==null || m2.obb==null)
        {
            if(m1.obb==null)
               System.out.println("Mesh 1 obb null");
            if(m2.obb==null)
               System.out.println("Mesh 2 obb null");

            return false;
        }

        if(!collidesWith(m1.obb.cube, m2.obb.cube))
        {
            System.out.println("Early false");
            return false;
        }
        /*else if(collidesWith(m1.obb.cube, m2.obb.cube))
            return true;*/


        m1.updateObbList();
        m2.updateObbList();


        ArrayList<ObbMesh> al1 = new ArrayList<>(), al2 = new ArrayList<>();
        collidesWith(m1.obb.cube, m2.obbList, al2);
        collidesWith(m2.obb.cube, m1.obbList, al1);
        //System.out.println("Shit size shape: " + al1.size() + " " + al2.size());
        return collidesWith(al1, al2);

        /*return (shapeOverlapSAT(m1.obb.cube, m2.obb.cube, "xy") &&
                shapeOverlapSAT(m1.obb.cube, m2.obb.cube, "xz") &&
                shapeOverlapSAT(m1.obb.cube, m2.obb.cube, "yz"));*/

        /*boolean yo1 = false, yo2 = false;

        if((parentBoxColTest(m1.obb.cube, m2.obb.cube, "xy") &&
                parentBoxColTest(m1.obb.cube, m2.obb.cube, "xz") &&
                parentBoxColTest(m1.obb.cube, m2.obb.cube, "yz"))) {
            System.out.println("In if: " + m1.obbList.size());
            for(Mesh m: m1.obbList)
            {
                System.out.println("In if-for");
                if((parentChildrenColTest((ObbMesh) m, "xy")) &&
                        (parentChildrenColTest((ObbMesh) m, "xz")) &&
                        (parentChildrenColTest((ObbMesh) m, "yz")))
                {
                    System.out.println("YO1");
                    yo1 = true;
                    break;
                }
            }

            for(Mesh m: m2.obbList)
            {
                if((parentChildrenColTest((ObbMesh) m, "xy")) &&
                        (parentChildrenColTest((ObbMesh) m, "xz")) &&
                        (parentChildrenColTest((ObbMesh) m, "yz")))
                {
                    System.out.println("YO2");
                    yo2 = true;
                    break;
                }
            }

            return (yo1 && yo2);
        }

        else
            return false;

*/
    }

    public boolean collidesWith(ObbMesh m1, ArrayList<ObbMesh> al)
    {
        boolean flag=false;
        for(ObbMesh m: al)
            if(collidesWith(m, m1))
            {
                flag = true;
            }

        return flag;
    }

    public boolean collidesWith(ObbMesh m1, ArrayList<ObbMesh> al, ArrayList<ObbMesh> al2)
    {
        boolean flag=false;
        for(ObbMesh m: al)
            if(collidesWith(m, m1))
            {
                al2.add(m);
                flag = true;
            }

        return flag;
    }

    public boolean collidesWith(ArrayList<ObbMesh> al, ArrayList<ObbMesh> al2)
    {
        boolean flag=false;
        for(ObbMesh m: al)
            if(collidesWith(m, al2))
            {
                flag = true;
            }

        return flag;
    }

    public boolean collidesWith(ObbMesh m1, ObbMesh m2)
    {
        return (shapeOverlapSAT(m1, m2, "xy") && shapeOverlapSAT(m1, m2, "yz") && shapeOverlapSAT(m1, m2, "xz"));
    }

    public boolean shapeOverlapSAT(ObbMesh m1, ObbMesh m2, String axes)
    {
        ObbMesh poly1 = m1;
        ObbMesh poly2 = m2;

        poly1.p.clear();
        poly2.p.clear();

        if(!(axes.equals("xy") || axes.equals("xz") || axes.equals("yz")))
            throw new IllegalArgumentException("wrong axes data");

        Vector <Vec3d> vec = null;
        if(axes.equals("xy"))
            vec = poly1.vecXY;
        else if(axes.equals("xz"))
            vec = poly1.vecXZ;
        else if(axes.equals("yz"))
            vec = poly1.vecYZ;

        for(Vec3d v: vec)
        {
            poly1.p.add(VectorGeometry.multiplyMatrixAndVector(poly1.getWorldMat(), v));
            poly2.p.add(VectorGeometry.multiplyMatrixAndVector(poly2.getWorldMat(), v));
            /*System.out.println(poly1.getStats());
            System.out.println();*/
        }

        for (int shape = 0; shape < 2; shape++)
        {
            if (shape == 1)
            {
                poly1 = m2;
                poly2 = m1;
            }

            //System.out.println("Poly p.size" + poly1.p.size());

            for (int a = 0; a < poly1.p.size(); a++)
            {
                int b = (a + 1) % poly1.p.size();
                Vec2d axisProj;
                if(axes.equals("xy"))
                    axisProj = new Vec2d( -(poly1.p.get(b).y - poly1.p.get(a).y), poly1.p.get(b).x - poly1.p.get(a).x );
                else if(axes.equals("xz"))
                    axisProj = new Vec2d( -(poly1.p.get(b).z - poly1.p.get(a).z), poly1.p.get(b).x - poly1.p.get(a).x );
                else
                    axisProj = new Vec2d( -(poly1.p.get(b).z - poly1.p.get(a).z), poly1.p.get(b).y - poly1.p.get(a).y );
                //float d = (float) Math.sqrt(axisProj.x * axisProj.x + axisProj.y * axisProj.y);
                //axisProj = new Vec2d(axisProj.x / d, axisProj.y / d);

                // Work out min and max 1D points for r1
                float min_r1 = 1000000, max_r1 = -1000000;
                for (int p = 0; p < poly1.p.size(); p++)
                {
                    float q;
                    if(axes.equals("xy"))
                        q = (poly1.p.get(p).x * axisProj.x + poly1.p.get(p).y * axisProj.y);
                    else if(axes.equals("xz"))
                        q = (poly1.p.get(p).x * axisProj.x + poly1.p.get(p).z * axisProj.y);
                    else
                        q = (poly1.p.get(p).y * axisProj.x + poly1.p.get(p).z * axisProj.y);

                    min_r1 = Math.min(min_r1, q);
                    max_r1 = Math.max(max_r1, q);
                }

                // Work out min and max 1D points for r2
                float min_r2 = 1000000, max_r2 = -1000000;
                for (int p = 0; p < poly2.p.size(); p++)
                {
                    float q;
                    if(axes.equals("xy"))
                        q = (poly2.p.get(p).x * axisProj.x + poly2.p.get(p).y * axisProj.y);
                    else if(axes.equals("xz"))
                        q = (poly2.p.get(p).x * axisProj.x + poly2.p.get(p).z * axisProj.y);
                    else
                        q = (poly2.p.get(p).y * axisProj.x + poly2.p.get(p).z * axisProj.y);

                    min_r2 = Math.min(min_r2, q);
                    max_r2 = Math.max(max_r2, q);
                }

                //System.out.println("R1: " + min_r1 + ", " + max_r1);
                //System.out.println("R2: " + min_r2 + ", " + max_r2);

                if (!(max_r2 >= min_r1 && max_r1 >= min_r2))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean parentBoxColTest(ObbMesh m1, ObbMesh m2, String axes)
    {
        ObbMesh poly1 = m1;
        ObbMesh poly2 = m2;

        poly1.p.clear();
        poly2.p.clear();

        if(!(axes.equals("xy") || axes.equals("xz") || axes.equals("yz")))
            throw new IllegalArgumentException("wrong axes data");

        Vector <Vec3d> vec = null;
        if(axes.equals("xy"))
            vec = poly1.vecXY;
        else if(axes.equals("xz"))
            vec = poly1.vecXZ;
        else if(axes.equals("yz"))
            vec = poly1.vecYZ;

        for(Vec3d v: vec)
        {
            poly1.p.add(VectorGeometry.multiplyMatrixAndVector(poly1.getWorldMat(), v));
            poly2.p.add(VectorGeometry.multiplyMatrixAndVector(poly2.getWorldMat(), v));
        }

        for (int shape = 0; shape < 2; shape++)
        {
            if (shape == 1)
            {
                poly1 = m2;
                poly2 = m1;
            }


            for (int a = 0; a < poly1.p.size(); a++)
            {
                int b = (a + 1) % poly1.p.size();
                Vec2d axisProj;
                if(axes.equals("xy"))
                    axisProj = new Vec2d( -(poly1.p.get(b).y - poly1.p.get(a).y), poly1.p.get(b).x - poly1.p.get(a).x );
                else if(axes.equals("xz"))
                    axisProj = new Vec2d( -(poly1.p.get(b).z - poly1.p.get(a).z), poly1.p.get(b).x - poly1.p.get(a).x );
                else
                    axisProj = new Vec2d( -(poly1.p.get(b).z - poly1.p.get(a).z), poly1.p.get(b).y - poly1.p.get(a).y );
                //float d = (float) Math.sqrt(axisProj.x * axisProj.x + axisProj.y * axisProj.y);
                //axisProj = new Vec2d(axisProj.x / d, axisProj.y / d);

                // Work out min and max 1D points for r1
                float min_r1 = 1000000, max_r1 = -1000000;
                for (int p = 0; p < poly1.p.size(); p++)
                {
                    float q;
                    if(axes.equals("xy"))
                        q = (poly1.p.get(p).x * axisProj.x + poly1.p.get(p).y * axisProj.y);
                    else if(axes.equals("xz"))
                        q = (poly1.p.get(p).x * axisProj.x + poly1.p.get(p).z * axisProj.y);
                    else
                        q = (poly1.p.get(p).y * axisProj.x + poly1.p.get(p).z * axisProj.y);

                    min_r1 = Math.min(min_r1, q);
                    max_r1 = Math.max(max_r1, q);
                }

                // Work out min and max 1D points for r2
                float min_r2 = 1000000, max_r2 = -1000000;
                for (int p = 0; p < poly2.p.size(); p++)
                {
                    float q;
                    if(axes.equals("xy"))
                        q = (poly2.p.get(p).x * axisProj.x + poly2.p.get(p).y * axisProj.y);
                    else if(axes.equals("xz"))
                        q = (poly2.p.get(p).x * axisProj.x + poly2.p.get(p).z * axisProj.y);
                    else
                        q = (poly2.p.get(p).y * axisProj.x + poly2.p.get(p).z * axisProj.y);

                    min_r2 = Math.min(min_r2, q);
                    max_r2 = Math.max(max_r2, q);
                }

                //System.out.println("R1: " + min_r1 + ", " + max_r1);
                //System.out.println("R2: " + min_r2 + ", " + max_r2);

                if (!(max_r2 >= min_r1 && max_r1 >= min_r2))
                {
                    alxy.clear();
                    alxz.clear();
                    alyz.clear();
                    return false;
                }

                if(axes.equals("xy")) {
                    alxy.add(new Vec2d(Math.max(min_r1, min_r2), Math.min(max_r1, max_r2)));
                }
                else if(axes.equals("xz")) {
                    alxz.add(new Vec2d(Math.max(min_r1, min_r2), Math.min(max_r1, max_r2)));
                }
                else if(axes.equals("yz")){
                    alyz.add(new Vec2d(Math.max(min_r1, min_r2), Math.min(max_r1, max_r2)));
                }
            }
        }

        return true;
    }

    public boolean parentChildrenColTest(ObbMesh m1, String axes)
    {
        ObbMesh poly1 = m1;
        poly1.p.clear();

        if(!(axes.equals("xy") || axes.equals("xz") || axes.equals("yz")))
            throw new IllegalArgumentException("wrong axes data");

        Vector <Vec3d> vec = null;
        if(axes.equals("xy"))
            vec = poly1.vecXY;
        else if(axes.equals("xz"))
            vec = poly1.vecXZ;
        else if(axes.equals("yz"))
            vec = poly1.vecYZ;

        for(Vec3d v: vec)
        {
            poly1.p.add(VectorGeometry.multiplyMatrixAndVector(poly1.getWorldMat(), v));
        }

        for (int shape = 0; shape < 1; shape++)
        {

            for (int a = 0; a < poly1.p.size(); a++)
            {
                int b = (a + 1) % poly1.p.size();
                Vec2d axisProj;
                if(axes.equals("xy"))
                    axisProj = new Vec2d( -(poly1.p.get(b).y - poly1.p.get(a).y), poly1.p.get(b).x - poly1.p.get(a).x );
                else if(axes.equals("xz"))
                    axisProj = new Vec2d( -(poly1.p.get(b).z - poly1.p.get(a).z), poly1.p.get(b).x - poly1.p.get(a).x );
                else
                    axisProj = new Vec2d( -(poly1.p.get(b).z - poly1.p.get(a).z), poly1.p.get(b).y - poly1.p.get(a).y );
                //float d = (float) Math.sqrt(axisProj.x * axisProj.x + axisProj.y * axisProj.y);
                //axisProj = new Vec2d(axisProj.x / d, axisProj.y / d);

                // Work out min and max 1D points for r1
                float min_r1 = 1000000, max_r1 = -1000000;
                for (int p = 0; p < poly1.p.size(); p++)
                {
                    float q;
                    if(axes.equals("xy"))
                        q = (poly1.p.get(p).x * axisProj.x + poly1.p.get(p).y * axisProj.y);
                    else if(axes.equals("xz"))
                        q = (poly1.p.get(p).x * axisProj.x + poly1.p.get(p).z * axisProj.y);
                    else
                        q = (poly1.p.get(p).y * axisProj.x + poly1.p.get(p).z * axisProj.y);

                    min_r1 = Math.min(min_r1, q);
                    max_r1 = Math.max(max_r1, q);
                }

                // Work out min and max 1D points for r2
                float min_r2 = 1000000, max_r2 = -1000000;
                for (int i = 0; i < poly1.p.size(); i++)
                {
                    float p,q;
                    if(axes.equals("xy"))
                    {
                        q = alxy.get(i).x;
                        p = alxy.get(i).y;
                    }
                    else if(axes.equals("xz"))
                    {
                        q = alxz.get(i).x;
                        p = alxz.get(i).y;
                    }
                    else
                    {
                        q = alyz.get(i).x;
                        p = alyz.get(i).y;
                    }
                    min_r2 = Math.min(min_r2, q);
                    max_r2 = Math.max(max_r2, p);
                }

                if (!(max_r2 >= min_r1 && max_r1 >= min_r2))
                {
                    return false;
                }
            }
        }

        return true;
    }

            /*ArrayList<Triangle> al1 = new ArrayList<>(), al2=new ArrayList<>();
        float rot1=obb1.cube.xTheta, rot2

        for(Triangle triangle: obb1.cube.tris) {
            al1.add(ThreeDObjectTransformations.projectTriangle(triangle, obb1.getWorldMat()));
            al2.add(ThreeDObjectTransformations.projectTriangle(triangle, obb2.getWorldMat()));
        }*/

    /*public boolean shapeOverlapSAT(ObbMesh m1, ObbMesh m2, String axes)
    {
        ObbMesh poly1 = m1;
        ObbMesh poly2 = m2;

        for(Vec3d v: poly1.vec)
        {
            poly1.p.add(VectorGeometry.multiplyMatrixAndVector(poly1.getWorldMat(), v));
            poly2.p.add(VectorGeometry.multiplyMatrixAndVector(poly2.getWorldMat(), v));
        }

        for (int shape = 0; shape < 2; shape++)
        {
            if (shape == 1)
            {
                poly1 = m2;
                poly2 = m1;
            }

            for (int a = 0; a < poly1.p.size(); a++)
            {
                int b = (a + 1) % poly1.p.size();
                Vec2d axisProj = new Vec2d( -(poly1.p.get(b).y - poly1.p.get(a).y), poly1.p.get(b).x - poly1.p.get(a).x );
                //float d = (float) Math.sqrt(axisProj.x * axisProj.x + axisProj.y * axisProj.y);
                //axisProj = new Vec2d(axisProj.x / d, axisProj.y / d);

                // Work out min and max 1D points for r1
                float min_r1 = 1000000, max_r1 = -1000000;
                for (int p = 0; p < poly1.p.size(); p++)
                {
                    float q = (poly1.p.get(p).x * axisProj.x + poly1.p.get(p).y * axisProj.y);
                    min_r1 = Math.min(min_r1, q);
                    max_r1 = Math.max(max_r1, q);
                }

                // Work out min and max 1D points for r2
                float min_r2 = 1000000, max_r2 = -1000000;
                for (int p = 0; p < poly2.p.size(); p++)
                {
                    float q = (poly2.p.get(p).x * axisProj.x + poly2.p.get(p).y * axisProj.y);
                    min_r2 = Math.min(min_r2, q);
                    max_r2 = Math.max(max_r2, q);
                }

                if (!(max_r2 >= min_r1 && max_r1 >= min_r2))
                    return false;
            }
        }

        return true;
    }*/

}

class Vec2d {
    float x,y;
    public Vec2d(float x, float y)
    {
        this.x=x;
        this.y=y;
    }
}
