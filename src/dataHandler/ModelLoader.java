package dataLoader;

import threeDItems.Mesh;
import threeDItems.Triangle;
import threeDItems.Vec3d;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class ModelLoader {

    public Mesh meshLoader(String fileName)
    {
        Mesh mesh = new Mesh();
        Vector<Vec3d> pointBuffer = new Vector<>();
        Vector<Vec3d> normalBuffer = new Vector<>();
        File file = new File("D:\\ideaIntellij\\olcge\\src\\resources\\"+fileName);
        try {
            Scanner jin = new Scanner(file);
            while(jin.hasNext())
            {
                String s, s1, s2, s3;
                s= jin.next();
                //if(s.isEmpty() || s.startsWith("vn") || (s.charAt(0)!='v' && s.charAt(0)!='f'))
                /*if(s.isEmpty() || !((s.startsWith("v") || s.startsWith("f")) && s.length()==1))
                //if(s.length()!=1)
                {
                    //if(!s.isEmpty())
                        jin.nextLine();
                    //System.out.println("Continuing");
                    continue;
                }*/
                /*s1=jin.next();
                s2=jin.next();
                s3=jin.next();*/

               // System.out.println(s + " :: " + s1);
                //if(!s.startsWith("vn") && s.startsWith("v"))
                if(s.startsWith("v") && s.length()==1)
                {
                    s1=jin.next();
                    s2=jin.next();
                    s3=jin.next();
                    pointBuffer.add(new Vec3d(Float.parseFloat(s1), Float.parseFloat(s2), Float.parseFloat(s3)));
                }
                //else if (s.startsWith("vn"))
                    //normalBuffer.add(new Vec3d(Float.parseFloat(s1), Float.parseFloat(s2), Float.parseFloat(s3)));
                else if (s.startsWith("f") && s.length()==1)
                {
                    s1=jin.next();
                    s2=jin.next();
                    s3=jin.next();
                    String temp1[] = s1.split("/");
                    String temp2[] = s2.split("/");
                    String temp3[] = s3.split("/");
                    //for(int i=0; i<temp.length; i++)
                        //System.out.println(temp.length + ":length string:" +temp[i]);
                    //System.out.println(temp1[0] + " " + temp2[0] + " " + temp3[0]);
                    Triangle newtri = new Triangle(pointBuffer.elementAt(Integer.parseInt(temp1[0])-1), pointBuffer.elementAt(Integer.parseInt(temp2[0])-1), pointBuffer.elementAt(Integer.parseInt(temp3[0])-1));
                    //newtri.normal=normalBuffer.elementAt(Integer.parseInt(temp1[2])-1);
                    //System.out.println(temp1[0] + " " + temp2[0] + " " + temp3[0]);
                    //System.out.println(s1 + s2 + s3);
                    mesh.addTri(newtri);
                    //System.out.println("tri added");
                    //System.out.println(newtri);

                }
            }
            //System.out.println(mesh);
            //System.out.println("Point Buffer: " + pointBuffer.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("Triangle cunt: " + mesh.tris.size());
        return mesh;
    }

}
