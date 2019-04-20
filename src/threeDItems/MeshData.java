package threeDItems;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class MeshData {
    private static Map<String, ArrayList<Triangle>> map = new TreeMap<>();

    public static void insertDataArray(String name, ArrayList<Triangle> arrayList)
    {
        map.put(name, arrayList);
    }

    public static ArrayList<Triangle> getDataPointReference(String name)
    {
        return map.get(name);
    }

}
