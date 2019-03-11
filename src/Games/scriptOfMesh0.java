package Games;
import rendererEngine.MasterScript;
import rendererEngine.Inheritable;
import threeDItems.Mesh; 
import java.util.Vector; 
public class scriptOfMesh0 extends Inheritable {
//Mesh thisMesh = MasterScript.getMesh(0);
	MasterScript ms;
	int a=0;
	public void create(MasterScript mas)
	{
		ms=mas;
	}
	public String run(rendererEngine.Camera cam)
	{
		System.out.println("yo");
//		System.out.println(vec.get(0).getClass().getName());
		//thisMesh.xTheta+=0.01f;
		//thisMesh.yTheta+=0.01f;
		//thisMesh.xTranslation+=0.01f;
		//thisMesh.yTranslation+=0.01f;
		//thisMesh.zTranslation+=0.01f;
		return "";
	}
	public boolean equalstozie()
	{
		a++;
		System.out.println("In = " + a);
//		System.out.println("The funniest part: " + ((MasterScript) o).test);
		return true;
	}
}
