package mathHandler;

import threeDItems.Matrix4by4;
import threeDItems.Vec3d;

public class VectorGeometry {
    public Vec3d vectorAdd(Vec3d v1, Vec3d v2)
    {
        return new Vec3d( v1.x + v2.x, v1.y + v2.y, v1.z + v2.z );
    }

    public Vec3d vectorSub(Vec3d v1, Vec3d v2)
    {
        return new Vec3d( v1.x - v2.x, v1.y - v2.y, v1.z - v2.z );
    }

    public Vec3d vectorMul(Vec3d v1, float f)
    {
        return new Vec3d( v1.x * f, v1.y * f, v1.z * f );
    }
    public Vec3d vectorDiv(Vec3d v1, float f)
    {
        return new Vec3d( v1.x / f, v1.y / f, v1.z / f );
    }

    public float dotProduct(Vec3d v1, Vec3d v2)
    {
        return v1.x*v2.x + v1.y*v2.y + v1.z * v2.z;
    }

    float lengthOfVectorength(Vec3d v)
    {
        return (float)Math.sqrt((double)dotProduct(v, v));
    }

    public Vec3d normaliseVector(Vec3d v)
    {
        float l = lengthOfVectorength(v);
        return new Vec3d( v.x / l, v.y / l, v.z / l );
    }

    public Matrix4by4 pointAtMatrix(Vec3d position, Vec3d targetVector, Vec3d upDirection)
    {
        Vec3d forwardDirectionVector = vectorSub(targetVector, position);
        forwardDirectionVector = normaliseVector(forwardDirectionVector);

        Vec3d temp = vectorMul(forwardDirectionVector, dotProduct(upDirection, forwardDirectionVector));
        Vec3d newUpDirectionVector = vectorSub(upDirection, temp);
        newUpDirectionVector = normaliseVector(newUpDirectionVector);

        Vec3d newRight = crossProduct(newUpDirectionVector, forwardDirectionVector);

        Matrix4by4 matrix= new Matrix4by4();
        matrix.m[0][0] = newRight.x;
        matrix.m[1][0] = newUpDirectionVector.x;
        matrix.m[2][0] = forwardDirectionVector.x;
        matrix.m[3][0] = position.x;

        matrix.m[0][1] = newRight.y;
        matrix.m[1][1] = newUpDirectionVector.y;
        matrix.m[2][1] = forwardDirectionVector.y;
        matrix.m[3][1] = position.y;

        matrix.m[0][2] = newRight.z;
        matrix.m[1][2] = newUpDirectionVector.z;
        matrix.m[2][2] = forwardDirectionVector.z;
        matrix.m[3][2] = position.z;

        matrix.m[0][3] = 0.0f;
        matrix.m[1][3] = 0.0f;
        matrix.m[2][3] = 0.0f;
        matrix.m[3][3] = 1.0f;

        return matrix;

    }

    public Vec3d crossProduct(Vec3d v1, Vec3d v2)
    {
        Vec3d v = new Vec3d();
        v.x = v1.y * v2.z - v1.z * v2.y;
        v.y = v1.z * v2.x - v1.x * v2.z;
        v.z = v1.x * v2.y - v1.y * v2.x;
        return v;
    }

    public static Vec3d multiplyMatrixAndVector(Matrix4by4 m, Vec3d i)
    {
        Vec3d v = new Vec3d();
        v.x = i.x * m.m[0][0] + i.y * m.m[1][0] + i.z * m.m[2][0] + i.w * m.m[3][0];
        v.y = i.x * m.m[0][1] + i.y * m.m[1][1] + i.z * m.m[2][1] + i.w * m.m[3][1];
        v.z = i.x * m.m[0][2] + i.y * m.m[1][2] + i.z * m.m[2][2] + i.w * m.m[3][2];
        v.w = i.x * m.m[0][3] + i.y * m.m[1][3] + i.z * m.m[2][3] + i.w * m.m[3][3];
        return v;
    }

    public static Matrix4by4 makeIdentity()
    {
        Matrix4by4 matrix= new Matrix4by4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public static Matrix4by4 scale(float x, float y, float z)
    {
        Matrix4by4 mat = new Matrix4by4();
        mat.m[0][0]=x;
        mat.m[1][1]=y;
        mat.m[2][2]=z;
        mat.m[3][3]=1.0f;
        return mat;
    }

    public static Matrix4by4 makeXRotationMatrix(float angle)
    {
        Matrix4by4 matrix = new Matrix4by4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = (float)Math.cos(angle);
        matrix.m[2][1] = -(float)Math.sin(angle);
        matrix.m[1][2] = (float)Math.sin(angle);
        matrix.m[2][2] = (float)Math.cos(angle);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public static Matrix4by4 makeYRotationMatrix(float angle)
    {
        float fAngleRad = -angle;
        Matrix4by4 matrix = new Matrix4by4();
        matrix.m[0][0] = (float)Math.cos(fAngleRad);
        matrix.m[2][0] = -(float)Math.sin(fAngleRad);
        matrix.m[1][1] = 1.0f;
        matrix.m[0][2] = (float)Math.sin(fAngleRad);
        matrix.m[2][2] = (float)Math.cos(fAngleRad);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public static Matrix4by4 makeZRotationMatrix(float angle)
    {
        Matrix4by4 matrix = new Matrix4by4();
        matrix.m[0][0] = (float)Math.cos(angle);
        matrix.m[1][0] = -(float)Math.sin(angle);
        matrix.m[0][1] = (float)Math.sin(angle);
        matrix.m[1][1] = (float)Math.cos(angle);
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public static Matrix4by4 makeTranslation(float x, float y, float z)
    {
        Matrix4by4 matrix = new Matrix4by4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        matrix.m[3][0] = x;
        matrix.m[3][1] = y;
        matrix.m[3][2] = z;
        return matrix;
    }

    public Matrix4by4 makeProjectionMatrix(float fOVInDegrees, float aspectRatio, float fNear, float fFar)
    {
        float fFovRad = 1.0f / (float)Math.tan(fOVInDegrees * 0.5f / 180.0f * 3.14159f);
        Matrix4by4 matrix= new Matrix4by4();
        matrix.m[0][0] = aspectRatio * fFovRad;
        matrix.m[1][1] = fFovRad;
        matrix.m[2][2] = fFar / (fFar - fNear);
        matrix.m[3][2] = (-fFar * fNear) / (fFar - fNear);
        matrix.m[2][3] = 1.0f;
        matrix.m[3][3] = 0.0f;
        return matrix;
    }

    public static Matrix4by4 multiplyMatrix(Matrix4by4 m1, Matrix4by4 m2)
    {
        Matrix4by4 matrix = new Matrix4by4();
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                matrix.m[r][c] = m1.m[r][0] * m2.m[0][c] + m1.m[r][1] * m2.m[1][c] + m1.m[r][2] * m2.m[2][c] + m1.m[r][3] * m2.m[3][c];
        return matrix;
    }

    public Matrix4by4 quickInverse(Matrix4by4 m)
    {
        Matrix4by4 matrix = new Matrix4by4();
        matrix.m[0][0] = m.m[0][0];
        matrix.m[1][0] = m.m[0][1];
        matrix.m[2][0] = m.m[0][2];
        matrix.m[3][0] = -(m.m[3][0] * matrix.m[0][0] + m.m[3][1] * matrix.m[1][0] + m.m[3][2] * matrix.m[2][0]);

        matrix.m[0][1] = m.m[1][0];
        matrix.m[1][1] = m.m[1][1];
        matrix.m[2][1] = m.m[1][2];
        matrix.m[3][1] = -(m.m[3][0] * matrix.m[0][1] + m.m[3][1] * matrix.m[1][1] + m.m[3][2] * matrix.m[2][1]);

        matrix.m[0][2] = m.m[2][0];
        matrix.m[1][2] = m.m[2][1];
        matrix.m[2][2] = m.m[2][2];
        matrix.m[3][2] = -(m.m[3][0] * matrix.m[0][2] + m.m[3][1] * matrix.m[1][2] + m.m[3][2] * matrix.m[2][2]);

        matrix.m[0][3] = 0.0f;
        matrix.m[1][3] = 0.0f;
        matrix.m[2][3] = 0.0f;
        matrix.m[3][3] = 1.0f;

        return matrix;
    }

    public static Matrix4by4 makeRotation(float x, float y, float z)
    {
        Matrix4by4 temp=multiplyMatrix(makeZRotationMatrix(z), makeXRotationMatrix(x));
        //ystem.out.println("fixie");
        return multiplyMatrix(temp , makeYRotationMatrix(y));

    }
    public void useless()
    {
        int i=0;
        i++;
    }

}
