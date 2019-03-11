package dataHandler;

import rendererEngine.itemBag.ItemBag;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh;
import threeDItems.Triangle;
import threeDItems.Vec3d;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class ModelLoader {
    public  Vector<String> fileNameVector = new Vector<>();
    public int id=0;


    public Vector<Mesh> loadAll()
    {
        Vector<Mesh> vec = new Vector<>();
        try {
            Scanner in = new Scanner(new File("D:\\ideaIntellij\\olcge\\src\\Games\\info.hma"));
            System.out.println("Trying to loadAll");

            //File info = new File("D:\\ideaIntellij\\olcge\\src\\Games\\info.hma");
            while(in.hasNext())
            {
                String meshName = in.next();
                System.out.println("Mesh name is: " + meshName);
                vec.add(meshLoader(meshName, false));

                vec.lastElement().xTheta=in.nextFloat(); vec.lastElement().yTheta=in.nextFloat(); vec.lastElement().zTheta=in.nextFloat();
                vec.lastElement().xTranslation=in.nextFloat(); vec.lastElement().yTranslation=in.nextFloat(); vec.lastElement().zTranslation=in.nextFloat();
                vec.lastElement().xScale=in.nextFloat(); vec.lastElement().yScale=in.nextFloat(); vec.lastElement().zScale=in.nextFloat();
                String scripted = in.next();
                vec.lastElement().id=in.nextInt();
                if(scripted.equals("true")) {
                    System.out.println("THE ID IS: " + vec.lastElement().id);
                    vec.lastElement().isScripted=true;
                    loadScript(vec.lastElement().id);

                }
                else
                    vec.lastElement().isScripted=false;

                System.out.println("Here, yTrans is: " + vec.lastElement().yTranslation);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return  vec;
    }

    public Mesh meshLoader(String fileName, boolean isGameObj)
    {
        Mesh mesh = new Mesh();
        if(isGameObj)
            mesh.id=id++;
        Vector<Vec3d> pointBuffer = new Vector<>();
        //Vector<Vec3d> normalBuffer = new Vector<>();
        File file = new File("D:\\ideaIntellij\\olcge\\src\\resources\\"+fileName);
        if(isGameObj)
            fileNameVector.add(fileName);
        try {
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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Triangle count: " + mesh.tris.size());
        return mesh;
    }

    public void loadScript(int id)
    {
        try {
            System.out.println("Trying to oad class for: " + "Games.scriptOfMesh"+id);
            ItemBag.addScript(id, (Inheritable) Class.forName("Games.scriptOfMesh"+id).newInstance());

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



}
