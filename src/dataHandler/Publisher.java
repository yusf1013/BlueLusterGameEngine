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
        publishNoGames(fileVec, meshList, "src\\Games\\");
        compileAndCopyClasses();
    }
    public void publishNoGames(Vector<String> fileVec, List<Mesh> meshList, String saveFolderAbsolutePath)
    {
        if(fileVec.size()==0)
            return;

        System.out.println("File name as received in publish no games: " + saveFolderAbsolutePath + ": " + new File(saveFolderAbsolutePath).exists());

        for(int i=0; i<fileVec.size(); i++)
        {
            File source = new File("src\\resources\\"+fileVec.elementAt(i));
            File dest = new File(saveFolderAbsolutePath+fileVec.elementAt(i));
            System.out.println("File name in publish no games: " + dest.getAbsolutePath());
            if(dest.exists())
            {
               System.out.println("It exists");
            }
            else
                copyFile(source, dest);
        }

        File info = new File(saveFolderAbsolutePath+ "info.hma");
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
                write.write(meshList.get(i).getxTheta() +" "+ meshList.get(i).getyTheta() + " " + meshList.get(i).getzTheta() + " ");
                write.write(meshList.get(i).getxTranslation() +" "+ meshList.get(i).getyTranslation() + " " + meshList.get(i).getzTranslation() + " ");
                write.write(meshList.get(i).getxScale() +" "+ meshList.get(i).getyScale() + " " + meshList.get(i).getzScale() + " ");
                write.write(meshList.get(i).isScripted + " " + meshList.get(i).id + " " + meshList.get(i).isRigidBody + " ");
                if(meshList.get(i).isRigidBody)
                {
                    write.write(meshList.get(i).obb.min.x + " " + meshList.get(i).obb.min.y + " " + meshList.get(i).obb.min.z + " ");
                    write.write(meshList.get(i).obb.max.x + " " + meshList.get(i).obb.max.y + " " + meshList.get(i).obb.max.z + " ");

                    write.write(meshList.get(i).obbList.size() + " ");

                    System.out.println("Faltu out: " + meshList.get(i).obbList.size());
                    for(ObbMesh m: meshList.get(i).obbList) {
                        write.write(m.getxTheta() +" "+ m.getyTheta() + " " + m.getzTheta() + " ");
                        write.write(m.getxTranslation() +" "+ m.getyTranslation() + " " + m.getzTranslation() + " ");
                        write.write(m.getxScale() +" "+ m.getyScale() + " " + m.getzScale() + " ");
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



            //cmd.runCommand("");


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compileAndCopyClasses()
    {
        System.out.println("Worthless life");
        Cmd cmd = new Cmd();
        File testFile = new File("out\\production\\olcge\\Games");

        if(testFile.exists())
            cmd.runCommand("echo CompilingScripts && pushd src && cd && " + "javac Games\\*.java && exit");
        else
            cmd.runCommand("echo \"First thing\" && javac Games\\*.java && echo \"Test File DNE\"");

        cmd.runCommand("echo \"Moving class\" && pushd src\\Games &&" +
                "move *.class D:\\ideaIntellij\\olcge\\out\\production\\olcge\\Games && exit");
        cmd.runCommand("pushd src\\Games && copy .\\*.* ..\\..\\olcge\\Games && exit");
    }

    public void deleteAllFilesFromGames()
    {
        File dir = new File("src\\Games");
        File arr[] = dir.listFiles();
        if(arr==null)
            return;
        for(File f: arr)
        {
            if(f.getName().endsWith(".obj"))
                f.delete();
        }
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

}
