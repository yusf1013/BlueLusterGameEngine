/*
package rendererEngine;
import threeDItems.Mesh;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import dataHandler.*;
public class MasterScript extends Object{

	//Object i;
	Vector <Object> vecOfObj = new Vector<>();
	Vector<Integer> hash = new Vector<>();
	public int test=5;
	Vector<Mesh> meshVec;

	public MasterScript (Vector<Mesh> meshVec)
	{
		this.meshVec=meshVec;
		try {
			for(int i=0; i<=meshVec.lastElement().id; i++)
				hash.add(-1);
			System.out.println(hash.size());
			int i=0;
			for(Mesh m: meshVec)
			{
                System.out.println("In loop of MS. Size is: " + meshVec.size());
				System.out.println("The id is: " + m.id);
			    if(m.isScripted)
				{
					vecOfObj.add( new scriptClassLoader().loadClass("Games.scriptOfMesh"+m.id).newInstance());
					//System.out.println((((Inheritable)new scriptClassLoader().loadClass("Games.scriptOfMesh"+m.id).newInstance()).i));
					*/
/*if(new scriptClassLoader().loadClass("Games.scriptOfMesh"+m.id).newInstance() instanceof Inheritable)
						System.out.println("It is instance of");
					else
						System.out.println("No it is not");

				}
			    else
                    System.out.println("Not scripted " + m.id);
			    hash.set(m.id, i);
			    i++;
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		for(Object o:vecOfObj)
		{
			System.out.println("Ultimate sout: " + o.getClass().getClassLoader());
			Method m2[] = o.getClass().getDeclaredMethods();
			Class<?> cl[]=null;
			for(Method method:m2)
			{
				System.out.println(method.getName());
				if(method.getName().equals("run"))
					cl=method.getParameterTypes();
			}


			try {
				for(Class<?> c: cl)
				{
					System.out.println("Class loader: " + Mesh.class.getClassLoader().getName());
					System.out.println("Class loader: " + c.getClassLoader().getName());
				}

				Method m = o.getClass().getDeclaredMethod("run", Camera.class);
				m.invoke(o, new Camera());



			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void getMesh(int id)
	{
		System.out.println("Trying to get: " + hash.get(id));
		//return meshVector.get(hash.get(id));
	}

}*/

package rendererEngine;
import threeDItems.Mesh;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;
import dataHandler.*;
public class MasterScript extends Object{

	//Object i;
	Vector <Object> vecOfObj = new Vector<>();
	Vector<Integer> hash = new Vector<>();
	public int test=5;
	Vector<Mesh> meshVec;

	public MasterScript (Vector<Mesh> meshVec)
	{
		this.meshVec=meshVec;
		try {
			for(int i=0; i<=meshVec.lastElement().id; i++)
				hash.add(-1);
			System.out.println(hash.size());
			int i=0;
			for(Mesh m: meshVec)
			{
				System.out.println("In loop of MS. Size is: " + meshVec.size());
				System.out.println("The id is: " + m.id);
				if(m.isScripted)
				{
					vecOfObj.add( new scriptClassLoader().loadClass("Games.scriptOfMesh"+m.id).newInstance());
					//System.out.println((((Inheritable)new scriptClassLoader().loadClass("Games.scriptOfMesh"+m.id).newInstance()).i));
					/*if(new scriptClassLoader().loadClass("Games.scriptOfMesh"+m.id).newInstance() instanceof Inheritable)
						System.out.println("It is instance of");
					else
						System.out.println("No it is not");*/
				}
				else
					System.out.println("Not scripted " + m.id);
				hash.set(m.id, i);
				i++;
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		for(Object o:vecOfObj)
		{
			System.out.println("Ultimate sout: " + o.getClass().getClassLoader());
			Method m2[] = o.getClass().getDeclaredMethods();
			Class<?> cl[]=null;
			for(Method method:m2)
			{
				System.out.println(method.getName());
				if(method.getName().equals("run"))
					cl=method.getParameterTypes();
			}


			try {
				for(Class<?> c: cl)
				{
					System.out.println("Class loader: " + Mesh.class.getClassLoader().getName());
					System.out.println("Class loader: " + c.getClassLoader().getName());
				}

				Method m = o.getClass().getDeclaredMethod("run", Camera.class);
				m.invoke(o, new Camera());



			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	public void getMesh(int id)
	{
		System.out.println("Trying to get: " + hash.get(id));
		//return meshVector.get(hash.get(id));
	}

}