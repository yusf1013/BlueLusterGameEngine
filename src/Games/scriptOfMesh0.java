package Games;
import rendererEngine.scriptManager.Inheritable;
import threeDItems.Mesh; 
import rendererEngine.scriptManager.Control;
import java.util.*; 

public class scriptOfMesh0 extends Inheritable {

	boolean isBulletActive=false;

	@Override
	public void run(Map<Integer, Mesh> meshMap) {
		Mesh thisMesh=meshMap.get(0);
		Mesh bullet = meshMap.get(1);
		if(!isBulletActive)
		{
			System.out.println("YOO");
			bullet.teleportTo(thisMesh.position.x+0.025f, thisMesh.position.y+1.4f, thisMesh.position.z);
			bullet.setyTheta(thisMesh.getyTheta());
		}
		else
			bullet.move((float)Math.sin(thisMesh.getyTheta())/10, 0, (float)Math.cos(thisMesh.getyTheta())/10);
		//write your script here. Some sample lines are written.
		//thisMesh.move(0.1f, 0.1f, 0.1f);
		//thisMesh.teleportTo(1,1,1);
		//thisMesh.rotate(0.1f, 0.1f, 0.1f);
		//thisMesh.scale(0.1f, 0.1f, 0.1f);
		//thisMesh.growInSize(0.1f, 0.1f, 0.1f);
		//Control.bindCamera(thisMesh);
		Control.walkControlScheme(thisMesh, 0.1f, 0.1f);
		//Control.setColor(thisMesh, 1.0f, 0f, 0f);
		if(Control.isKeyPressed('q') )
		{
			isBulletActive=true;
		}
		if(bullet.collidedWith!=null)
		{
			isBulletActive=false;
			bullet.collidedWith=null;
		}

	}
}