package main;

import dataHandler.ModelLoader;
import dataHandler.Publisher;
import guiComponents.Controllers.ColliderController;
import guiComponents.Controllers.ScriptEditorController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import physicsEngine.CollisionModule.ObjectSliceAndMerge;
import rendererEngine.Cmd;
import threeDItems.Mesh;
import threeDItems.Vec3d;

import java.io.*;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    FlowPane fileListPane, addedFileListPane;
    @FXML
    Group gameStage;
    @FXML
    AnchorPane anchor, transPane, rhsPane, physicsPane;
    @FXML
    TextField scaleX, scaleY, scaleZ, rotX, rotY, rotZ, transX, transY, transZ;
    @FXML
    MenuItem topView, bottomView, rightView, leftView, frontView, backView, freeView, runInReleaseMode;
    @FXML
    MenuItem transMenuItem;
    @FXML
    Button delButton, editScript, createScript, createColliderButton;
    @FXML
    SplitPane split;
    @FXML
    CheckBox rigidBodyCB;
    @FXML
    Label isColliderSet, meshId;


    ModelLoader ml = new ModelLoader();
    final DisplayDriverGUI ddgui = new DisplayDriverGUI();
    double width=550, height=600;
    Canvas canvas = new Canvas( width, height );
    GraphicsContext gc = canvas.getGraphicsContext2D();
    List <Mesh> meshArray= new ArrayList<Mesh>();
    Mesh grid;
    VBox vbox2 = new VBox();
    int counter=0, selectedObject=-1;
    boolean clicked=false, tableEnabled=false, isObjectSelected=false;
    Vector<Label> labelVec = new Vector();
    Publisher publish = new Publisher();
    Scanner scan = new Scanner(System.in);
    public Vector<Integer> hash = new Vector<>();
    FXMLLoader loader;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //anchor.setOnMouseClicked(e-> System.out.println("dies"));
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        fileListPane.getChildren().add(vbox);

        vbox2.setSpacing(10);
        addedFileListPane.getChildren().add(vbox2);

        File file = new File("src\\resources");
        File [] arrOfFiles = file.listFiles();
        try {
            grid=ml.meshLoader("src\\resources\\","toNotDisplay\\grid2.obj", false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        grid.zTranslation=3f;
        ddgui.drawMesh(grid, 0f, gc);

        for(int i=0,j=i+1; i<arrOfFiles.length; i++)
        {
            final String name=arrOfFiles[i].getName();
            if(!arrOfFiles[i].getName().endsWith(".obj"))
            {
                continue;
            }
            Label label = new Label();
            label.setPadding(new Insets(3,5,3,5));
            label.setOnMouseClicked(e -> {
                addMeshToList(name);
                update();
            });
            label.setStyle("-fx-border-color: black;");
            label.setText("  " + (j) + ". " + arrOfFiles[i].getName() + "  ");
            label.setPrefWidth(200d);
            vbox.getChildren().add(label);
            j++;
        }

        /*gc.setFill(Color.BLUE);
        gc.fillRoundRect(100, 10, 50, 50, 10, 10);*/


        gameStage.getChildren().add(canvas);

        initializeTableItems();
        initializeMenuItems();
        initializePublisherMenuItems();
        initializeButtons();
        rigidBodyCB.setOnAction(e->{
            meshArray.get(selectedObject).isRigidBody=rigidBodyCB.isSelected();
            if(meshArray.get(selectedObject).isRigidBody)
            {
                if(meshArray.get(selectedObject).obb !=null)
                {
                    isColliderSet.setText("Collider is set");
                    createColliderButton.setText("View Collider");
                    createColliderButton.setDisable(true);
                }
                else
                {
                    isColliderSet.setText("No collider set");
                    createColliderButton.setText("Create Collider");
                    createColliderButton.setDisable(false);
                }
            }
            else
            {
                isColliderSet.setText("Collider N/A");
                meshArray.get(selectedObject).obb =null;
                createColliderButton.setDisable(true);
            }
            System.out.println(meshArray.get(selectedObject).isRigidBody);
            //createColliderButton.setDisable(false);
        });
        //System.out.println("fixie");
        //rhsPane.getChildren().clear();
        //initializeControls();
    }

    public void tro()
    {
        System.out.println("dsfdss");
        int i=0;
    }

    public void addMeshToList(String name)
    {
        //System.out.println("Loading first shit: " + ml.id);
        ArrayList<Mesh> al=null;
        try {
                Mesh toAdd = ml.meshLoader("src\\resources\\",name, true);
            System.out.println("After loading: " + toAdd.max);
            ObjectSliceAndMerge obj = new ObjectSliceAndMerge(toAdd);
            obj.createTree();
            al = obj.getMeshList();
            meshArray.addAll(al);
            System.out.println("Big moment: " + al.size());
            //meshArray.add(toAdd);

            //obj.divideWRTX(meshArray.get(meshArray.size()-1), 0.1f);
            //System.out.println("After division: " + meshArray.get(meshArray.size()-1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(int i=0; i<al.size(); i++) {
            //to restore to previous version, take this shit out of the loop.
            if (hash.size() > 0)
                hash.add(hash.lastElement() + 1);
            else
                hash.add(0);
            counter++;
            Label label = new Label();
            labelVec.add(label);
            label.setPadding(new Insets(3, 5, 3, 5));
            label.setText("  " + (counter) + ". " + name + "  ");
            //label.setText(name + "  ");
            int temp = Integer.parseInt(label.getText().substring(2, 3)) - 1;
            label.setOnMouseClicked(e -> {
                if (isObjectSelected) {
                    int earlierSelection = selectedObject;
                    System.out.println(labelVec.size() + "  " + selectedObject);
                    labelVec.get(selectedObject).setStyle("-fx-border-color: black;");
                    deSelectObject();
                    if (temp != earlierSelection) {
                        //selectObject(label.getText().substring(2,3));
                        selectObject(splitString(label.getText(), ".", true));
                        label.setStyle("-fx-border-color: red;");
                    }
                } else {
                    //selectObject(label.getText().substring(2,3));
                    System.out.println("Before special op: " + splitString(label.getText(), ".", true));
                    selectObject(splitString(label.getText(), ".", true));

                    label.setStyle("-fx-border-color: red;");
                    System.out.println("else: " + selectedObject);
                }
            });
            label.setOnKeyPressed(e -> System.out.println("sfdfsefsd"));
            label.setStyle("-fx-border-color: black;");
            label.setPrefWidth(200d);
            //vbox2.getChildren().add(label);
        }
    }

    public void selectObject(String s)
    {

        selectedObject=Integer.parseInt(s)-1;
        tableEnabled=true;
        disableAllTableItems(!tableEnabled);
        loadAllTableItems();
        isObjectSelected=true;
        delButton.setDisable(false);
        if(meshArray.get(selectedObject).isScripted)
        {
            createScript.setDisable(true);
            editScript.setDisable(false);
        }
        else
        {
            createScript.setDisable(false);
            editScript.setDisable(true);
        }
        rigidBodyCB.setSelected(meshArray.get(selectedObject).isRigidBody);

        meshId.setText("Mesh Id: "+ meshArray.get(selectedObject).id);
        if(meshArray.get(selectedObject).isRigidBody)
        {
            if(meshArray.get(selectedObject).obb !=null)
            {
                isColliderSet.setText("Collider is set");
                createColliderButton.setText("View Collider");
                //createColliderButton.setDisable(true);
            }
            else
            {
                isColliderSet.setText("No collider set");
                createColliderButton.setText("Create Collider");
                createColliderButton.setDisable(false);
            }
        }
        else
        {
            isColliderSet.setText("Collider N/A");
            createColliderButton.setDisable(true);
        }
        /*if(selectedObject!=-1)
        {*/


        /*}*/
        //System.out.println(s + " has been selected and its index is: " + hash.get(meshArray.get(selectedObject).id));
        //System.out.println("But its ID is: " + meshArray.get(selectedObject).id);
    }

    public void updateSelectedObj()
    {
        int i=1;
        vbox2.getChildren().clear();
        for(Label label: labelVec)
        {
            String temp = splitString(label.getText(), ".", false);
            label.setText("  " + i + ". " + temp);
            System.out.println(temp);
            vbox2.getChildren().add(label);
            i++;
        }

    }

    public String splitString(String s, String regex, boolean first)
    {
        String s1="";
        for(int i=0; i<s.length(); i++)
        {
            if(s.charAt(i)==regex.charAt(0))
            {
                if(first)
                    return s1.trim();
                else
                    return s.substring(i+1).trim();
            }
            else
            {
                s1+=s.charAt(i);
            }
        }
        return null;
    }

    public void deSelectObject()
    {
        selectedObject=-1;
        tableEnabled=false;
        disableAllTableItems(!tableEnabled);
        clearAllTableItems();
        isObjectSelected=false;
        delButton.setDisable(true);
        editScript.setDisable(true);
        createScript.setDisable(true);
        rigidBodyCB.setSelected(false);
        physicsPane.setDisable(true);
        isColliderSet.setText("Collider info");
        meshId.setText("Mesh Id");
    }

    public void update()
    {
        System.out.println("Mesh Array ize is: " + meshArray.size());
        /*if(meshArray.size()==4)
        {
            System.out.println(meshArray.get(1));
            meshArray.remove(1);
        }*/
        updateSelectedObj();
        gc.clearRect(0,0, width, height);
        ddgui.drawMesh(grid, 0f, gc);
        if(meshArray.size()>0)
            ddgui.onUserUpdate(0f, gc, meshArray);


    }

    public void disableAllTableItems(boolean disable)
    {
        scaleX.setDisable(disable); rotX.setDisable(disable); transX.setDisable(disable);
        scaleY.setDisable(disable); rotY.setDisable(disable); transY.setDisable(disable);
        scaleZ.setDisable(disable); rotZ.setDisable(disable); transZ.setDisable(disable);
        physicsPane.setDisable(disable);
    }

    public void loadAllTableItems()
    {
        scaleX.setText(""+meshArray.get(selectedObject).xScale); rotX.setText(""+meshArray.get(selectedObject).xTheta); transX.setText(""+meshArray.get(selectedObject).xTranslation);
        scaleY.setText(""+meshArray.get(selectedObject).yScale); rotY.setText(""+meshArray.get(selectedObject).yTheta); transY.setText(""+meshArray.get(selectedObject).yTranslation);
        scaleZ.setText(""+meshArray.get(selectedObject).zScale); rotZ.setText(""+meshArray.get(selectedObject).zTheta); transZ.setText(""+meshArray.get(selectedObject).zTranslation);
    }

    public void clearAllTableItems()
    {
        scaleX.setText(""); rotX.setText(""); transX.setText("");
        scaleY.setText(""); rotY.setText(""); transY.setText("");
        scaleZ.setText(""); rotZ.setText(""); transZ.setText("");
    }

    public void initializeTableItems()
    {
        scaleX.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).xScale=Float.parseFloat(scaleX.getText());
                    update();
                }
            }
        });

        disableAllTableItems(true);

        scaleY.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).yScale=Float.parseFloat(scaleY.getText());
                    update();
                }
            }
        });

        scaleZ.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).zScale=Float.parseFloat(scaleZ.getText());
                    update();
                }
            }
        });

        transX.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).xTranslation=Float.parseFloat(transX.getText());
                    update();
                }
            }
        });

        transY.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).yTranslation=Float.parseFloat(transY.getText());
                    update();
                }
            }
        });

        transZ.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).zTranslation=Float.parseFloat(transZ.getText());
                    update();
                }
            }
        });

        rotX.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).xTheta=Float.parseFloat(rotX.getText())*-3.14159f/180f;
                    update();
                }
            }
        });

        rotY.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).yTheta=Float.parseFloat(rotY.getText())*-3.14159f/180f;
                    update();
                }
            }
        });

        rotZ.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).zTheta=Float.parseFloat(rotZ.getText())*-3.14159f/180f;
                    update();
                }
            }
        });
    }

    public void initializeMenuItems()
    {
        initializeViewMenu();
        initializeEditMenu();
    }

    public void initializeEditMenu()
    {
        transMenuItem.setOnAction(e->{
            rhsPane.getChildren().clear();
            rhsPane.getChildren().add(transPane);
        });
    }

    public void initializeViewMenu()
    {
        topView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(0,5,0);
            ddgui.camera.pitch=3.14159f/2.0f;
            ddgui.camera.yaw=0;
            System.out.println("View");
            update();
        });

        bottomView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(0,-5,0);
            ddgui.camera.pitch=-3.14159f/2.0f;
            ddgui.camera.yaw=0;
            System.out.println("View");
            update();
        });

        frontView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(0,1,-3);
            ddgui.camera.pitch=0;
            ddgui.camera.yaw=0;
            update();
        });

        backView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(0,1,3);
            ddgui.camera.pitch=0;
            ddgui.camera.yaw=3.14159f;
            update();
        });

        rightView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(5,0,0);
            ddgui.camera.pitch=0;
            ddgui.camera.yaw=-3.14159f/2.0f;
            System.out.println("View");
            update();
        });

        leftView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(-5,0,0);
            ddgui.camera.pitch=0;
            ddgui.camera.yaw=3.14159f/2.0f;
            System.out.println("leftView");
            update();
        });

        freeView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(-3,5,-5);
            ddgui.camera.yaw=30*3.14159f/180f;
            ddgui.camera.pitch=30*3.14159f/180f;
            System.out.println("View");
            update();
        });

    }

    public void initializePublisherMenuItems()
    {
        System.out.println("");
        runInReleaseMode.setOnAction(e->{
            for(Mesh m: meshArray)
            {
                System.out.println("Is ID vsl: " + m.id);
            }
            publish.publish(ml.fileNameVector, meshArray);
            System.out.println("Confirmation recieved");
            //Cmd.runCommand("pushd D:\\ideaIntellij\\olcge\\out\\production\\olcge\\rendererEngine && java MainGL");

            File file = new File("src\\Games");
            if(file.exists())
            {
                System.out.println("File exists: " + "src\\Games");

                Cmd.runCommand("xcopy /s out\\production\\olcge\\*.class .\\olcge\\ /Y && echo copy done && exit");

                /*File source = new File("src\\Games");
                File sourcesArr[] = source.listFiles();
                for(File f: sourcesArr)
                    copyFile(f, file);

                source = new File("out\\production\\olcge\\Games");
                sourcesArr = source.listFiles();
                for(File f: sourcesArr)
                    copyFile(f, new File("olcge\\Games"));*/

                Cmd.runCommand("pushd olcge && java rendererEngine.MainGL");
            }
            else
            {
                throw new IllegalStateException("In else  in controller");
                //Cmd.runCommand("java rendererEngine.MainGL && exit");
            }




            //MainGL.main(new String[2]);
        });
    }

    public void initializeButtons()
    {
        delButton.setDisable(true);
        createScript.setDisable(true);
        editScript.setDisable(true);

        delButton.setOnAction(e ->{
            if(isObjectSelected)
            {
                int temp=selectedObject;
                deSelectObject();
                for(int i=meshArray.get(temp).id; i<hash.size(); i++)
                {
                    if(i==meshArray.get(temp).id)
                        hash.set(i, -1);
                    else
                        hash.set(i, hash.get(i)-1);
                }
                if(meshArray.get(temp).isScripted)
                {
                    File file = new File("src\\Games\\scriptOfMesh"+meshArray.get(temp).id+".java");
                    file.delete();
                }
                meshArray.remove(temp);
                labelVec.remove(temp);
                ml.fileNameVector.remove(temp);
                counter--;
                update();


            }
            if(selectedObject==-1)
                delButton.setDisable(true);
        });

        createScript.setOnAction(e->{
            System.out.println("Selected obj is: " + selectedObject);
            System.out.println("ID is: " + meshArray.get(selectedObject).id);
            File file = new File("src\\Games\\scriptOfMesh"+meshArray.get(selectedObject).id+".java");
            String scriptBody=
                    "package Games;\n" +
                    "import rendererEngine.scriptManager.Inheritable;\n" +
                    "import threeDItems.Mesh; \n" +
                    "import java.util.*; \n" +
                    "public class scriptOfMesh"+meshArray.get(selectedObject).id+" extends Inheritable {\n" +
                        "\t//Mesh mesh = getMesh(0);\n" +
                        "\t@Override\n" +
                        "\tpublic void run(Map<Integer, Mesh> meshMap) {\n" +
                            "\t\tSystem.out.println(\"YO\");\n" +
                            "\t\tmeshMap.get(0).xTheta+=0.01f;\n" +
                        "\t}\n" +
                    "}";
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(scriptBody);
                meshArray.get(selectedObject).isScripted=true;
                writer.close();
                createScript.setDisable(true);
                editScript.setDisable(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });

        editScript.setOnAction(e->{
            Stage primaryStage= new Stage();
            Parent root;
            //root = FXMLLoader.load(getClass().getResource("../guiComponents/scriptEditor.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../guiComponents/fxml/scriptEditor.fxml"));
            try {
                root = loader.load();
                primaryStage.setTitle("Hello World");
                primaryStage.setScene(new Scene(root));
                primaryStage.show();
                ScriptEditorController se = loader.getController();
                se.editScript(meshArray.get(selectedObject).id);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        createColliderButton.setOnAction(e ->{
            loader= new FXMLLoader(getClass().getResource("../guiComponents/fxml/colliderPane.fxml"));
            try {
                Parent root = loader.load();
                //Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();

                ColliderController c= loader.getController();
                c.setObb(meshArray.get(selectedObject));

                /*c.setThisMesh(meshArray.get(selectedObject));
                c.update(false);*/


            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });

    }

    public int indexFinder(int id)
    {
        return hash.get(id);
    }

    public void copyFile(File source,File dest) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
        File oFile = new File(dest.getPath()+"\\"+source.getName());
        System.out.println("OFILE: " + dest.getPath()+"\\"+source.getName());
        System.out.println(oFile.getPath());

        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(oFile);

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

    @FXML
    public void handleInputs(KeyEvent event)
    {
        System.out.println("dsds");
    }

    public void useless()
    {
        int i=0;
        i++;
    }

}

//TODO allow renaming scripts and objects.
//TODO allow user to select obj and script path with fileView screen