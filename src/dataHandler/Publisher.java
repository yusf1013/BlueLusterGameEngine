package dataHandler;

import rendererEngine.Cmd;
import threeDItems.Mesh;
import threeDItems.ObbMesh;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Publisher {

    Scanner scan = new Scanner(System.in);

    public void publish(Vector<String> fileVec, List<Mesh> meshList)
    {
        deleteAllFilesFromGames();
       System.out.println("Deleted");

        /*try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("src/Games/gameInfo.hlit"));
            out.writeObject(meshList);
            List  l = (List) new ObjectInputStream(new FileInputStream("src/Games/gameInfo.hlit")).readObject();
           System.out.println("List loaded");
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/

        if(fileVec.size()==0)
            return;

        for(int i=0; i<fileVec.size(); i++)
        {
            File source = new File("src\\resources\\"+fileVec.elementAt(i));
            File dest = new File("src\\Games\\"+fileVec.elementAt(i));
            if(dest.exists())
            {
               System.out.println("It exists");
            }
            else
                copyFile(source, dest);
        }


            /*if(testFile.exists())
            {
               System.out.println("2nd shit maybe?");
                cmd.runCommand("pushd src\\Games &&" +
                        "move *.class ..\\..\\out\\production\\olcge\\Games" +
                        "&& exit");
            }
            else
            {
               System.out.println("Not 2nd shit maybe?");
                cmd.runCommand("pushd src\\Games &&" +
                        "move *.class ..\\..\\Games" +
                        "&& exit");
            }*/

        File info = new File("src\\Games\\info.hma");
        try {
            //Writer write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(info)));
            BufferedWriter write = new BufferedWriter(new FileWriter(info));
           System.out.println("Preparing to write");
            for(int i=0; i<fileVec.size(); i++)
            {
               System.out.println("Writing at : " + info.getAbsolutePath());
                //write.write("Please kill me");
               System.out.println("Please kill me");
                write.write(fileVec.elementAt(i)+ " ");
                write.write(meshList.get(i).xTheta+" "+ meshList.get(i).yTheta + " " + meshList.get(i).zTheta + " ");
                write.write(meshList.get(i).xTranslation+" "+ meshList.get(i).yTranslation + " " + meshList.get(i).zTranslation+ " ");
                write.write(meshList.get(i).xScale+" "+ meshList.get(i).yScale + " " + meshList.get(i).zScale + " ");
                write.write(meshList.get(i).isScripted + " " + meshList.get(i).id + " " + meshList.get(i).isRigidBody + " ");
                if(meshList.get(i).isRigidBody)
                {
                    write.write(meshList.get(i).obb.min.x + " " + meshList.get(i).obb.min.y + " " + meshList.get(i).obb.min.z + " ");
                    write.write(meshList.get(i).obb.max.x + " " + meshList.get(i).obb.max.y + " " + meshList.get(i).obb.max.z + " ");

                    write.write(meshList.get(i).obbList.size() + " ");

                    System.out.println("Faltu out: " + meshList.get(i).obbList.size());
                    for(ObbMesh m: meshList.get(i).obbList) {
                        write.write(m.xTheta+" "+ m.yTheta + " " + m.zTheta + " ");
                        write.write(m.xTranslation+" "+ m.yTranslation + " " + m.zTranslation+ " ");
                        write.write(m.xScale+" "+ m.yScale + " " + m.zScale + " ");
                        write.write(m.initTransX + " " + m.initTransY + " " + m.initTransZ + " ");
                        write.write(m.initScaleX + " " + m.initScaleY + " " + m.initScaleZ + " ");
                    }
                }
                write.write("\n");
                Scanner scan = new Scanner(System.in);
                //scan.nextLine();
            }
            write.close();
           System.out.println("writer closed");
            //System.exit(0);

           System.out.println("Worthless life");
            Cmd cmd = new Cmd();
            File testFile = new File("out\\production\\olcge\\Games");

            if(testFile.exists())
                cmd.runCommand("echo lieee && pushd src && cd && " + "javac Games\\*.java && exit");
            else
                cmd.runCommand("echo \"First thing\" && javac src\\Games\\*.java && echo \"Test File DNE\"");

            cmd.runCommand("echo \"Moving class\" && pushd src\\Games &&" +
                    "move *.class D:\\ideaIntellij\\olcge\\out\\production\\olcge\\Games && exit");
            cmd.runCommand("pushd src\\Games && copy .\\info.hma ..\\..\\olcge\\Games && exit");

            //cmd.runCommand("");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void deleteAllFilesFromGames()
    {
        File dir = new File("src\\Games");
        File arr[] = dir.listFiles();
        for(File f: arr)
        {
            if(f.getName().endsWith(".obj"))
                f.delete();
        }
        //ystem.out.println("fixie");
    }

    public File loadFile(String fileName)
    {
        return new File(fileName);
    }

    public void copyFile(File source,File dest) {

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {

                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void useless()
    {
        int i=0;
        i++;
    }


}
