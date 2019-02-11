package main;

import threeDItems.Triangle;
import threeDItems.Vec3d;

import java.util.Vector;

public class TempMain {
    public static void main(String[] args)
    {
        Vec3d p1 = new Vec3d(-0.07f, -0.13f, -0.03f);
        Vec3d p2 = new Vec3d(0.93f, -0.13f, 0.96f);
        Vec3d p3 = new Vec3d(-0.07f, -0.13f, 0.97f);
        Triangle tri = new Triangle(p1, p2, p3);
        System.out.println(tri);
        Triangle [] arr = zClip(tri, 1);
        System.out.println("After clipping");
        for (int i=0; i<arr.length; i++)
        {
            System.out.println(arr[i]);
        }
    }
    public static Triangle[] zClip(Triangle tri, int indind)
    {
        //System.out.println("ZClip is called");
        Vec3d p1=new Vec3d(), p2=new Vec3d(), p3=new Vec3d();
        Vector<Vec3d> vec = new Vector<>();
        vec.add(tri.p[0]);
        vec.add(tri.p[1]);
        vec.add(tri.p[2]);

        int count =0, ind1=0, ind2=0, ind3=0;

        if(tri.p[0].z<0.1)
            count++;
        if(tri.p[1].z<0.1)
            count++;
        if(tri.p[2].z<0.1)
            count++;
        //System.out.println("Count: " + count);
        if(count==3)
            return null;

        if(count==1)
        {
            //System.out.println("Count is 1");
            int lowestOne;
            if(tri.p[0].z<tri.p[1].z)
                lowestOne=0;
            else
                lowestOne=1;
            if(tri.p[lowestOne].z>tri.p[2].z)
                lowestOne=2;
            Vec3d tempV=tri.p[lowestOne];
            vec.remove(lowestOne);
            System.out.println(vec.elementAt(0));
            System.out.println(tempV);
            float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).z, tempV.x, tempV.z);
            System.out.println("New x: " + newX);
            //System.out.println(vec.elementAt(0));
            //System.out.println(tempV);
            //System.out.print("Expect deatto: ");
            float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).z, tempV.y, tempV.z);
            //System.out.println("");



            Vec3d new2 = new Vec3d(newX, newY, 0.1f);
            //System.out.print("Expect deatto: ");
            newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).z, tempV.x, tempV.z);
            //System.out.println("");
            /*System.out.println(vec.elementAt(1));
            System.out.println(tempV);
            System.out.println("The " + tri);
            System.out.println(indind);*/
            /*if(indind==118)
                System.exit(1);*/


            //System.out.print("Expect deatto: ");
            newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).z, tempV.y, tempV.z);
            //System.out.println("");
            Vec3d new3 = new Vec3d(newX, newY, 0.1f);
            vec.add(lowestOne, new3);
            vec.add(lowestOne, new2);
            Triangle triArr[] = new Triangle[2];
            triArr[0]=new Triangle(vec.elementAt(2), vec.elementAt(1), vec.elementAt(0));
            triArr[0].setColor(tri.getColor());
            triArr[1]=new Triangle(vec.elementAt(3), vec.elementAt(2), vec.elementAt(1));
            triArr[1].setColor(tri.getColor());
            return  triArr;
        }
        else if(count==2)
        {
            //System.out.println("Count is 2");
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
            float newX=zcalc(vec.elementAt(0).x, vec.elementAt(0).z, tempV.x, tempV.z);
            System.out.println("New x: " + newX);
            float newY=zcalc(vec.elementAt(0).y, vec.elementAt(0).z, tempV.y, tempV.z);
            Vec3d new2 = new Vec3d(newX, newY, 0.1f);
            newX=zcalc(vec.elementAt(1).x, vec.elementAt(1).z, tempV.x, tempV.z);
            newY=zcalc(vec.elementAt(1).y, vec.elementAt(1).z, tempV.y, tempV.z);
            Vec3d new3 = new Vec3d(newX, newY, 0.1f);
            vec.add(lowestOne, new3);
            vec.add(lowestOne, new2);

            Vector<Vec3d> vec2= new Vector<>();
            vec2.add(new2);
            vec2.add(new3);
            vec2.add(lowestOne, tempV);

            Triangle triArr[] = new Triangle[1];
            triArr[0]= new Triangle(vec2.elementAt(0), vec2.elementAt(1), vec2.elementAt(2));
            triArr[0].setColor(tri.getColor());
            return  triArr;
        }
        Triangle arr[] = new Triangle[1];
        arr[0]=tri;
        return  arr;
    }

    public static float zcalc(float xoy1, float z1, float xoy3, float z3)
    {
        if(z1==z3) {
            System.out.println("You died");

        }
        return (((xoy1-xoy3)*(0.1f-z3)/(z1-z3))+xoy3);
    }
}
