package physicsEngine.CollisionModule;

import threeDItems.Mesh;
import threeDItems.Triangle;
import threeDItems.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import threeDItems.ObbMesh;


class Node{
    Mesh node;
    ArrayList<Node> children = new ArrayList<>();
    ObbDataOnly obbDataOnly;

    public Node(Mesh node)
    {
        this.node=node;
    }

    public void duplicateParent()
    {
        if(children.size()!=0)
            throw new ArrayIndexOutOfBoundsException("Die");
        children.add(new Node(node));
    }
}

public class ObjectSliceAndMerge {

    Node tree;
    int wantedDivisionDepth=1;

    public ObjectSliceAndMerge(Mesh mesh)
    {
        tree = new Node(mesh);
    }

    public void createTree()
    {
        createTreeStarts(tree, 0);
    }

    public void createTreeStarts(Node node, int depth)
    {
        if(depth>=wantedDivisionDepth)
        {
            System.out.println("Returning");
            return;
        }
        else
            System.out.println("Proceeding");

        if(node.children.size()==0)
        {
            node.duplicateParent();
            /*Obb obb = new Obb(node.node);
            Vec3d criticalValue = calcCriticalValue(obb.min, obb.max);*/
            Vec3d criticalValue = calcCriticalValue(node.node.min, node.node.max);
            System.out.println("Critical min: " + node.node.max);
            createChildren(node, criticalValue.x,"x");
            createChildren(node, criticalValue.y,"y");
            createChildren(node, criticalValue.z,"z");
            node.node=null;
        }

        for (Node n:node.children)
        {
            createTreeStarts(n, depth+1);
        }



        /*node.node=mesh;
        tree.children.add(new Node(mesh));
        createChildren(tree, 0.0f, "x");*/

        /*createChildrenX(tree, new float[] {-0.25f, 0.25f});
        createChildrenX(tree, new float[] {-0.375f, -0.125f, 0.125f, 0.375f});*/

        /*pointer=tree.children.get(1);
        pointer.duplicateParent();
        pointer.node=null;
        createChildrenX(pointer, new float[] {-0.25f});

        pointer=tree.children.get(0);
        pointer.duplicateParent();
        pointer.node=null;
        createChildrenX(pointer, new float[] {+0.25f});

        pointer.children.add(new Node(pointer.node));
        pointer.node=null;*/
    }

    public void createChildren(Node tree, float criticalValue, String axis)
    {
        int size=tree.children.size();
        for(int i=0; i<size; i++)
        {
            System.out.println("In createChildrenX for");
            /*for(Node n: tree.children)
                System.out.println(n.node);*/
            System.out.println("Ending\n\n");
            tree.children.addAll(divideWRT(tree.children.get(0), criticalValue, axis));
            tree.children.remove(0);
            System.out.println("At the end");
            /*for(Node n: tree.children)
                System.out.println(n.node);*/
            System.out.println("Ending\n\n");
        }
    }

    /*public void createChildren(Node tree, float []criticalValues, int index)
    {
        if(index>3)
            return;

        if(tree.children.size()==0)
        {
            tree.duplicateParent();
            tree.children.addAll(divideWRT(tree.children.get(0), criticalValues[i], "x"));
            tree.children.addAll(divideWRT(tree.children.get(0), criticalValues[i], "y"));
            tree.children.addAll(divideWRT(tree.children.get(0), criticalValues[i], "z"));
            tree.node=null;
        }

        for(int i=0; i<1; i++)
        {
            System.out.println("In createChildrenX for");
            for(Node n: tree.children)
                System.out.println(n.node);
            System.out.println("Ending\n\n");
            tree.children.addAll(divideWRT(tree.children.get(0), criticalValues[i], "x"));
            tree.children.remove(0);
            System.out.println("At the end");
            for(Node n: tree.children)
                System.out.println(n.node);
            System.out.println("Ending\n\n");
        }
    }*/

    /*public void createChildren(Node tree, float []criticalValues)
    {
        int size=tree.children.size();
        for(int i=0; i<size; i++)
        {
            System.out.println("In createChildrenX for");
            for(Node n: tree.children)
                System.out.println(n.node);
            System.out.println("Ending\n\n");
            tree.children.addAll(divideWRT(tree.children.get(0), criticalValues[i]));
            tree.children.remove(0);
            System.out.println("At the end");
            for(Node n: tree.children)
                System.out.println(n.node);
            System.out.println("Ending\n\n");
        }
    }*/


    public ArrayList<Node> divideWRT(Node node, float criticalValue, String axis)
    {
        ArrayList<Triangle> newTriangleAL1 = new ArrayList<>(), newTriangleAL12 = new ArrayList<>();
        for(Triangle tri: node.node.tris)
        {
            List<ArrayList<Triangle>> temp = xClip(tri, criticalValue, axis);
            if(temp.get(0)!=null)
            newTriangleAL1.addAll(temp.get(0));
            if(temp.get(1)!=null)
                newTriangleAL12.addAll(temp.get(1));
        }

        ArrayList<Node> toBeReturned = new ArrayList<>();
        Mesh m1=new Mesh(), m2 = new Mesh();
        m1.setTris(newTriangleAL1);
        m2.setTris(newTriangleAL12);
        if(newTriangleAL12.size()!=0)
            toBeReturned.add(new Node(m2));
        if(newTriangleAL1.size()!=0)
            toBeReturned.add(new Node(m1));
        newTriangleAL1=null;
        newTriangleAL12=null;
        return toBeReturned;
    }

    public List<ArrayList<Triangle>> xClip(Triangle tri, float criticalValue, String axis)
    {
        Vector<Vec3d> vec = new Vector<>();
        vec.add(tri.p[0]);
        vec.add(tri.p[1]);
        vec.add(tri.p[2]);

        int count=0;
        if(axis.equalsIgnoreCase("x"))
        {
            if(tri.p[0].x<criticalValue)
                count++;
            if(tri.p[1].x<criticalValue)
                count++;
            if(tri.p[2].x<criticalValue)
                count++;
        }
        else if(axis.equalsIgnoreCase("y"))
        {
            if(tri.p[0].y<criticalValue)
                count++;
            if(tri.p[1].y<criticalValue)
                count++;
            if(tri.p[2].y<criticalValue)
                count++;
        }
        else if(axis.equalsIgnoreCase("z"))
        {
            if(tri.p[0].z<criticalValue)
                count++;
            if(tri.p[1].z<criticalValue)
                count++;
            if(tri.p[2].z<criticalValue)
                count++;
        }
        /*if(count==3)
            return null;*/

        if(count==1)
        {
            int lowestOne=-1;
            if(axis.equalsIgnoreCase("x"))
            {
                if(tri.p[0].x<tri.p[1].x)
                    lowestOne=0;
                else
                    lowestOne=1;
                if(tri.p[lowestOne].x>tri.p[2].x)
                    lowestOne=2;
            }

            else if(axis.equalsIgnoreCase("y"))
            {
                if(tri.p[0].y<tri.p[1].y)
                    lowestOne=0;
                else
                    lowestOne=1;
                if(tri.p[lowestOne].y>tri.p[2].y)
                    lowestOne=2;
            }

            else if(axis.equalsIgnoreCase("z"))
            {
                if(tri.p[0].z<tri.p[1].z)
                    lowestOne=0;
                else
                    lowestOne=1;
                if(tri.p[lowestOne].z>tri.p[2].z)
                    lowestOne=2;
            }

            Vec3d tempV=tri.p[lowestOne];
            Vec3d temp = vec.elementAt(lowestOne);
            vec.remove(lowestOne);
            Vec3d new2=null, new3=null;

            if(axis.equalsIgnoreCase("x"))
            {
                float newZ=zcalc(vec.elementAt(0).z, vec.elementAt(0).x, tempV.z, tempV.x, criticalValue);
                float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).x, tempV.y, tempV.x, criticalValue);
                new2 = new Vec3d(criticalValue, newY, newZ);
                newZ=zcalc(vec.elementAt(1).z, vec.elementAt(1).x, tempV.z, tempV.x, criticalValue);
                newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).x, tempV.y, tempV.x, criticalValue);
                new3 = new Vec3d(criticalValue, newY, newZ);
            }

            else if(axis.equalsIgnoreCase("y"))
            {
                float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).y, tempV.x, tempV.y, criticalValue);
                float newZ=zcalc(vec.elementAt(0).z, vec.elementAt(0).y, tempV.z, tempV.y, criticalValue);
                new2 = new Vec3d(newX, criticalValue, newZ );
                newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).y, tempV.x, tempV.y, criticalValue);
                newZ=zcalc(vec.elementAt(1).z, vec.elementAt(1).y, tempV.z, tempV.y, criticalValue);
                new3 = new Vec3d(newX, criticalValue, newZ);
            }

            else if(axis.equalsIgnoreCase("z"))
            {
                float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).z, tempV.x, tempV.z, criticalValue);
                float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).z, tempV.y, tempV.z, criticalValue);
                new2 = new Vec3d(newX, newY, criticalValue);
                newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).z, tempV.x, tempV.z, criticalValue);
                newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).z, tempV.y, tempV.z, criticalValue);
                new3 = new Vec3d(newX, newY, criticalValue);
            }

            vec.add(new2);
            vec.add(new3);
            ArrayList<Triangle> arrayListForMesh1 = new ArrayList<>(), arrayListForMesh2 = new ArrayList<>();

            //creating arraylist of one part of mesh
            arrayListForMesh1.add(new Triangle(vec.elementAt(0), vec.elementAt(1), vec.elementAt(2)));
            arrayListForMesh1.get(0).setColor(tri.getColor());
            arrayListForMesh1.add(new Triangle(vec.elementAt(1), vec.elementAt(2), vec.elementAt(3)));
            arrayListForMesh1.get(1).setColor(tri.getColor());
            List<ArrayList<Triangle>> arrayListToBeReturned = new ArrayList<>();
            arrayListToBeReturned.add(arrayListForMesh1);

            //creating arraylist of another part of mesh
            arrayListForMesh2.add(new Triangle(vec.elementAt(2), vec.elementAt(3), temp));
            arrayListToBeReturned.add(arrayListForMesh2);

            return arrayListToBeReturned;
        }
        else if(count==2)
        {
            //System.out.println("ShitCount 2");


            int lowestOne=-1;
            if(axis.equalsIgnoreCase("x"))
            {
                tri.p[0].x*=-1; tri.p[1].x*=-1; tri.p[2].x*=-1;
                if(tri.p[0].x<tri.p[1].x)
                    lowestOne=0;
                else
                    lowestOne=1;
                if(tri.p[lowestOne].x>tri.p[2].x)
                    lowestOne=2;
                tri.p[0].x*=-1; tri.p[1].x*=-1; tri.p[2].x*=-1;
            }

            else if(axis.equalsIgnoreCase("y"))
            {
                if(tri.p[0].y>tri.p[1].y)
                    lowestOne=0;
                else
                    lowestOne=1;
                if(tri.p[lowestOne].y<tri.p[2].y)
                    lowestOne=2;
            }

            else if(axis.equalsIgnoreCase("z"))
            {
                if(tri.p[0].z>tri.p[1].z)
                    lowestOne=0;
                else
                    lowestOne=1;
                if(tri.p[lowestOne].z<tri.p[2].z)
                    lowestOne=2;
            }

            /*int lowestOne;
            if(tri.p[0].x<tri.p[1].x)
                lowestOne=0;
            else
                lowestOne=1;
            if(tri.p[lowestOne].x>tri.p[2].x)
                lowestOne=2;*/


            Vec3d tempV=tri.p[lowestOne];
            vec.remove(lowestOne);
            Vec3d new2=null, new3=null;

            if(axis.equalsIgnoreCase("x"))
            {
                float newZ=zcalc(vec.elementAt(0).z, vec.elementAt(0).x, tempV.z, tempV.x, criticalValue);
                float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).x, tempV.y, tempV.x, criticalValue);
                new2 = new Vec3d(criticalValue, newY, newZ);
                newZ=zcalc(vec.elementAt(1).z, vec.elementAt(1).x, tempV.z, tempV.x, criticalValue);
                newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).x, tempV.y, tempV.x, criticalValue);
                new3 = new Vec3d(criticalValue, newY, newZ);
            }

            else if(axis.equalsIgnoreCase("y"))
            {
                float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).y, tempV.x, tempV.y, criticalValue);
                float newZ=zcalc(vec.elementAt(0).z, vec.elementAt(0).y, tempV.z, tempV.y, criticalValue);
                new2 = new Vec3d(newX, criticalValue, newZ );
                newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).y, tempV.x, tempV.y, criticalValue);
                newZ=zcalc(vec.elementAt(1).z, vec.elementAt(1).y, tempV.z, tempV.y, criticalValue);
                new3 = new Vec3d(newX, criticalValue, newZ);
            }

            else if(axis.equalsIgnoreCase("z"))
            {
                float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).z, tempV.x, tempV.z, criticalValue);
                float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).z, tempV.y, tempV.z, criticalValue);
                new2 = new Vec3d(newX, newY, criticalValue);
                newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).z, tempV.x, tempV.z, criticalValue);
                newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).z, tempV.y, tempV.z, criticalValue);
                new3 = new Vec3d(newX, newY, criticalValue);
            }

            Vector<Vec3d> vec2= new Vector<>();
            vec2.add(new2);
            vec2.add(new3);
            vec2.add(lowestOne, tempV);

            //creating arraylist of one part of mesh
            ArrayList<Triangle> arrayListForMesh1 = new ArrayList<>(), arrayListForMesh2 = new ArrayList<>();
            arrayListForMesh1.add(new Triangle(vec2.elementAt(0), vec2.elementAt(1), vec2.elementAt(2)));
            arrayListForMesh1.get(0).setColor(tri.getColor());
            List<ArrayList<Triangle>> arrayListToBeReturned = new ArrayList<>();
            arrayListToBeReturned.add(arrayListForMesh1);

            //creating arraylist of another part of mesh
            arrayListForMesh2.add(new Triangle(vec.elementAt(0), vec.elementAt(1), new2));
            arrayListForMesh2.add(new Triangle(vec.elementAt(1), new2, new3));
            arrayListToBeReturned.add(arrayListForMesh2);

            return arrayListToBeReturned;
        }

        if(count==3)
        {
            ArrayList<Triangle> arrayListForMesh1=null, arrayListForMesh2 = new ArrayList<>();
            arrayListForMesh2.add(tri);
            List<ArrayList<Triangle>> arrayListToBeReturned = new ArrayList<>();
            arrayListToBeReturned.add(arrayListForMesh1);
            arrayListToBeReturned.add(arrayListForMesh2);
            return arrayListToBeReturned;
        }

        ArrayList<Triangle> arrayListForMesh2=null, arrayListForMesh1 = new ArrayList<>();
        arrayListForMesh1.add(tri);
        List<ArrayList<Triangle>> arrayListToBeReturned = new ArrayList<>();
        arrayListToBeReturned.add(arrayListForMesh1);
        arrayListToBeReturned.add(arrayListForMesh2);
        return arrayListToBeReturned;
        //return al;
    }

    public float zcalc(float xoy1, float z1, float xoy3, float z3, float criticalValue)
    {
        if(z1==z3) {
            System.out.println("You died");
        }
        return (((xoy1-xoy3)*(criticalValue-z3)/(z1-z3))+xoy3);
    }
    public ArrayList<Mesh> getMeshList()
    {
        /*float f=-0.5f, i=1;
        int j=0;
        float [] arr = new float[]{1,-1,1.5f,-1.5f,2,-2};
        ArrayList<Mesh> meshList = new ArrayList<>();
        System.out.println(tree.node);*/
        ArrayList<Mesh> toReturn = prepareMeshList(tree);
        /*for(Mesh m: toReturn)
        {
            *//*System.out.println("In getMeshList for");
            System.out.println(m);
            m.xTranslation=arr[j%6]*//*;
            *//*m.xTranslation=f*i;
            System.out.println("Mesh trans: " + m.xTranslation);
            if(j%2==1)
                i++;
            f*=-1;
            j++;*//*
            m.xTranslation=(float)(-5+Math.random()*12);
        }*/
        return toReturn;
    }

    public ArrayList<Mesh> prepareMeshList(Node pointer)
    {
        ArrayList<Mesh> meshList = new ArrayList<>();
        if(pointer == null)
        {
            System.out.println("Pointer is null");
            return meshList;
        }
        if(pointer.node!=null)
        {
            System.out.println("Pointer.node list will be added");
            meshList.add(pointer.node);
            System.out.println("In prepareMesh. pointer.node!=null and mesh size: " + meshList.size());
        }

        for(Node n: pointer.children)
        {
            //System.out.println("In for");
            meshList.addAll(prepareMeshList(n));
            System.out.println("In prepareMesh. In for and mesh size: " + meshList.size());
        }

        System.out.println("Ending here");
        return meshList;

    }

    public ArrayList<Mesh> getObbList()
    {
        return prepareObbList(tree);
    }


    public ArrayList<Mesh> prepareObbList(Node pointer)
    {
        ArrayList<Mesh> meshList = new ArrayList<>();
        if(pointer == null)
        {
            System.out.println("Pointer is null");
            return meshList;
        }
        if(pointer.node!=null)
        {
            System.out.println("Pointer.node list will be added");
            meshList.add(new Obb(pointer.node).getMesh(pointer.node));
            System.out.println("In prepareMesh. pointer.node!=null and mesh size: " + meshList.size());
        }

        for(Node n: pointer.children)
        {
            //System.out.println("In for");
            meshList.addAll(prepareObbList(n));
            System.out.println("In prepareMesh. In for and mesh size: " + meshList.size());
        }

        System.out.println("Ending here");
        return meshList;

    }

    public Vec3d calcCriticalValue(Vec3d min, Vec3d max)
    {
        return new Vec3d((min.x+max.x)/2.0f, (min.y+max.y)/2.0f, (min.z+max.z)/2.0f);
    }





    /*public ArrayList<Triangle> zClip(Triangle tri, float criticalValue)
    {
        Vector <Vec3d> vec = new Vector<>();
        vec.add(tri.p[0]);
        vec.add(tri.p[1]);
        vec.add(tri.p[2]);

        int count=0;

        if(tri.p[0].z<criticalValue)
            count++;
        if(tri.p[1].z<criticalValue)
            count++;
        if(tri.p[2].z<criticalValue)
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
            float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).z, tempV.x, tempV.z, criticalValue);
            float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).z, tempV.y, tempV.z, criticalValue);
            Vec3d new2 = new Vec3d(newX, newY, criticalValue);
            newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).z, tempV.x, tempV.z, criticalValue);
            newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).z, tempV.y, tempV.z, criticalValue);
            Vec3d new3 = new Vec3d(newX, newY, criticalValue);
            vec.add(new2);
            vec.add(new3);
            ArrayList<Triangle> al = new ArrayList<>();
            al.add(new Triangle(vec.elementAt(0), vec.elementAt(1), vec.elementAt(2)));
            al.get(0).setColor(tri.getColor());
            al.add(new Triangle(vec.elementAt(1), vec.elementAt(2), vec.elementAt(3)));
            al.get(1).setColor(tri.getColor());
            return al;
            *//*Triangle triArr[] = new Triangle[2];
            triArr[0]=new Triangle(vec.elementAt(0), vec.elementAt(1), vec.elementAt(2));
            triArr[0].setColor(tri.getColor());
            triArr[1]=new Triangle(vec.elementAt(1), vec.elementAt(2), vec.elementAt(3));
            triArr[1].setColor(tri.getColor());
            return  triArr;*//*
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
            float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).z, tempV.x, tempV.z, criticalValue);
            float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).z, tempV.y, tempV.z, criticalValue);
            Vec3d new2 = new Vec3d(newX, newY, criticalValue);
            newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).z, tempV.x, tempV.z, criticalValue);
            newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).z, tempV.y, tempV.z, criticalValue);
            Vec3d new3 = new Vec3d(newX, newY, criticalValue);
            vec.add(lowestOne, new3);
            vec.add(lowestOne, new2);

            Vector<Vec3d> vec2= new Vector<>();
            vec2.add(new2);
            vec2.add(new3);
            vec2.add(lowestOne, tempV);

            ArrayList<Triangle> al = new ArrayList<>();
            al.add(new Triangle(vec.elementAt(0), vec.elementAt(1), vec.elementAt(2)));
            al.get(0).setColor(tri.getColor());
            *//*al.add(new Triangle(vec.elementAt(1), vec.elementAt(2), vec.elementAt(3)));
            al.get(1).setColor(tri.getColor());*//*
            return al;

            *//*Triangle triArr[] = new Triangle[1];
            triArr[0]= new Triangle(vec2.elementAt(0), vec2.elementAt(1), vec2.elementAt(2));
            triArr[0].setColor(tri.getColor());
            return  triArr;*//*
        }
        ArrayList<Triangle> al = new ArrayList<>();
        al.add(tri);
        return al;
        *//*Triangle arr[] = new Triangle[1];
        arr[0]=tri;
        return  arr;*//*
    }

    public ArrayList<Triangle> yClip(Triangle tri, float criticalValue)
    {
        Vector <Vec3d> vec = new Vector<>();
        vec.add(tri.p[0]);
        vec.add(tri.p[1]);
        vec.add(tri.p[2]);

        int count=0;

        if(tri.p[0].y<criticalValue)
            count++;
        if(tri.p[1].y<criticalValue)
            count++;
        if(tri.p[2].y<criticalValue)
            count++;
        System.out.println(tri.p[0]+"\n"+tri.p[1]+"\n"+tri.p[2]);
        if(count==3)
        {
            System.out.println("Count is: 3");
            return null;
        }
        else
            System.out.println("Count is: " + count);


        if(count==1)
        {
            int lowestOne;
            if(tri.p[0].y<tri.p[1].y)
                lowestOne=0;
            else
                lowestOne=1;
            if(tri.p[lowestOne].y>tri.p[2].y)
                lowestOne=2;
            Vec3d tempV=tri.p[lowestOne];
            vec.remove(lowestOne);
            float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).y, tempV.x, tempV.y, criticalValue);
            float newZ=zcalc(vec.elementAt(0).z, vec.elementAt(0).y, tempV.z, tempV.y, criticalValue);
            Vec3d new2 = new Vec3d(newX, criticalValue, newZ );
            newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).y, tempV.x, tempV.y, criticalValue);
            newZ=zcalc(vec.elementAt(1).z, vec.elementAt(1).y, tempV.z, tempV.y, criticalValue);
            Vec3d new3 = new Vec3d(newX, criticalValue, newZ);
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

            float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).y, tempV.x, tempV.y, criticalValue);
            float newZ=zcalc(vec.elementAt(0).z, vec.elementAt(0).y, tempV.z, tempV.y, criticalValue);
            Vec3d new2 = new Vec3d(newX, criticalValue, newZ );
            newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).y, tempV.x, tempV.y, criticalValue);
            newZ=zcalc(vec.elementAt(1).z, vec.elementAt(1).y, tempV.z, tempV.y, criticalValue);
            Vec3d new3 = new Vec3d(newX, criticalValue, newZ);

            System.out.println("New shits: " + new2 + "\n" + new3);
            vec.add(lowestOne, new3);
            vec.add(lowestOne, new2);

            Vector<Vec3d> vec2= new Vector<>();
            vec2.add(new2);
            vec2.add(new3);
            vec2.add(lowestOne, tempV);

            Triangle triArr[] = new Triangle[1];
            triArr[0]= new Triangle(vec2.elementAt(0), vec2.elementAt(1), vec2.elementAt(2));
            triArr[0].setColor(tri.getColor());
            System.out.println("Before return: " + triArr[0]);
            return  triArr;
        }
        Triangle arr[] = new Triangle[1];
        arr[0]=tri;
        return  arr;
    }*/



    /*public void swap(Triangle tri, String field)
    {
        if(field.equals("z"))
        {
            float temp=tri.p[0].x;
            tri.p[0].x=tri.p[0].z;
            tri.p[0].z=temp;

            temp=tri.p[1].x;
            tri.p[1].x=tri.p[1].z;
            tri.p[1].z=temp;

            temp=tri.p[2].x;
            tri.p[2].x=tri.p[2].z;
            tri.p[2].z=temp;
        }
    }

    public Vec3d getNewVec(float criticalValue, float newY, float newZ, String field)
    {
        if(field.equals("z"))
        {
            float temp=criticalValue;
            criticalValue=newZ;
            newZ=temp;
        }

        return new Vec3d(criticalValue, newY, newZ);
    }


    public Triangle[] zClip2(Triangle tri, float criticalValue, String field)
    {
        swap(tri, field);
        System.out.println("After swapping: " + tri);
        System.out.println(criticalValue);
        Vector<Vec3d> vec = new Vector<>();
        vec.add(tri.p[0]);
        vec.add(tri.p[1]);
        vec.add(tri.p[2]);

        int count=0;
        if(tri.p[0].x<criticalValue)
        {
            //System.out.println("This is strange: " + tri.p[0].x + ", " + criticalValue);
            count++;
        }
        if(tri.p[1].x<criticalValue)
            count++;
        if(tri.p[2].x<criticalValue)
            count++;
        if(count==3)
        {
            System.out.println("Count is 3");
            return null;
        }

        if(count==1)
        {
            System.out.println("ShitCount 1");
            int lowestOne;
            if(tri.p[0].x<tri.p[1].x)
                lowestOne=0;
            else
                lowestOne=1;
            if(tri.p[lowestOne].x>tri.p[2].x)
                lowestOne=2;
            Vec3d tempV=tri.p[lowestOne];
            vec.remove(lowestOne);
            float newZ=zcalc(vec.elementAt(0).z, vec.elementAt(0).x, tempV.z, tempV.x, criticalValue);
            float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).x, tempV.y, tempV.x, criticalValue);
            //Vec3d new2 = new Vec3d(criticalValue, newY, newZ);
            Vec3d new2= getNewVec(criticalValue, newY, newZ, field);
            newZ=zcalc(vec.elementAt(1).z, vec.elementAt(1).x, tempV.z, tempV.x, criticalValue);
            newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).x, tempV.y, tempV.x, criticalValue);
            //Vec3d new3 = new Vec3d(criticalValue, newY, newZ);
            Vec3d new3= getNewVec(criticalValue, newY, newZ, field);
            vec.add(new2);
            vec.add(new3);
            Triangle triArr[] = new Triangle[2];
            triArr[0]=new Triangle(vec.elementAt(0), vec.elementAt(1), vec.elementAt(2));
            swap(triArr[0], field);
            triArr[0].setColor(tri.getColor());
            triArr[1]=new Triangle(vec.elementAt(1), vec.elementAt(2), vec.elementAt(3));
            swap(triArr[1], field);
            triArr[1].setColor(tri.getColor());
            return  triArr;
        }
        else if(count==2)
        {
            System.out.println("ShitCount 2");
            tri.p[0].x*=-1; tri.p[1].x*=-1; tri.p[2].x*=-1;
            int lowestOne;
            if(tri.p[0].x<tri.p[1].x)
                lowestOne=0;
            else
                lowestOne=1;
            if(tri.p[lowestOne].x>tri.p[2].x)
                lowestOne=2;

            System.out.println(tri.p[0] + " " + tri.p[1] + " " + tri.p[2] + " ");
            System.out.println(lowestOne);

            tri.p[0].x*=-1; tri.p[1].x*=-1; tri.p[2].x*=-1;
            Vec3d tempV=tri.p[lowestOne];
            vec.remove(lowestOne);
            //System.out.println();
            float newZ=zcalc(vec.elementAt(0).z, vec.elementAt(0).x, tempV.z, tempV.x, criticalValue);
            float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).x, tempV.y, tempV.x, criticalValue);
            //Vec3d new2 = new Vec3d(criticalValue, newY, newZ);
            Vec3d new2= getNewVec(criticalValue, newY, newZ, field);
            newZ=zcalc(vec.elementAt(1).z, vec.elementAt(1).x, tempV.z, tempV.x, criticalValue);
            newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).x, tempV.y, tempV.x, criticalValue);
            //Vec3d new3 = new Vec3d(criticalValue, newY, newZ);
            Vec3d new3= getNewVec(criticalValue, newY, newZ, field);
            vec.add(lowestOne, new3);
            vec.add(lowestOne, new2);

            Vector<Vec3d> vec2= new Vector<>();
            vec2.add(new2);
            vec2.add(new3);
            vec2.add(lowestOne, tempV);

            Triangle triArr[] = new Triangle[1];
            triArr[0]= new Triangle(vec2.elementAt(0), vec2.elementAt(1), vec2.elementAt(2));
            swap(triArr[0], field);
            triArr[0].setColor(tri.getColor());
            return  triArr;
        }
        Triangle arr[] = new Triangle[1];
        arr[0]=tri;
        swap(arr[0], field);
        return  arr;
    }*/


}


