package Games;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh; 
import rendererEngine.scriptManager.Control;
import java.util.*; 
import threeDItems.Vec3d;
import rendererEngine.itemBag.ItemBag;
public class scriptOfMesh3 extends Inheritable {
	//Mesh mesh = getMesh(0);
	int i=0;
	@Override
	public void run(Map<Integer, Mesh> meshMap) {
		System.out.println(++i);
		Mesh thisMesh=meshMap.get(3);
		//thisMesh.setzTheta(thisMesh.getzTheta()-0.01f);
		thisMesh.setxTranslation(thisMesh.getxTranslation()+0.02f);
		//Control.bindCamera(thisMesh);
	}
}