package threeDItems;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class Triangle implements Serializable {
    public Vec3d p[] = new Vec3d[3];
    //public Color color;
    double red, green, blue, opacity;

    public Triangle(Vec3d[] p) {
        this.p = p;
    }

    public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3)
    {
        p[0]=new Vec3d(x1, y1, z1);
        p[1]=new Vec3d(x2, y2, z2);
        p[2]=new Vec3d(x3, y3, z3);
        //this.color = Color.GREY;
        red=Color.GREY.getRed();
        green=Color.GREY.getGreen();
        blue=Color.GREY.getBlue();
        opacity=Color.GREY.getOpacity();
    }

    public Triangle(Vec3d p1, Vec3d p2, Vec3d p3)
    {
        p[0]= p1;
        p[1]= p2;
        p[2]= p3;
        //this.color = Color.GREY;
        red=Color.GREY.getRed();
        green=Color.GREY.getGreen();
        blue=Color.GREY.getBlue();
        opacity=Color.GREY.getOpacity();
    }

    public Triangle(Vec3d p1, Vec3d p2, Vec3d p3, Color color)
    {
        p[0]= p1;
        p[1]= p2;
        p[2]= p3;
        //this.color = color;
        red=color.getRed();
        green=color.getGreen();
        blue=color.getBlue();
        opacity=color.getOpacity();
    }

    public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, Color color)
    {
        p[0]=new Vec3d(x1, y1, z1);
        p[1]=new Vec3d(x2, y2, z2);
        p[2]=new Vec3d(x3, y3, z3);
        //this.color = color;
        red=color.getRed();
        green=color.getGreen();
        blue=color.getBlue();
        opacity=color.getOpacity();
    }

    public Triangle()
    {
        this(0,0,0,0,0,0,0,0,0);
    }

    public Color getColor() {
        return new Color(red, green, blue, opacity);
    }

    public void setColor(Color color) {
        //this.color = color;
        red=color.getRed();
        green=color.getGreen();
        blue=color.getBlue();
        opacity=color.getOpacity();
        //System.out.println("fixie");
    }

    public float getMidPointz()
    {
        return (p[0].z+p[1].z+p[2].z)/3;
    }

    public float getFunPointz()
    {
        float f;
        if(p[0].z>p[1].z) f=p[0].z;
        else f=p[1].z;
        if(p[2].z>f)
            f=p[2].z;
        return f;
    }

    @Override
    public String toString() {
        String ts = "Triangle: " + p[0].x + "," + p[0].y + "," + p[0].z + "\n"
                    + p[1].x + "," + p[1].y + "," + p[1].z + "\n"
                    + p[2].x + "," + p[2].y + "," + p[2].z + "\n";
        return ts;
    }
}
