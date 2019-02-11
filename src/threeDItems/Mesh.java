package threeDItems;

import java.util.Vector;

public class Mesh {
    public Vector<Triangle> tris= new Vector<Triangle>();
    //Vector<Vec3d> point = new Vector<Vec3d>();
    //Vector<Vec3d> normal = new Vector<Vec3d>();

/*    public Mesh(Vector<Triangle> tris) {
        this.tris = tris;
    }*/
    public void addTri(Triangle t)
    {
        tris.add(t);
    }

    @Override
    public String toString() {
        String ts = "Mesh-\n";
        for(int i=0; i<tris.size(); i++)
        {
            ts+= tris.elementAt(i).toString();
        }
        return  ts;
    }

    /*    public void addVertex(Vec3d vec)
    {
        point.add(vec);
    }*/

/*    public void addNormal(Vec3d vec)
    {
        normal.add(vec);
    }*/
}
