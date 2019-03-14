package Games;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh; 
import java.util.*; 
public class scriptOfMesh5 extends Inheritable {
	//Mesh mesh = getMesh(0);
	float a =0;
	@Override
	public void run(Map<Integer, Mesh> meshMap) {
		//System.out.println("YO");
		meshMap.get(0).xTheta=(float)Math.sin(a);
	}
}