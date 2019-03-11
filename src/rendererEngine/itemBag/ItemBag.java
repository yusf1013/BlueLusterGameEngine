package rendererEngine.itemBag;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class ItemBag {
    private static Integer currentID=0;
    private static Map <Integer, Mesh> meshCollection = new TreeMap<>();
    private static Map <Integer, Inheritable> scriptCollection = new TreeMap<>();

    public static void addMesh(Mesh m)
    {
        meshCollection.put(currentID, m);
        currentID++;
    }

    public static Map<Integer, Mesh> getMeshMap()
    {
        return meshCollection;
    }

    public static void addMesh(Vector<Mesh> vec)
    {
        for(Mesh m: vec)
        {
            meshCollection.put(m.id, m);
        }
    }

    public static void addScript(int id, Inheritable inheritable)
    {
        //inheritable.addAllMeshes(ItemBag.);
        scriptCollection.put(id, inheritable);
    }

    public static Inheritable getScript(int id)
    {
        return scriptCollection.get(id);
    }

    public static Set<Map.Entry<Integer, Mesh>> getEntrySet()
    {
        return meshCollection.entrySet();
    }

    public static Mesh getMesh(int id)
    {
        return meshCollection.get(id);
    }
}
