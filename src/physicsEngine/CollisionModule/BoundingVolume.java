package physicsEngine.CollisionModule;

import threeDItems.Mesh;

public abstract class BoundingVolume {
    public abstract void collidesWith(BoundingVolume bv);
    public abstract Mesh getMesh(Mesh mesh);
}
