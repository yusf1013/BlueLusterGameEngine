package Games;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh; 
import java.util.*; 
public class scriptOfMesh2 extends Inheritable {
	//Mesh mesh = getMesh(0);
	int i=0;	@Override
	public void run(Map<Integer, Mesh> meshMap) {
		System.out.println(++i);
		//meshMap.get(0).setzTheta(meshMap.get(0).getzTheta()+0.01f);
		meshMap.get(0).setxTranslation(meshMap.get(0).getxTranslation()+0.01f);
	}
}