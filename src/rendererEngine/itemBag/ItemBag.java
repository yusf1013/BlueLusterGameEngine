package rendererEngine.itemBag;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh;
import threeDItems.Vec3d;

import java.util.*;

public class ItemBag {
    private static Integer currentID=0;
    private static Map <Integer, Mesh> meshCollection = new TreeMap<>();
    private static Map <Integer, Inheritable> scriptCollection = new TreeMap<>();
    public static boolean modified = true;

    public static void addMesh(Mesh m)
    {
        modified=true;
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

    public static void addMesh(List vec)
    {
        for(Object o: vec)
        {
            Mesh m = (Mesh) o;
            meshCollection.put(m.id, m);
            //ystem.out.println("fixie");
        }
    }

    public static void addScript(int id, Inheritable inheritable)
    {
        //inheritable.addAllMeshes(ItemBag.);
        scriptCollection.put(id, inheritable);
    }

    public static String getMapSize()
    {
        return "ItemBag map size is: " + meshCollection.size();
    }

    public static Inheritable getScript(int id)
    {
        return scriptCollection.get(id);
    }

    public static Set<Map.Entry<Integer, Mesh>> getEntrySet()
    {
        //ystem.out.println("Ghost: " + meshCollection.size());
        return meshCollection.entrySet();
    }

    public static Mesh getMesh(int id)
    {
        return meshCollection.get(id);
    }
}
