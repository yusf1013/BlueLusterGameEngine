package guiComponents.Controllers;

import dataHandler.ModelLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import main.DisplayDriverCollider;
import physicsEngine.CollisionModule.Obb;
import threeDItems.Mesh;
import threeDItems.Vec3d;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ColliderController implements Initializable {

    @FXML
    AnchorPane gameStage;
    @FXML
    Button setButton;
    @FXML
    MenuItem topView, bottomView, rightView, leftView, frontView, backView, freeView;

    Mesh thisMesh;
    double width=475, height=475;
    Canvas canvas = new Canvas( width, height);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    DisplayDriverCollider ddcol = new DisplayDriverCollider();
    //DisplayDriverGUI ddgui = new DisplayDriverGUI();
    ModelLoader ml = new ModelLoader();
    Mesh grid, aabbBox;
    Obb obb;
    List<Mesh> vector = new ArrayList<>();
    boolean meshAddeda=false, bool2=true;


    public void setThisMesh(Mesh thisMesh)
    {
        this.thisMesh=thisMesh;
    }

    public void update(boolean meshAdded)
    {
        //System.out.println("fixie");
        /*gc.clearRect(0,0, width, height);
        if(bool2)
        {
            vector.add(thisMesh);
            bool2=false;
        }
        ddcol.drawMesh(grid, 0f, gc);
        //ddcol.drawMesh(thisMesh, 0f, gc);
        if(meshAdded)
        {
            *//*aabbBox.xTranslation+=thisMesh.xTranslation;
            aabbBox.yTranslation+=thisMesh.yTranslation;
            aabbBox.zTranslation+=thisMesh.zTranslation;

            aabbBox.xScale*=thisMesh.xScale;
            aabbBox.yScale*=thisMesh.yScale;
            aabbBox.zScale*=thisMesh.zScale;*//*

            if(!meshAddeda)
            {
                vector.add(aabbBox);
                System.out.println("aabbBox added");
                meshAddeda=true;
            }

            System.out.println("Min and Max y: " + obb.min.y + " " + obb.max.y);
        }
        ddcol.onUserUpdate(0f, gc, vector);
        System.out.println("Vector size in collider controller: " + vector.size());
        //System.out.println(aabbBox);*/
        System.out.println("everything in update is disabled");
        gc.clearRect(0,0, width, height);
        ddcol.onUserUpdate(0f, gc, vector);
    }

    public void setButtonAction()
    {
        /*System.out.println("In set button action");
        obb=new Obb(thisMesh);
        aabbBox=obb.getMesh(thisMesh);
        thisMesh.obb=obb;
        for(Triangle tri:aabbBox.tris)
        {
            tri.setColor(Color.TRANSPARENT);
        }

        update(true);*/
        System.out.println("Everything is diabled");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialize");
        /*try {
            grid=ml.meshLoader("src\\resources\\","toNotDisplay\\grid2.obj", false);
            vector.add(ml.meshLoader("src\\resources\\","point.obj", false));
            //vector.add(grid);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        //grid.zTranslation=4f;
        ddcol.camera.position=new Vec3d(-3,5,-5);
        ddcol.camera.yaw=30*3.14159f/180f;
        ddcol.camera.pitch=30*3.14159f/180f;
        //ddcol.drawMesh(grid, 0f, gc);
        gameStage.getChildren().add(canvas);
        initializeViewMenu();
    }

    public void setObb(Mesh mesh)
    {
        if(mesh.obb ==null)
        {
            obb = new Obb(mesh);
            aabbBox = obb.getMesh(mesh);
            vector.add(mesh);
            vector.add(aabbBox);
            mesh.obb = obb;
            update(true);
        }
        else
        {
            obb = mesh.obb;
            aabbBox = mesh.obb.getMesh(mesh);
            vector.add(mesh);
            vector.add(aabbBox);
            update(true);
            System.out.println("Obb loaded");
        }
    }

    public void initializeViewMenu()
    {
        topView.setOnAction(e -> {
            ddcol.camera.position=new Vec3d(0,5,0);
            ddcol.camera.pitch=3.14159f/2.0f;
            ddcol.camera.yaw=0;
            System.out.println("View");
            update(false);
        });

        bottomView.setOnAction(e -> {
            ddcol.camera.position=new Vec3d(0,-5,0);
            ddcol.camera.pitch=-3.14159f/2.0f;
            ddcol.camera.yaw=0;
            System.out.println("View");
            update(false);
        });

        frontView.setOnAction(e -> {
            ddcol.camera.position=new Vec3d(0,1,-3);
            ddcol.camera.pitch=0;
            ddcol.camera.yaw=0;
            update(false);
        });

        backView.setOnAction(e -> {
            ddcol.camera.position=new Vec3d(0,1,3);
            ddcol.camera.pitch=0;
            ddcol.camera.yaw=3.14159f;
            update(false);
        });

        rightView.setOnAction(e -> {
            ddcol.camera.position=new Vec3d(5,0,0);
            ddcol.camera.pitch=0;
            ddcol.camera.yaw=-3.14159f/2.0f;
            System.out.println("View");
            update(false);
        });

        leftView.setOnAction(e -> {
            ddcol.camera.position=new Vec3d(-5,0,0);
            ddcol.camera.pitch=0;
            ddcol.camera.yaw=3.14159f/2.0f;
            System.out.println("leftView");
            update(false);
        });

        freeView.setOnAction(e -> {
            ddcol.camera.position=new Vec3d(-3,5,-5);
            ddcol.camera.yaw=30*3.14159f/180f;
            ddcol.camera.pitch=30*3.14159f/180f;
            System.out.println("View");
            update(false);
        });

    }


}
