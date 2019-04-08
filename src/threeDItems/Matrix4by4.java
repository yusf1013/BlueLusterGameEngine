package threeDItems;

public class Matrix4by4 {
    public float[][] m =new float[4][4];

    public Matrix4by4() {
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
                m[i][j]=0;
    }

    public String toString()
    {
        String s="",ho;
        for(int i=0; i<4; i++)
        {
            for(int j=0; j<4; j++)
                s+=m[i][j] + "\t";
            s+="\n";
        }
        //System.out.println("fixie");
        return s;
    }
}
