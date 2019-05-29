package main;

import dataHandler.ModelLoader;
import dataHandler.Publisher;
import guiComponents.Controllers.ColliderController;
import guiComponents.Controllers.ScriptEditorController;
import javafx.event.ActionEvent;
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
import javafx.stage.*;
import mathHandler.VectorGeometry;
import rendererEngine.Camera;
import rendererEngine.Cmd;
import rendererEngine.itemBag.ItemBag;
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
    MenuItem topView, bottomView, rightView, leftView, frontView, backView, freeView, runInReleaseMode, publishGame;
    @FXML
    MenuItem transMenuItem, save, load, addCam;
    @FXML
    Button delButton, editScript, createScript, createColliderButton;
    @FXML
    SplitPane split;
    @FXML
    CheckBox rigidBodyCB;
    @FXML
    Label isColliderSet, meshId;
    @FXML
    VBox mainVBox;


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
    File saveFile=null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpStage();
    }

    public void setUpStage()
    {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        fileListPane.getChildren().clear();
        fileListPane.getChildren().add(vbox);

        vbox2.getChildren().clear();
        vbox2.setSpacing(10);
        addedFileListPane.getChildren().clear();
        addedFileListPane.getChildren().add(vbox2);

        File file = new File("src\\resources");
        File [] arrOfFiles = file.listFiles();
        try {
            grid=ml.meshLoader("src\\resources\\","toNotDisplay\\grid2.obj", false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        grid.setzTranslation(3f);
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

        gameStage.getChildren().clear();
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
        });

        mainVBox.requestFocus();
        System.out.println("focus requested!");
        mainVBox.setOnKeyPressed(e -> {
            handleUserInputs(e);
        });
    }

    public void test()
    {
        System.out.println("TEST");
    }

    public void resetStage()
    {
        ml = new ModelLoader();
        width=550;
        height=600;
        canvas = new Canvas( width, height );
        gc = canvas.getGraphicsContext2D();
        meshArray= new ArrayList<Mesh>();
        vbox2 = new VBox();
        counter=0;
        selectedObject=-1;
        tableEnabled=false;
        isObjectSelected=false;
        labelVec = new Vector();
        publish = new Publisher();
        scan = new Scanner(System.in);
        hash = new Vector<>();
        saveFile=null;
        setUpStage();
    }

    public void addMeshToList(String name)
    {
        try {
            addMeshToList(ml.meshLoader("src\\resources\\",name, true), name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void addMeshToList(Mesh toAdd, String name)
    {

        System.out.println("TOADD stats:  " + toAdd.getStats());
        System.out.println("After loading: " + toAdd.max);
        meshArray.add(toAdd);

        for(int i=0; i<1; i++) {
            if (hash.size() > 0)
                hash.add(hash.lastElement() + 1);
            else
                hash.add(0);
            counter++;
            addLabel(counter, name);
        }
    }

    public void addLabel(int counter, String name)
    {
        Label label = new Label();
        labelVec.add(label);
        label.setPadding(new Insets(3, 5, 3, 5));
        label.setText("  " + (counter) + ". " + name + "  ");
        //label.setText(name + "  ");
        System.out.println("add label: " + label.getText());
        int temp = Integer.parseInt(label.getText().substring(2, 3)) - 1;
        System.out.println("added label: " + label.getText() + " " + temp);
        label.setOnMouseClicked(e -> {
            if (isObjectSelected) {
                int earlierSelection = selectedObject;
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
                selectObject(splitString(label.getText(),".", true));

                label.setStyle("-fx-border-color: red;");
                System.out.println("else: " + selectedObject);
            }
        });
        //label.setOnKeyPressed(e ->System.out.println("sfdfsefsd"));
        label.setStyle("-fx-border-color: black;");
        label.setPrefWidth(200d);
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
        //ystem.out.println(s + " has been selected and its index is: " + hash.get(meshArray.get(selectedObject).id));
        //ystem.out.println("But its ID is: " + meshArray.get(selectedObject).id);
    }

    public void updateSelectedObj()
    {
        int i=1;
        vbox2.getChildren().clear();
        for(Label label: labelVec)
        {
            String temp = splitString(label.getText(), ".", false);
            label.setText("  " + i + ". " + temp);
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

    public void updateLabelList()
    {
        System.out.println("Size matters!");
        labelVec.clear();
        int counter=0;
        for(Mesh mesh: meshArray)
        {
            System.out.println("Mesh name in controller: " + mesh.name);
            addLabel(++counter, mesh.name);
        }

        vbox2.getChildren().clear();
        //int i=0;
        for(Label l: labelVec) {
            /*//System.out.println("label text before: " + l.getText());
            i++;
            l.setText(i + ". " + l.getText());
            System.out.println("label text after: " + l.getText());*/
            vbox2.getChildren().add(l);
        }
    }

    public void update()
    {
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
        scaleX.setText(""+ meshArray.get(selectedObject).getxScale()); rotX.setText(""+ meshArray.get(selectedObject).getxTheta()); transX.setText(""+ meshArray.get(selectedObject).getxTranslation());
        scaleY.setText(""+ meshArray.get(selectedObject).getyScale()); rotY.setText(""+ meshArray.get(selectedObject).getyTheta()); transY.setText(""+ meshArray.get(selectedObject).getyTranslation());
        scaleZ.setText(""+ meshArray.get(selectedObject).getzScale()); rotZ.setText(""+ meshArray.get(selectedObject).getzTheta()); transZ.setText(""+ meshArray.get(selectedObject).getzTranslation());
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
                if(arrowKeysAction(event) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).setxScale(Float.parseFloat(scaleX.getText()));
                    update();
                }
                arrowKeysAction(event);
            }
        });

        disableAllTableItems(true);

        scaleY.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(arrowKeysAction(event) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).setyScale(Float.parseFloat(scaleY.getText()));
                    update();
                }
            }
        });

        scaleZ.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(arrowKeysAction(event) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).setzScale(Float.parseFloat(scaleZ.getText()));
                    update();
                }

            }
        });

        transX.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //arrowKeysAction(event);
                if(arrowKeysAction(event) && selectedObject!=-1)
                {
                    System.out.println("goind in");
                    meshArray.get(selectedObject).setxTranslation(Float.parseFloat(transX.getText()));
                    update();
                }
            }
        });

        transY.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(arrowKeysAction(event) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).setyTranslation(Float.parseFloat(transY.getText()));
                    update();
                }
            }
        });


        transZ.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(arrowKeysAction(event) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).setzTranslation(Float.parseFloat(transZ.getText()));
                    update();
                }
            }
        });

        rotX.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(arrowKeysAction(event) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).setxTheta(Float.parseFloat(rotX.getText())*-3.14159f/180f);
                    update();
                }
            }
        });

        rotY.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(arrowKeysAction(event) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).setyTheta(Float.parseFloat(rotY.getText())*-3.14159f/180f);
                    update();
                }
            }
        });

        rotZ.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(arrowKeysAction(event) && selectedObject!=-1)
                {
                    meshArray.get(selectedObject).setzTheta(Float.parseFloat(rotZ.getText())*-3.14159f/180f);
                    update();
                }
            }
        });
    }

    public void initializeMenuItems()
    {
        initializeViewMenu();
        initializeEditMenu();
        addCam.setOnAction(e ->{
            try {
                addCam();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        } );
    }

    public void addCam() throws FileNotFoundException {
        Mesh cam = ml.meshLoader("src\\resources\\","camera.obj", true);
        ItemBag.camMesh=cam;
        cam.isCam=true;
        addMeshToList(cam, "camera");
        update();
    }

    public void addCam(Mesh cam) {
        ItemBag.camMesh=cam;
        cam.isCam=true;
        addMeshToList(cam, "camera");
        update();
    }

    @FXML
    public void saveMenuItemAction()
    {
        if(saveFile==null)
            saveAsMenuItemAction();
        else
        {
            deleteFolder(saveFile);
            /*saveFile.mkdir();
            publish.publishNoGames(ml.fileNameVector, meshArray, saveFile.getAbsolutePath()+"\\");
            File arr[] = new File("src\\Games").listFiles();
            for(File f: arr)
                if(f.getName().endsWith(".java"))
                    publish.copyFile(f, new File(saveFile.getAbsolutePath()+"\\"+f.getName()));*/
            save();
        }

    }

    @FXML
    public void saveAsMenuItemAction()
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("D:\\ideaIntellij\\olcge\\saveProjects"));
        File selectedDir = directoryChooser.showDialog(split.getScene().getWindow());
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enter file name");
        dialog.setHeaderText("Project will be saved in folders. \nPlease enter name to save project by:");
        dialog.setContentText("Name:");
        dialog.showAndWait();
        String name = dialog.getEditor().getText();
        saveFile = new  File(selectedDir.getAbsolutePath()+"\\"+name);
        boolean makeDir = true;
        if(saveFile.exists())
            makeDir = showAlert(Alert.AlertType.CONFIRMATION, "Save file exists", "Save file exists. Overwrite?", "");

        if(makeDir)
        {
            if(saveFile.exists())
            {
                System.out.println("deleting");
                deleteFolder(saveFile);
            }

            /*saveFile.mkdir();
            System.out.println("save file name after make dir: " + saveFile.getAbsolutePath() +  ": " + saveFile.exists());
            publish.publishNoGames(ml.fileNameVector, meshArray, saveFile.getAbsolutePath()+"\\");
            File arr[] = new File("src\\Games").listFiles();
            for(File f: arr)
                if(f.getName().endsWith(".java"))
                    publish.copyFile(f, new File(saveFile.getAbsolutePath()+"\\"+f.getName()));*/
            save();
        }

    }

    public void save()
    {
        saveFile.mkdir();
        System.out.println("save file name after make dir: " + saveFile.getAbsolutePath() +  ": " + saveFile.exists());
        publish.publishNoGames(ml.fileNameVector, meshArray, saveFile.getAbsolutePath()+"\\");
        File arr[] = new File("src\\Games").listFiles();
        for(File f: arr)
            if(f.getName().endsWith(".java"))
                publish.copyFile(f, new File(saveFile.getAbsolutePath()+"\\"+f.getName()));
    }

    public void deleteFolder(File dir)
    {
        File arr[] = dir.listFiles();
        if(arr==null)
            return;
        for(File f: arr)
        {
            if(f.isDirectory())
                deleteFolder(f);
            f.delete();
        }
        dir.delete();
    }

    @FXML
    public void loadMenuItemAction(ActionEvent event)
    {
        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D:\\ideaIntellij\\olcge\\saveProjects"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.hma")
        );
        File file = fileChooser.showOpenDialog(split.getScene().getWindow());
        if(file!=null && file.getName().endsWith(".hma"))
        {
            meshArray.clear();
            ml.fileNameVector.clear();
            System.out.println("in controller before  loading: " + ml.fileNameVector.size());
            meshArray.addAll(ml.loadAll(file, false));
            System.out.println("in controller after  loading: " + ml.fileNameVector.size());
            saveFile=file.getParentFile();
            System.out.println("In loading: "+saveFile.getAbsolutePath());
        }
        else if(file!=null)
            showAlert(Alert.AlertType.ERROR, "Wrong file extension", "File extension must have \".hma\" extension", "");

        update();
        updateLabelList();
        File arr[] = saveFile.listFiles();
            for(File f: arr)
                if(f.getName().endsWith(".java"))
                    publish.copyFile(f, new File("src\\Games\\"+f.getName()));
*/
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D:\\ideaIntellij\\olcge\\saveProjects"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.hma")
        );
        File file = fileChooser.showOpenDialog(split.getScene().getWindow());
        if(file!=null && file.getName().endsWith(".hma"))
        {
            resetStage();
            Vector<Mesh> velco = ml.loadAll(file, false);
            if(ItemBag.camMesh!=null) {
                addCam(ItemBag.camMesh);
            }

            for(Mesh m: velco){
                addMeshToList(m, m.name);
                update();
            }

            saveFile=file.getParentFile();
            System.out.println("In loading: "+saveFile.getAbsolutePath());
            File arr[] = saveFile.listFiles();
            for(File f: arr)
                if(f.getName().endsWith(".java"))
                    publish.copyFile(f, new File("src\\Games\\"+f.getName()));
        }
        else if(file!=null)
            showAlert(Alert.AlertType.ERROR, "Wrong file extension", "File extension must have \".hma\" extension", "");

        update();
        System.out.println("Mesh arr size: " + meshArray.size());


    }

    public boolean showAlert(Alert.AlertType alertType, String title, String headerText, String contents)
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contents);
        alert.showAndWait();
        if(alertType.equals(Alert.AlertType.CONFIRMATION))
        {
            if (alert.getResult() == ButtonType.OK)
                return true;
            else
                return false;
        }
        return true;
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
            ddgui.camera.setPitch(3.14159f/2.0f);
            ddgui.camera.setYaw(0);
           System.out.println("View");
            update();
        });

        bottomView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(0,-5,0);
            ddgui.camera.setPitch(-3.14159f/2.0f);
            ddgui.camera.setYaw(0);
           System.out.println("View");
            update();
        });

        frontView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(0,1,-3);
            ddgui.camera.setPitch(0);
            ddgui.camera.setYaw(0);
            update();
        });

        backView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(0,1,3);
            ddgui.camera.setPitch(0);
            ddgui.camera.setYaw(3.14159f);
            update();
        });

        rightView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(5,0.4f,0);
            ddgui.camera.setPitch(0);
            ddgui.camera.setYaw(-3.14159f/2.0f);
           System.out.println("View");
            update();
        });

        leftView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(-5,0.4f,0);
            ddgui.camera.setPitch(0);
            ddgui.camera.setYaw(3.14159f/2.0f);
           System.out.println("leftView");
            update();
        });

        freeView.setOnAction(e -> {
            ddgui.camera.position=new Vec3d(-3,5,-5);
            ddgui.camera.setYaw(30*3.14159f/180f);
            ddgui.camera.setPitch(30*3.14159f/180f);
           System.out.println("View");
            update();
        });

    }

    public void initializePublisherMenuItems()
    {
        publishGame.setOnAction(e -> {
            publish.publish(ml.fileNameVector, meshArray);
            File file = new File("");
            System.out.println(file.getAbsolutePath());
            String command = "pushd " + file.getAbsolutePath() + "&& " +
                    "jar cvfm game.jar ./olcge/META-INF/MANIFEST.MF -C ./olcge/ ." +
                    " && exit"
                    ;
            Cmd.runCommand(command);
            Cmd.runCommand("xcopy /s olcge\\Games\\*.* .\\Games\\ /Y && exit");
            showAlert(Alert.AlertType.INFORMATION, "Published successfully!", "You game has now been published!", "");
        });

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
            deleteMesh();
            update();
            if(selectedObject==-1)
                delButton.setDisable(true);
        });

        createScript.setOnAction(e->{
           System.out.println("Selected obj is: " + selectedObject);
           System.out.println("ID is: " + meshArray.get(selectedObject).id);
            File file = new File("src\\Games\\scriptOfMesh"+meshArray.get(selectedObject).id+".java");
            /*String scriptBody=
                    "package Games;\n" +
                    "import rendererEngine.scriptManager.Inheritable;\n" +
                    "import threeDItems.Mesh; \n" +
                    "import rendererEngine.scriptManager.Control;\n" +
                    "import java.util.*; \n" +
                    "import threeDItems.Vec3d;\n" +
                    "import rendererEngine.itemBag.ItemBag;\n" +
                    "public class scriptOfMesh"+meshArray.get(selectedObject).id+" extends Inheritable {\n" +
                        "\t//Mesh mesh = getMesh(0);\n" +
                        "\tint i=0;\n" +
                        "\t@Override\n" +
                        "\tpublic void run(Map<Integer, Mesh> meshMap) {\n" +
                            "\t\tSystem.out.println(++i);\n" +
                            "\t\tMesh thisMesh=meshMap.get(0);\n" +
                            "\t\t//thisMesh.setzTheta(thisMesh.getzTheta()+0.01f);\n" +
                            "\t\t//thisMesh.setxTranslation(thisMesh.getxTranslation()+0.01f);\n" +
                            "\t\t//Control.bindCamera(thisMesh);\n" +
                            "\t\tControl.walkControlScheme(thisMesh, 0.01f, 0.01f);\n" +
                        "\t}\n" +
                    "}";*/

            String scriptBody = "package Games;\n" +
                    "import rendererEngine.scriptManager.Inheritable;\n" +
                    "import threeDItems.Mesh; \n" +
                    "import rendererEngine.scriptManager.Control;\n" +
                    "import java.util.*; \n" +
                    "\n" +
                    "public class scriptOfMesh"+meshArray.get(selectedObject).id+" extends Inheritable {\n" +
                    "\n" +
                    "\t@Override\n" +
                    "\tpublic void run(Map<Integer, Mesh> meshMap) {\n" +
                    "\t\tMesh thisMesh=meshMap.get("+meshArray.get(selectedObject).id+");\n" +
                    "\t\t\n" +
                    "\t\t//write your script here. Some sample lines are written.\n" +
                    "\t\t//thisMesh.move(0.1f, 0.1f, 0.1f);\n" +
                    "\t\t//thisMesh.teleportTo(1,1,1);\n" +
                    "\t\t//thisMesh.rotate(0.1f, 0.1f, 0.1f);\n" +
                    "\t\t//thisMesh.scale(0.1f, 0.1f, 0.1f);\n" +
                    "\t\t//thisMesh.growInSize(0.1f, 0.1f, 0.1f);\n" +
                    "\t\t//Control.walkControlScheme(thisMesh, 0.1f, 0.1f);\n" +
                    "\t\t//Control.bindCamera(thisMesh);\n" +
                    "\t\t//Control.setColor(thisMesh, 1.0f, 0f, 0f);\n" +
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
                //c.setThisMesh(meshArray.get(selectedObject));
                c.setObb(meshArray.get(selectedObject));

                /*c.setThisMesh(meshArray.get(selectedObject));
                c.update(false);*/


            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });

    }

    public void deleteMesh()
    {
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

    }

    public boolean arrowKeysAction(KeyEvent e)
    {
        Double f=0d;
        KeyCode code = e.getCode();
        System.out.println("CODE: " + code);
        if(!(code==KeyCode.UP || code==KeyCode.DOWN || code==KeyCode.LEFT || code==KeyCode.RIGHT || code==KeyCode.ENTER))
            return false;

        System.out.println("Here: ");

        TextField tf = ((TextField)e.getSource());
        try{
             f = Double.parseDouble(tf.getText());
            if((code.equals(KeyCode.UP)))
            {
                f+=0.01;
            }
            else if((code.equals(KeyCode.DOWN)))
                f-=0.01;
            else if((code.equals(KeyCode.PAGE_UP)))
                f+=0.1;
            else if((code.equals(KeyCode.PAGE_DOWN)))
                f-=0.1;
            else
            {
                System.out.println("no accept: " + code + " " + KeyCode.PAGE_DOWN + " " + KeyCode.PAGE_UP);
                if(code==KeyCode.ENTER)
                    return true;
                else
                    return false;
            }

            System.out.println("Kill them: " + Double.toString(f).substring(0,Math.min(5, Double.toString(f).length())));

            tf.setText(Double.toString(f).substring(0,Math.min(5, Double.toString(f).length())));

        } catch (NumberFormatException exception)
        {
            showAlert(Alert.AlertType.ERROR, "Invalid input" , "Please enter only numbers", "");
        }

        return true;

    }

    public int indexFinder(int id)
    {
        return hash.get(id);
    }

    public void handleUserInputs(KeyEvent event) {

        VectorGeometry v = new VectorGeometry();
        float speed=0.5f;
        Camera camera = ddgui.camera;
        Vec3d left = (v.crossProduct(camera.vUp, camera.vLookDir));
        Vec3d vForward = v.vectorMul(camera.vLookDir, speed);


        KeyCode code  = event.getCode();
        if(code==KeyCode.A)
            camera.position = v.vectorAdd(camera.position, v.vectorMul(v.normaliseVector(v.crossProduct(camera.vLookDir, camera.vUp)), speed));
        if(code==KeyCode.D)
            camera.position = v.vectorSub(camera.position, v.vectorMul(v.normaliseVector(v.crossProduct(camera.vLookDir, camera.vUp)), speed));
        if(code==KeyCode.W)
            camera.position = v.vectorAdd(camera.position, v.vectorMul(v.normaliseVector(v.crossProduct(camera.vLookDir, left)), speed));
        if(code==KeyCode.S)
            camera.position = v.vectorSub(camera.position, v.vectorMul(v.normaliseVector(v.crossProduct(camera.vLookDir, left)), speed));
        if(code==KeyCode.UP)
            camera.position = v.vectorAdd(camera.position, vForward);
        if(code==KeyCode.DOWN)
            camera.position = v.vectorSub(camera.position, vForward);
        if(code==KeyCode.R)
            camera.position = v.vectorAdd(camera.position, vForward);
        if(code==KeyCode.F)
            camera.position = v.vectorSub(camera.position, vForward);

        update();


        /*if (KeyboardHandler.isKeyDown(GLFW_KEY_UP))
            camera.position.y += walkSpeed * fElapsedTime;
        if (KeyboardHandler.isKeyDown(GLFW_KEY_DOWN))
            camera.position.y -= walkSpeed * fElapsedTime;
        if (KeyboardHandler.isKeyDown(GLFW_KEY_A))
            camera.position = vectorAdd(camera.position, vectorMul(normaliseVector(crossProduct(camera.vLookDir, camera.vUp)), fElapsedTime * walkSpeed));
        if (KeyboardHandler.isKeyDown(GLFW_KEY_D))
            camera.position = vectorSub(camera.position, vectorMul(normaliseVector(crossProduct(camera.vLookDir, camera.vUp)), fElapsedTime * walkSpeed));
        Vec3d vForward = vectorMul(camera.vLookDir, walkSpeed * fElapsedTime);
        if (KeyboardHandler.isKeyDown(GLFW_KEY_W))
            camera.position = vectorAdd(camera.position, vForward);
        if (KeyboardHandler.isKeyDown(GLFW_KEY_S))
            camera.position = vectorSub(camera.position, vForward);
        if (KeyboardHandler.isKeyDown(GLFW_KEY_LEFT))
            camera.yaw -= walkSpeed / 4f * fElapsedTime;
        if (KeyboardHandler.isKeyDown(GLFW_KEY_RIGHT))
            camera.yaw += walkSpeed / 4f * fElapsedTime;
        if (KeyboardHandler.isKeyDown(GLFW_KEY_R))
            camera.pitch -= walkSpeed / 4f * fElapsedTime;
        if (KeyboardHandler.isKeyDown(GLFW_KEY_F))
            camera.pitch += walkSpeed / 4f * fElapsedTime;*/

    }

    @FXML
    public void handleInputs(KeyEvent event)
    {
       System.out.println("dsds");
    }

    public void setLightModeWell()
    {
        ItemBag.lightMode=1;
    }

    public void setLightModeTorch()
    {
        ItemBag.lightMode=2;
    }

    public void setLightModeBoth()
    {
        ItemBag.lightMode=3;
    }

}

//TODO allow renaming scripts and objects.
//TODO allow user to select obj and script path with fileView screen