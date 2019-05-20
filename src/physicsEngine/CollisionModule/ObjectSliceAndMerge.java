package physicsEngine.CollisionModule;

import threeDItems.Mesh;
import threeDItems.Triangle;
import threeDItems.Vec3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import threeDItems.ObbMesh;

class Node{
    Mesh node;
    ArrayList<Node> children = new ArrayList<>();

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
    /*int wantedDivisionDepth=4;
    float eps=0.001f;*/
    int wantedDivisionDepth;
    float eps;

    public ObjectSliceAndMerge(Mesh mesh, int wantedDivisionDepth, float eps)
    {
        tree = new Node(mesh);
        this.wantedDivisionDepth=wantedDivisionDepth;
        this.eps=eps;
    }

    public void createTree()
    {
        createTreeStarts(tree, 0);
    }

    public void createTreeStarts(Node node, int depth)
    {
        if(depth>=wantedDivisionDepth)
        {
            return;
        }

        if(node.children.size()==0)
        {
            node.duplicateParent();
            /*Obb obb = new Obb(node.node);
            Vec3d criticalValue = calcCriticalValue(obb.min, obb.max);*/
            Vec3d criticalValue = calcCriticalValue(node.node.min, node.node.max);
            createChildren(node, criticalValue.x,"x");
            createChildren(node, criticalValue.y,"y");
            createChildren(node, criticalValue.z,"z");
            node.node=null;
        }

        for (Node n:node.children)
        {
            createTreeStarts(n, depth+1);
        }
    }

    public void createChildren(Node tree, float criticalValue, String axis)
    {
        int size=tree.children.size();
        for(int i=0; i<size; i++)
        {
            /*for(Node n: tree.children)
               System.out.println(n.node);*/
            tree.children.addAll(divideWRT(tree.children.get(0), criticalValue, axis));
            tree.children.remove(0);
            /*for(Node n: tree.children)
               System.out.println(n.node);*/
        }
    }


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
        Mesh m1=new Mesh(true), m2 = new Mesh(true);
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
            //ystem.out.println("ShitCount 2");


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
            *//*ystem.out.println("In getMeshList for");
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
            return meshList;
        }
        if(pointer.node!=null)
        {
            meshList.add(pointer.node);
        }

        for(Node n: pointer.children)
        {
            //ystem.out.println("In for");
            meshList.addAll(prepareMeshList(n));
        }

        return meshList;

    }

    public ArrayList<Mesh> getObbList()
    {
        return prepareObbList(tree);
    }

    public ArrayList<ObbMesh> getObbMeshList()
    {
        return prepareObbMeshList(tree);
    }

    public ArrayList<ObbMesh> prepareObbMeshList(Node pointer)
    {
        ArrayList<ObbMesh> meshList = new ArrayList<>();
        if(pointer == null)
        {
            System.out.println("Pointer is null");
            return meshList;
        }
        if(pointer.node!=null)
        {
            //meshList.add(new Obb(pointer.node).getMesh(pointer.node));
            Mesh tempMesh = pointer.node.getObb().getMesh(pointer.node);
            meshList.add(new Obb(tempMesh).cube);
        }

        for(Node n: pointer.children)
        {
            //ystem.out.println("In for");
            meshList.addAll(prepareObbMeshList(n));
        }

        return meshList;

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
            //meshList.add(new Obb(pointer.node).getMesh(pointer.node));
            Mesh tempMesh = pointer.node.getObb().getMesh(pointer.node);
            meshList.add(tempMesh);
        }

        for(Node n: pointer.children)
        {
            //ystem.out.println("In for");
            meshList.addAll(prepareObbList(n));
        }

        return meshList;

    }

    public Vec3d calcCriticalValue(Vec3d min, Vec3d max)
    {
        return new Vec3d((min.x+max.x)/2.0f, (min.y+max.y)/2.0f, (min.z+max.z)/2.0f);
        //return new Vec3d((float)Math.random()*(max.x-min.x)+min.x, (float)Math.random()*(max.y-min.y)+min.y, (float)Math.random()*(max.z-min.z)+min.z);
    }

    public List clusterize()
    {
       System.out.println("STArts");
        LinkedList list = new LinkedList();
        list.addAll(getObbList());

        //list.addAll(getObbMeshList());

       System.out.println("SHIT st: " + list.size());
        LinkedList <LinkedList<Float>> table = new LinkedList<>();

        int counter=0, stop=50000;
        boolean restart=false;
        for(int i=0; i<list.size(); i++)
        {
            counter++;
            if(counter>stop)
                break;
            table.add(new LinkedList<>());

            for(int j=0; j<i; j++)
            {

                float mergeVol=-calcVol((ObbMesh)list.get(i))-calcVol((ObbMesh)list.get(j))+calcMergeVol((ObbMesh)list.get(i), (ObbMesh)list.get(j));
                table.get(i).add(mergeVol);

                if((mergeVol)<=eps)
                {
                    merge(list, i, j);

                    updatePartialTable(list, table, i, j);

                    i=table.size()-2;
                    break;
                }
            }

            /*System.out.println("Printing entire list");
            for(Object o : list)
            {
                System.out.println(((Mesh)o).getStats());
            }*/
        }

        //printTable(table);



        return list;
    }


    public void merge(LinkedList<Mesh> list, int indexI, int indexJ)
    {
        ObbMesh mesh = new ObbMesh(true);
        //ArrayList <Triangle> al = new ArrayList<>();
        mesh.tris.clear();
        //al.addAll(list.get(indexI).tris);
        //al.addAll(list.get(indexJ).tris);
        //mesh.setTris(al);
       //System.out.println("\n\nMerge has started!\n");
        ObbMesh mesh1=(ObbMesh)list.get(indexI);
        ObbMesh mesh2 = (ObbMesh)list.get(indexJ);
       /*System.out.println("Mesh 1 stats: " + mesh1.getStats());
       System.out.println("Mesh 2 stats: " + mesh2.getStats());*/
        list.remove(indexI);
        if(indexI<indexJ)
            indexJ--;
        list.remove(indexJ);
        /*Vec3d m1min= new Vec3d(mesh1.min.x*mesh1.initScaleX+mesh1.initTransX, mesh1.min.y*mesh1.initScaleY+mesh1.initTransY, mesh1.min.z*mesh1.initScaleZ+mesh1.initTransZ);
        Vec3d m1max= new Vec3d(mesh1.max.x*mesh1.initScaleX+mesh1.initTransX, mesh1.max.y*mesh1.initScaleY+mesh1.initTransY, mesh1.max.z*mesh1.initScaleZ+mesh1.initTransZ);
        Vec3d m2min= new Vec3d(mesh2.min.x*mesh2.initScaleX+mesh2.initTransX, mesh2.min.y*mesh2.initScaleY+mesh2.initTransY, mesh2.min.z*mesh2.initScaleZ+mesh2.initTransZ);
        Vec3d m2max= new Vec3d(mesh2.max.x*mesh2.initScaleX+mesh2.initTransX, mesh2.max.y*mesh2.initScaleY+mesh2.initTransY, mesh2.max.z*mesh2.initScaleZ+mesh2.initTransZ);
        Vec3d newMin = new Vec3d(Math.min(m1min.x, m2min.x),Math.min(m1min.y, m2min.y),Math.min(m1min.z, m2min.z));
        Vec3d newMax = new Vec3d(Math.max(m1max.x, m2max.x),Math.max(m1max.y, m2max.y),Math.max(m1max.z, m2max.z));*/
        Vec3d newMin = new Vec3d(Math.min(mesh1.min.x, mesh2.min.x),Math.min(mesh1.min.y, mesh2.min.y),Math.min(mesh1.min.z, mesh2.min.z));
        Vec3d newMax = new Vec3d(Math.max(mesh1.max.x, mesh2.max.x),Math.max(mesh1.max.y, mesh2.max.y),Math.max(mesh1.max.z, mesh2.max.z));
        //System.out.println(m1min + ", " + m1max + ", " + m2min + ", " + m2max + ", " + newMin + ", " + newMax);
        list.add(new Obb(newMin, newMax, 0).getMesh());
    }

    public void updatePartialTable(List<Mesh> list, LinkedList <LinkedList<Float>> table, int minI, int minJ)
    {
        //ystem.out.println("In update: " + minI + " " + minJ);
        table.remove(minI);
        if(!table.isEmpty())
        {
            table.remove(minJ);
            if(table.isEmpty())
                table.add(new LinkedList<>());
        }

        for(List l:table)
        {
            int temp=minI;
            if(minJ<l.size())
            {
                l.remove(minJ);
                if(minJ<minI)
                    temp--;
            }

            if(temp<l.size())
                l.remove(temp);
        }
    }

    public void updateTable(List<Mesh> list, LinkedList <LinkedList<Float>> table, int minI, int minJ)
    {
        table.remove(minI);
        for(List l:table)
        {
            l.remove(minJ);
            int temp=minI;
            if(minJ<minI)
                temp--;
            if(!l.isEmpty())
                l.remove(temp);
        }
        table.add(new LinkedList<>());

        float vol = calcVol((ObbMesh)list.get(list.size()-1)), minVol=500000f;
       System.out.println("Size: "+list.size());
        for(int j=0; j<list.size()-1; j++)
        {
            float mergeVol=calcMergeVol((ObbMesh)list.get(list.size()-1), (ObbMesh)list.get(j));
            table.get(list.size()).add(mergeVol);
            if(mergeVol<minVol)
            {
                minVol=mergeVol;
                minI=list.size()-1;
                minJ=j;
            }
        }
        table.get(list.size()).add(vol);
    }

    public void printTable(LinkedList<LinkedList<Float>> table)
    {
       System.out.println("Printing table: ");
        for(List l: table)
        {
            for(Object o:l)
            {
                System.out.print(o+"\t\t");
            }
           System.out.println(" -");
        }
    }

    public void printTableSize(LinkedList<LinkedList<Float>> table)
    {
        if(table!=null)
        {
           System.out.println("Sizes comping up:");
            for(List l:table)
               System.out.println(l.size());
        }
        else
           System.out.println("table is null");
    }

    public float calcVol(ObbMesh mesh)
    {
        Vec3d temp = new Vec3d(mesh.max.x-mesh.min.x, mesh.max.y-mesh.min.y, mesh.max.z-mesh.min.z);
        //float f =temp.x*temp.y*temp.z*(mesh.xScale*mesh.yScale*mesh.zScale)*(mesh.initScaleX*mesh.initScaleY*mesh.initScaleZ);
        float f =temp.x*temp.y*temp.z;
        return Math.abs(f);
    }

    public float calcMergeVol(ObbMesh mesh1, ObbMesh mesh2)
    {
        /*Vec3d m1min= new Vec3d(mesh1.min.x*mesh1.initScaleX+mesh1.initTransX, mesh1.min.y*mesh1.initScaleY+mesh1.initTransY, mesh1.min.z*mesh1.initScaleZ+mesh1.initTransZ);
        Vec3d m1max= new Vec3d(mesh1.max.x*mesh1.initScaleX+mesh1.initTransX, mesh1.max.y*mesh1.initScaleY+mesh1.initTransY, mesh1.max.z*mesh1.initScaleZ+mesh1.initTransZ);
        Vec3d m2min= new Vec3d(mesh2.min.x*mesh2.initScaleX+mesh2.initTransX, mesh2.min.y*mesh2.initScaleY+mesh2.initTransY, mesh2.min.z*mesh2.initScaleZ+mesh2.initTransZ);
        Vec3d m2max= new Vec3d(mesh2.max.x*mesh2.initScaleX+mesh2.initTransX, mesh2.max.y*mesh2.initScaleY+mesh2.initTransY, mesh2.max.z*mesh2.initScaleZ+mesh2.initTransZ);
        Vec3d newMin = new Vec3d(Math.min(m1min.x, m2min.x),Math.min(m1min.y, m2min.y),Math.min(m1min.z, m2min.z));
        Vec3d newMax = new Vec3d(Math.max(m1max.x, m2max.x),Math.max(m1max.y, m2max.y),Math.max(m1max.z, m2max.z));
        Vec3d temp = new Vec3d(newMax.x-newMin.x, newMax.y-newMin.y, newMax.z-newMin.z);*/

        Vec3d newMin = new Vec3d(Math.min(mesh1.min.x, mesh2.min.x),Math.min(mesh1.min.y, mesh2.min.y),Math.min(mesh1.min.z, mesh2.min.z));
        Vec3d newMax = new Vec3d(Math.max(mesh1.max.x, mesh2.max.x),Math.max(mesh1.max.y, mesh2.max.y),Math.max(mesh1.max.z, mesh2.max.z));
        Vec3d temp = new Vec3d(newMax.x-newMin.x, newMax.y-newMin.y, newMax.z-newMin.z);

        /*Vec3d temp1=new Vec3d(mesh1.initScaleX+mesh1.initTransX, mesh1.initScaleY+mesh1.initTransY, mesh1.initScaleZ+mesh1.initTransZ);
        Vec3d temp2=new Vec3d(mesh2.initScaleX+mesh2.initTransX, mesh2.initScaleY+mesh2.initTransY, mesh2.initScaleZ+mesh2.initTransZ);
        Vec3d temp = new Vec3d(temp2.x-temp1.x, temp2.y-temp1.y, temp2.z-temp1.z);*/
        return Math.abs(temp.x*temp.y*temp.z);
    }

    public List clusterizeOld()
    {
        LinkedList list = new LinkedList();
        list.addAll(getObbList());
        LinkedList <LinkedList<Float>> table = new LinkedList<>();

        int minI=-1, minJ=-1;
        float minVol=500000f;

        for(int i=0; i<list.size(); i++)
        {
            table.add(new LinkedList<Float>());
            float vol = calcVol((ObbMesh)list.get(i));


           System.out.println(((ObbMesh) list.get(i)).getStats());

            for(int j=0; j<i; j++)
            {
                float mergeVol=calcMergeVol((ObbMesh)list.get(i), (ObbMesh)list.get(j));
                table.get(i).add(mergeVol);
                if(mergeVol<minVol)
                {
                    minVol=mergeVol;
                    minI=i;
                    minJ=j;
                }
            }
            table.get(i).add(vol);
           /* if(minI>-1 && minJ>-1)
            {
                merge(list, minI, minJ);
                i--;
            }*/
        }

        printTable(table);

       System.out.println(minVol + " " + minI + " " + minJ);

        merge(list, minI, minJ);

        updateTable(list, table, minI, minJ);

        printTable(table);

        /*for(int i=1; i<2; i++)
        {
            for(int j=0; j<i; j++)
            {
                float mergeVol=table.get(i).get(j);
                if(mergeVol<minVol)
                {
                    minVol=mergeVol;
                    minI=i;
                    minJ=j;
                }
            }
        }*/

        return list;
    }


}


