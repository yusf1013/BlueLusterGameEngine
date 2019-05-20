package rendererEngine.scriptManager;
import rendererEngine.itemBag.ItemBag;
import threeDItems.Mesh;

import java.util.Map;
public class MasterScript extends Object{

	public void run() {
		for(Map.Entry e:ItemBag.getEntrySet())
		{
			Mesh temp=(Mesh)e.getValue();
			if(temp.isScripted)
			{
				ItemBag.getScript(temp.id).run(ItemBag.getMeshMap());
			}
		}
		//ystem.out.println("fixie");
	}

}