package rendererEngine;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Cmd {
    public static void runCommand(String command)
    {
        try
        {
            Process p = Runtime.getRuntime().exec("cmd /c start cmd.exe /K \""+ command + "\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while(reader.readLine()!=null)
            {
                System.out.println("Going");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //System.out.println("fixie");
    }
}
