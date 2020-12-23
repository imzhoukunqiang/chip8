package com.github.zkq.emulator.chip8.gui;/**
 * Date:2020/4/15 15:45
 *
 * @author zhoukq
 */

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class App extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(300, 250);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawShapes(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        //primaryStage.setResizable(false);
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("oldValue = " + oldValue);
            System.out.println("newValue = " + newValue);
        });
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("oldValue = " + oldValue);
            System.out.println("newValue = " + newValue);
        });
    }

    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        //gc.fillPolygon();
        //gc.strokeLine(40, 10, 10, 40);
        //gc.fillOval(10, 60, 30, 30);
        //gc.strokeOval(60, 60, 30, 30);
        //gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        //gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        //gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        //gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        //gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        //gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        //gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        //gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillRect(20,30,20,20);
        gc.setFill(Color.BLACK);
        gc.fillPolygon(new double[]{10, 40, 40, 10},
                       new double[]{210, 210, 240, 240}, 4);
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
        pool.schedule(() -> {
            Canvas canvas = gc.getCanvas();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            pool.shutdown();
        }, 2, TimeUnit.SECONDS);
        //gc.strokePolygon(new double[]{60, 90, 60, 90},
        //                 new double[]{210, 210, 240, 240}, 4);
        //gc.strokePolyline(new double[]{110, 140, 110, 140},
        //                  new double[]{210, 210, 240, 240}, 4);
    }


}
