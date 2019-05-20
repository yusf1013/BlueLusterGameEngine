package dataHandler;

import physicsEngine.CollisionModule.Obb;
import rendererEngine.itemBag.ItemBag;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class ModelLoader {
    public  Vector<String> fileNameVector = new Vector<>();
    public int id=0;


    public List loadAll(boolean bool)
    {
        //ystem.out.println("fixie");
        try {
            return ((List)(new ObjectInputStream(new FileInputStream("Games/gameInfo.hlit"))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Vector<Mesh> loadAll()
    {
        Vector<Mesh> vec = new Vector<>();
        try {

            //Scanner in = new Scanner(new File("src\\Games\\info.hma"));
            Scanner in = new Scanner(new File("Games\\info.hma"));
            //Scanner in = new Scanner(new File("out\\artifacts\\olcge_jar\\olcge\\Games\\info.hma"));
           System.out.println("Trying to loadAll");

            //File info = new File("src\\Games\\info.hma");
            while(in.hasNext())
            {
                String meshName = in.next();
               System.out.println("Mesh name is: " + meshName + " HO HO HO HI HI HI");
                vec.add(meshLoader("Games\\", meshName, false));
               System.out.println("Load done");
                vec.lastElement().xTheta=in.nextFloat(); vec.lastElement().yTheta=in.nextFloat(); vec.lastElement().zTheta=in.nextFloat();
                vec.lastElement().xTranslation=in.nextFloat(); vec.lastElement().yTranslation=in.nextFloat(); vec.lastElement().zTranslation=in.nextFloat();
                vec.lastElement().xScale=in.nextFloat(); vec.lastElement().yScale=in.nextFloat(); vec.lastElement().zScale=in.nextFloat();
                String scripted = in.next();
                vec.lastElement().id=in.nextInt();
                String rb = in.next();

                if(scripted.equals("true")) {
                   System.out.println("Is scripted and THE ID IS: " + vec.lastElement().id);
                    vec.lastElement().isScripted=true;
                    loadScript(vec.lastElement().id);

                }
                else
                {
                   System.out.println("Is not scripted");
                    vec.lastElement().isScripted=false;
                }

                if(rb.equals("true")){
                   System.out.println("Is rigid body in model loader " + meshName);
                    vec.lastElement().isRigidBody=true;
                    vec.lastElement().obb = new Obb(new Vec3d(in.nextFloat(), in.nextFloat(), in.nextFloat()), new Vec3d(in.nextFloat(), in.nextFloat(), in.nextFloat()), vec.lastElement().id);

                    int size = in.nextInt();
                    for(int i=0; i<size; i++) {
                        ObbMesh om=null;

                        try{
                            om = obbMeshLoader("src\\resources\\","cube.obj");
                        } catch (FileNotFoundException e) {
                            try {
                                om = obbMeshLoader("resources\\","cube.obj");
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            }
                        }

                        om.xTheta = in.nextFloat();
                        om.yTheta = in.nextFloat();
                        om.zTheta = in.nextFloat();
                        om.xTranslation = in.nextFloat();
                        om.yTranslation = in.nextFloat();
                        om.zTranslation = in.nextFloat();
                        om.xScale = in.nextFloat();
                        om.yScale  = in.nextFloat();
                        om.zScale = in.nextFloat();
                        om.initTransX  = in.nextFloat();
                        om.initTransY = in.nextFloat();
                        om.initTransZ = in.nextFloat();
                        om.initScaleX = in.nextFloat();
                        om.initScaleY  = in.nextFloat();
                        om.initScaleZ = in.nextFloat();
                        vec.lastElement().obbList.add(om);
                    }
                }

                else
                    vec.lastElement().isRigidBody=false;

               System.out.println("Here, vecSize is: " + vec.size());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return  vec;
    }

    public Mesh meshLoader(String directory, String fileName, boolean isGameObj) throws FileNotFoundException {
        Mesh mesh = new Mesh();
        if(isGameObj)
            mesh.id=id++;
        if(isGameObj)
            fileNameVector.add(fileName);

        ArrayList<Triangle> al = MeshData.getDataPointReference(fileName);
        //Vector<Vec3d> vec = MeshData.getPointMapReference(fileName);
        if(al!=null)
        {
            mesh.tris=al;
            //mesh.p=vec;
            return mesh;
        }
        mesh.tris=new ArrayList<Triangle>();
        //mesh.p=new Vector<>();
        MeshData.insertDataArray(fileName, mesh.tris);
        //MeshData.insertDataArray(fileName, mesh.p);
        /*if(ItemBag.loadedMesh.get(fileName)!=null)
        {
            mesh.setTris(ItemBag.loadedMesh.get(fileName).tris);
            return mesh;
        }
       System.out.println("PASSING THE DANGER GUARD");*/
        Vector<Vec3d> pointBuffer = new Vector<>();
        //Vector<Vec3d> normalBuffer = new Vector<>();
        File file = new File(directory+fileName);
        //ystem.out.println("Model loader -> Mesh loader -> " + file.getAbsolutePath());
        /*try {*/
            Scanner jin = new Scanner(file);
            while(jin.hasNext())
            {
                String s, s1, s2, s3;
                s= jin.next();
                if(s.startsWith("v") && s.length()==1)
                {
                    s1=jin.next();
                    s2=jin.next();
                    s3=jin.next();
                    pointBuffer.add(new Vec3d(Float.parseFloat(s1), Float.parseFloat(s2), Float.parseFloat(s3)));
                }

                else if (s.startsWith("f") && s.length()==1)
                {
                    s1=jin.next();
                    s2=jin.next();
                    s3=jin.next();
                    String temp1[] = s1.split("/");
                    String temp2[] = s2.split("/");
                    String temp3[] = s3.split("/");

                    Triangle newtri = new Triangle(pointBuffer.elementAt(Integer.parseInt(temp1[0])-1), pointBuffer.elementAt(Integer.parseInt(temp2[0])-1), pointBuffer.elementAt(Integer.parseInt(temp3[0])-1));

                    mesh.addTri(newtri);

                }
            }
            //mesh.p=pointBuffer;
            return mesh;
    }

    public ObbMesh obbMeshLoader(String directory, String fileName) throws FileNotFoundException {
        ObbMesh mesh = new ObbMesh();

        ArrayList<Triangle> al = MeshData.getDataPointReference(fileName);
        //Vector<Vec3d> vec = MeshData.getPointMapReference(fileName);
        if(al!=null && !al.isEmpty())
        {
            mesh.tris=al;
            //mesh.p=vec;
            return mesh;
        }
        mesh.tris=new ArrayList<Triangle>();
        //mesh.p=new Vector<>();
        MeshData.insertDataArray(fileName, mesh.tris);
        //MeshData.insertDataArray(fileName, mesh.p);
        Vector<Vec3d> pointBuffer = new Vector<>();
        //Vector<Vec3d> normalBuffer = new Vector<>();
        File file = new File(directory+fileName);
        //ystem.out.println("Model loader -> Mesh loader -> " + file.getAbsolutePath());
        /*try {*/
        Scanner jin = new Scanner(file);
        while(jin.hasNext())
        {
            String s, s1, s2, s3;
            s= jin.next();
            if(s.startsWith("v") && s.length()==1)
            {
                s1=jin.next();
                s2=jin.next();
                s3=jin.next();
                pointBuffer.add(new Vec3d(Float.parseFloat(s1), Float.parseFloat(s2), Float.parseFloat(s3)));
            }

            else if (s.startsWith("f") && s.length()==1)
            {
                s1=jin.next();
                s2=jin.next();
                s3=jin.next();
                String temp1[] = s1.split("/");
                String temp2[] = s2.split("/");
                String temp3[] = s3.split("/");

                Triangle newtri = new Triangle(pointBuffer.elementAt(Integer.parseInt(temp1[0])-1), pointBuffer.elementAt(Integer.parseInt(temp2[0])-1), pointBuffer.elementAt(Integer.parseInt(temp3[0])-1));

                mesh.addTri(newtri);

            }
        }
        //mesh.p=pointBuffer;
        return mesh;
    }

    public void loadScript(int id)
    {
        try {
            ItemBag.addScript(id, (Inheritable) Class.forName( "Games.scriptOfMesh"+id).newInstance());
            /*Class cl = Class.forName("Games.scriptOfMesh"+id);
            Constructor ctor = cl.getDeclaredConstructor(ItemBag.getMeshMap().getClass());
            //ctor.setAccessible(true);
            //EmailAliases email = (EmailAliases)ctor.newInstance(defaultAliases);
            ItemBag.addScript(id, (Inheritable)ctor.newInstance(ItemBag.getMeshMap()));*/



        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void useless()
    {
        int i=0;
        i++;
    }



}
