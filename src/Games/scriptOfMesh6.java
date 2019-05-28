package Games;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh; 
import rendererEngine.scriptManager.Control;
import java.util.*; 

public class scriptOfMesh6 extends Inheritable {

	@Override
	public void run(Map<Integer, Mesh> meshMap) {
		Mesh thisMesh=meshMap.get(6);
		
		//write your script here. Some sample lines are written.
		//thisMesh.move(0.1f, 0.1f, 0.1f);
		//thisMesh.teleportTo(1,1,1);
		//thisMesh.rotate(0.1f, 0.1f, 0.1f);
		//thisMesh.scale(0.1f, 0.1f, 0.1f);
		//thisMesh.growInSize(0.1f, 0.1f, 0.1f);
		Control.walkControlScheme(thisMesh, 0.1f, 0.1f);
		//Control.bindCamera(thisMesh);
	}
}