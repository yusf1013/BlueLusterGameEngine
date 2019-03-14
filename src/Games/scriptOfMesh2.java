package Games;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh; 
import java.util.*; 
public class scriptOfMesh2 extends Inheritable {
	float a =0;
	@Override
	public void run(Map<Integer, Mesh> meshMap) {
		a+=0.05f;
		meshMap.get(2).yTranslation=(float)Math.sin(a);
	}
}