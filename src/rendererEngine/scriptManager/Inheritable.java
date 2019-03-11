package rendererEngine.scriptManager;

import threeDItems.Mesh;
import java.util.Map;
import java.util.TreeMap;

public abstract class Inheritable {
    private Map<Integer, Mesh> meshCollection = new TreeMap<>();

    public void addAllMeshes(Map<Integer, Mesh> meshCollection)
    {
        this.meshCollection=meshCollection;
    }

    public Mesh getMesh(int id)
    {
        return meshCollection.get(id);
    }

    public abstract void run(Map<Integer, Mesh> meshMap);
}
