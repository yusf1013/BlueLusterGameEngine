package mathHandler;

import threeDItems.Matrix4by4;
import threeDItems.Mesh;
import threeDItems.Triangle;

public class ThreeDObjectTransformations extends VectorGeometry{
    public Triangle transform(Matrix4by4 transformationMatrix, Triangle triTransformed)
    {
        Triangle nTri = new Triangle();
        nTri.p[0]=multiplyMatrixAndVector(transformationMatrix, triTransformed.p[0]);
        nTri.p[1]=multiplyMatrixAndVector(transformationMatrix, triTransformed.p[1]);
        nTri.p[2]=multiplyMatrixAndVector(transformationMatrix, triTransformed.p[2]);
        nTri.setColor(triTransformed.getColor());
        return nTri;
    }

    public Triangle scaleTriangle(Triangle triProjected, float w)
    {
        triProjected.p[0] = vectorMul(triProjected.p[0], w);
        triProjected.p[1] = vectorMul(triProjected.p[1], w);
        triProjected.p[2] = vectorMul(triProjected.p[2], w);
        //System.out.println("fixie");
        return triProjected;
    }

    public void scaleMesh(Mesh mesh, float w)
    {
        for(Triangle tri: mesh.tris)
        {
            scaleTriangle(tri, w);
        }
    }

    public void useless()
    {
        int i=0;
        i++;
    }


}
