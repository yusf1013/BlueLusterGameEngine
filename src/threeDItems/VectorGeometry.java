package threeDItems;

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

    public Mat4x4 pointAtMatrix(Vec3d pos, Vec3d target, Vec3d up)
    {
        // Calculate new forward direction
        Vec3d newForward = vectorSub(target, pos);
        newForward = normaliseVector(newForward);

        // Calculate new Up direction
        Vec3d a = vectorMul(newForward, dotProduct(up, newForward));
        Vec3d newUp = vectorSub(up, a);
        newUp = normaliseVector(newUp);

        // New Right direction is easy, its just cross product
        Vec3d newRight = crossProduct(newUp, newForward);

        // Construct Dimensioning and Translation Matrix
        Mat4x4 matrix= new Mat4x4();
        matrix.m[0][0] = newRight.x;	matrix.m[0][1] = newRight.y;	matrix.m[0][2] = newRight.z;	matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = newUp.x;		matrix.m[1][1] = newUp.y;		matrix.m[1][2] = newUp.z;		matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = newForward.x;	matrix.m[2][1] = newForward.y;	matrix.m[2][2] = newForward.z;	matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = pos.x;			matrix.m[3][1] = pos.y;			matrix.m[3][2] = pos.z;			matrix.m[3][3] = 1.0f;
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

    Vec3d Vector_IntersectPlane(Vec3d plane_p, Vec3d plane_n, Vec3d lineStart, Vec3d lineEnd)
    {
        plane_n = normaliseVector(plane_n);
        float plane_d = -dotProduct(plane_n, plane_p);
        float ad = dotProduct(lineStart, plane_n);
        float bd =dotProduct(lineEnd, plane_n);
        float t = (-plane_d - ad) / (bd - ad);
        Vec3d lineStartToEnd = vectorSub(lineEnd, lineStart);
        Vec3d lineToIntersect = vectorMul(lineStartToEnd, t);
        return vectorAdd(lineStart, lineToIntersect);
    }

    public Vec3d multiplyVector(Mat4x4 m, Vec3d i)
    {
        Vec3d v = new Vec3d();
        v.x = i.x * m.m[0][0] + i.y * m.m[1][0] + i.z * m.m[2][0] + i.w * m.m[3][0];
        v.y = i.x * m.m[0][1] + i.y * m.m[1][1] + i.z * m.m[2][1] + i.w * m.m[3][1];
        v.z = i.x * m.m[0][2] + i.y * m.m[1][2] + i.z * m.m[2][2] + i.w * m.m[3][2];
        v.w = i.x * m.m[0][3] + i.y * m.m[1][3] + i.z * m.m[2][3] + i.w * m.m[3][3];
        return v;
    }

    public static Mat4x4  makeIdentity()
    {
        Mat4x4 matrix= new Mat4x4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public Mat4x4 makeXRotationMatrix(float fAngleRad)
    {
        Mat4x4 matrix = new Mat4x4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = (float)Math.cos(fAngleRad);
        matrix.m[1][2] = (float)Math.sin(fAngleRad);
        matrix.m[2][1] = -(float)Math.sin(fAngleRad);
        matrix.m[2][2] = (float)Math.cos(fAngleRad);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public Mat4x4 makeYRotationMatrix(float fAngle)
    {
        float fAngleRad = -fAngle;
        Mat4x4 matrix = new Mat4x4();
        matrix.m[0][0] = (float)Math.cos(fAngleRad);
        matrix.m[0][2] = (float)Math.sin(fAngleRad);
        matrix.m[2][0] = -(float)Math.sin(fAngleRad);
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = (float)Math.cos(fAngleRad);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public Mat4x4 makeZRotationMatrix(float fAngleRad)
    {
        Mat4x4 matrix = new Mat4x4();
        matrix.m[0][0] = (float)Math.cos(fAngleRad);
        matrix.m[0][1] = (float)Math.sin(fAngleRad);
        matrix.m[1][0] = -(float)Math.sin(fAngleRad);
        matrix.m[1][1] = (float)Math.cos(fAngleRad);
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        return matrix;
    }

    public Mat4x4 makeTranslation(float x, float y, float z)
    {
        Mat4x4 matrix = new Mat4x4();
        matrix.m[0][0] = 1.0f;
        matrix.m[1][1] = 1.0f;
        matrix.m[2][2] = 1.0f;
        matrix.m[3][3] = 1.0f;
        matrix.m[3][0] = x;
        matrix.m[3][1] = y;
        matrix.m[3][2] = z;
        return matrix;
    }

    public Mat4x4 makeProjectionMatrix(float fFovDegrees, float fAspectRatio, float fNear, float fFar)
    {
        float fFovRad = 1.0f / (float)Math.tan(fFovDegrees * 0.5f / 180.0f * 3.14159f);
        Mat4x4 matrix= new Mat4x4();
        matrix.m[0][0] = fAspectRatio * fFovRad;
        matrix.m[1][1] = fFovRad;
        matrix.m[2][2] = fFar / (fFar - fNear);
        matrix.m[3][2] = (-fFar * fNear) / (fFar - fNear);
        matrix.m[2][3] = 1.0f;
        matrix.m[3][3] = 0.0f;
        return matrix;
    }

    public Mat4x4 multiplyMatrix(Mat4x4 m1, Mat4x4 m2)
    {
        Mat4x4 matrix = new Mat4x4();
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                matrix.m[r][c] = m1.m[r][0] * m2.m[0][c] + m1.m[r][1] * m2.m[1][c] + m1.m[r][2] * m2.m[2][c] + m1.m[r][3] * m2.m[3][c];
        return matrix;
    }

    public Mat4x4 quickInverse(Mat4x4 m) // Only for Rotation/Translation Matrices
    {
        Mat4x4 matrix = new Mat4x4();
        matrix.m[0][0] = m.m[0][0]; matrix.m[0][1] = m.m[1][0]; matrix.m[0][2] = m.m[2][0]; matrix.m[0][3] = 0.0f;
        matrix.m[1][0] = m.m[0][1]; matrix.m[1][1] = m.m[1][1]; matrix.m[1][2] = m.m[2][1]; matrix.m[1][3] = 0.0f;
        matrix.m[2][0] = m.m[0][2]; matrix.m[2][1] = m.m[1][2]; matrix.m[2][2] = m.m[2][2]; matrix.m[2][3] = 0.0f;
        matrix.m[3][0] = -(m.m[3][0] * matrix.m[0][0] + m.m[3][1] * matrix.m[1][0] + m.m[3][2] * matrix.m[2][0]);
        matrix.m[3][1] = -(m.m[3][0] * matrix.m[0][1] + m.m[3][1] * matrix.m[1][1] + m.m[3][2] * matrix.m[2][1]);
        matrix.m[3][2] = -(m.m[3][0] * matrix.m[0][2] + m.m[3][1] * matrix.m[1][2] + m.m[3][2] * matrix.m[2][2]);
        matrix.m[3][3] = 1.0f;
        return matrix;
    }
}
