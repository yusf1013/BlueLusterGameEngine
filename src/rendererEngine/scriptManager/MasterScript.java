package rendererEngine.scriptManager;
import rendererEngine.DisplayDriverGL;
import rendererEngine.itemBag.ItemBag;
import threeDItems.Mesh;

import java.util.Map;
public class MasterScript{

	DisplayDriverGL ddgl;
	//Control bh = new Control(ddgl);
	public MasterScript(DisplayDriverGL gl)
	{
		ddgl=gl;
	}

	public void run() {
		for(Map.Entry e:ItemBag.getEntrySet())
		{
			Mesh temp=(Mesh)e.getValue();
			if(temp.isScripted)
			{
				ItemBag.getScript(temp.id).run(ItemBag.getMeshMap());
			}
		}
		if(!ItemBag.isCameraBound)
		{
			ddgl.cameraControls(0.02f);
		}
		else
		{
			Control.boundedCameraControls();
		}
	}

}