package guiComponents;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ScriptEditorController implements Initializable {

    @FXML
    TextArea another;
    @FXML
    MenuItem save;

    public void editScript(int id)
    {
        String s="";
        File file = new File("D:\\ideaIntellij\\olcge\\src\\Games\\scriptOfMesh"+id+".java");
        try {
            Scanner scan = new Scanner(file);
            scan.useDelimiter("\\Z");
            another.setText(scan.next());
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        save.setOnAction(e->{
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(another.getText());
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
