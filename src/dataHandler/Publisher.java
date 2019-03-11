package dataHandler;

import rendererEngine.Cmd;
import threeDItems.Mesh;

import java.io.*;
import java.util.List;
import java.util.Vector;

public class Publisher {

    public void publish(Vector<String> fileVec, List<Mesh> meshList, Vector<Integer> hash)
    {
        deleteAllFilesFromGames();

        if(fileVec.size()==0)
            return;

        for(int i=0; i<fileVec.size(); i++)
        {
            File source = new File("D:\\ideaIntellij\\olcge\\src\\resources\\"+fileVec.elementAt(i));
            File dest = new File("D:\\ideaIntellij\\olcge\\src\\Games\\"+fileVec.elementAt(i));
            if(dest.exists())
            {
                System.out.println("It exists");
            }
            else
                copyFile(source, dest);
        }

        String scriptHeader = "package rendererEngine; \n" +
                "import threeDItems.Mesh;\n" +
                "import java.util.Vector;\n" +
                "public class MasterScript extends InheritableClass{ \n" +
                "\tpublic void run(Vector<Mesh> meshVec) \n" +
                "\t{\n";
        //File masterScript = new File("D:\\ideaIntellij\\olcge\\src\\rendererEngine\\MasterScript.java");
       /* try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(masterScript));
            writer.write(scriptHeader);

            for(Mesh m: meshList)
            {
                if(m.isScripted)
                {
                    writer.write("\t\tGames.scriptOfMesh"+m.id+".run(meshVec.get("+(hash.get(m.id))+"));\n");
                }
            }
            writer.write("\t}\n}");
            writer.close();*/

            Cmd cmd = new Cmd();
            //cmd.runCommand("pushd D:\\ideaIntellij\\olcge\\src && more rendererEngine\\MasterScript.java ");
            /*cmd.runCommand("pushd D:\\ideaIntellij\\olcge\\src && javac rendererEngine/MasterScript.java " +
                    "&& move D:\\ideaIntellij\\olcge\\src\\rendererEngine\\MasterScript.class " +
                    "D:\\ideaIntellij\\olcge\\out\\production\\olcge\\rendererEngine" +
                    "&& exit");*/

            cmd.runCommand("pushd D:\\ideaIntellij\\olcge\\src &&" + "javac Games\\*.java" +
                    " && exit");

            cmd.runCommand("pushd D:\\ideaIntellij\\olcge\\src\\Games &&" +
                    "move *.class D:\\ideaIntellij\\olcge\\out\\production\\olcge\\Games" +
                    "&& exit");


        /*cmd.runCommand("pushd D:\\ideaIntellij\\olcge\\src &&" + "javac rendererEngine\\MasterScript.java" +
                " && exit");

        cmd.runCommand("pushd D:\\ideaIntellij\\olcge\\src\\rendererEngine &&" +
                "move *.class D:\\ideaIntellij\\olcge\\out\\production\\olcge\\rendererEngine" +
                "&& exit");*/



        /*} catch (IOException e) {
            e.printStackTrace();
        }*/



        File info = new File("D:\\ideaIntellij\\olcge\\src\\Games\\info.hma");
        try {
            //Writer write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(info)));
            BufferedWriter write = new BufferedWriter(new FileWriter(info));
            System.out.println("Preparing to write");
            for(int i=0; i<fileVec.size(); i++)
            {
                System.out.println("Writing");
                write.write(fileVec.elementAt(i)+ " ");
                write.write(meshList.get(i).xTheta+" "+ meshList.get(i).yTheta + " " + meshList.get(i).zTheta + " ");
                write.write(meshList.get(i).xTranslation+" "+ meshList.get(i).yTranslation + " " + meshList.get(i).zTranslation+ " ");
                write.write(meshList.get(i).xScale+" "+ meshList.get(i).yScale + " " + meshList.get(i).zScale);
                write.write(" "+meshList.get(i).isScripted + " " + meshList.get(i).id);
                write.write("\n");
            }
            write.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void deleteAllFilesFromGames()
    {
        File dir = new File("D:\\ideaIntellij\\olcge\\src\\Games");
        File arr[] = dir.listFiles();
        for(File f: arr)
        {
            if(f.getName().endsWith(".obj"))
                f.delete();
        }
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


}
