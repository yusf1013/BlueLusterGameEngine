package dataHandler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class scriptClassLoader extends ClassLoader {
    /*public static void main(String[] args) throws Exception {
        do {
            Object foo = new scriptClassLoader().loadClass("MyFoo").newInstance();
           System.out.println("LOADED: " + foo); // Overload MyFoo#toString() for effect
           System.out.println("Press <ENTER> when MyFoo.class has changed");
            System.in.read();
        } while (true);
    }*/

    @Override
    public Class<?> loadClass(String s) {
        return findClass(s);
    }

    @Override
    public Class<?> findClass(String s) {
        try {
            byte[] bytes = loadClassData(s);
            return defineClass(s, bytes, 0, bytes.length);
        } catch (IOException ioe) {
           System.out.println("In first catch");
            try {
                return super.loadClass(s);
            } catch (ClassNotFoundException ignore) {System.out.println("Doing sth veryyyy wrong"); }
            ioe.printStackTrace(System.out);
           System.out.println("Doing sth very wrong");
            return null;
        }
    }

    private byte[] loadClassData(String className) throws IOException {
       System.out.println("We are here with " + className);
        File f = new File("D:\\ideaIntellij\\olcge\\out\\production\\olcge\\" + className.replaceAll("\\.", "/") + ".class");
       System.out.println("The name is: " + f.getName());
        if(f.exists())
           System.out.println("File exists");
        else
           System.out.println("File doesn't exist");
       System.out.println(f.getAbsolutePath());
        int size = (int) f.length();
        byte buff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        dis.readFully(buff);
        dis.close();
        //ystem.out.println("fixie");
        return buff;
    }

    public void useless()
    {
        int i=0;
        i++;
    }
}