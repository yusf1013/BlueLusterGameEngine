package threeDItems;

import java.io.Serializable;

public class Vec3d implements Serializable {
    public float x,y,z;
    public float w=1.0f;

    /*public Vec3d(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }*/

    public Vec3d(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1.0f;
        //System.out.println("fixie");
    }

    public Vec3d()
    {
        this(0,0,0);
    }

    public float distance(Vec3d anotherPoint)
    {
        return (float)Math.sqrt(Math.pow(anotherPoint.x-this.x, 2) + Math.pow(anotherPoint.y-this.y, 2) + Math.pow(anotherPoint.z-this.z, 2));
    }

    @Override
    public String toString() {
        return "Vec3d: " + x + "," + y + "," + z;
    }

}
