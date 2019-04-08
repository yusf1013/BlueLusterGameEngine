package Games;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh; 
import java.util.*; 
public class scriptOfMesh1 extends Inheritable {
	//Mesh mesh = getMesh(0);
	@Override
	public void run(Map<Integer, Mesh> meshMap) {
		//System.out.println("YO");
		meshMap.get(1).xTheta+=0.01f;
		//meshMap.get(1).yTranslation+=0.01f;
		//meshMap.get(1).zScale+=0.01f;
	}
}