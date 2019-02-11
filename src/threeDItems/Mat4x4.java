package threeDItems;

public class Mat4x4 {
    public float[][] m =new float[4][4];

    public Mat4x4() {
        for(int i=0; i<4; i++)
            for(int j=0; j<4; j++)
                m[i][j]=0;
    }

    public String toString()
    {
        String s="";
        for(int i=0; i<4; i++)
        {
            for(int j=0; j<4; j++)
                s+=m[i][j] + "\t";
            s+="\n";
        }
        return s;
    }
}
