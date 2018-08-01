package graphBuilder;

import java.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

public class Main extends Application {

    static double mouseX;
    static double mouseY;

    private static GraphicsContext gc;
    private Canvas canvas;
    private static double canvasHeight;
    private static double canvasWidth;
    static double centerX;
    static double centerY;
    static double delta = 0.01;
    static double startendXY = 500;
    static int scale = 40;

    static ArrayList<Double> chordsY = new ArrayList<>();
    static ArrayList<Double> chordsX = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/main.fxml"));
        primaryStage.setTitle("Graph Builder by Aleksandr Vasilenko БИС-17-02");
        primaryStage.setScene(new Scene(root, 785, 635));
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream( "resources/favicon.png" )));
        primaryStage.setMinWidth(785);
        primaryStage.setMinHeight(635);
        canvas = ((Canvas) root.lookup("#mainCanvas"));
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        centerX = canvasWidth / 2;
        centerY = canvasHeight / 2;
        gc = canvas.getGraphicsContext2D();
        primaryStage.show();
        primaryStage.widthProperty().addListener(e -> onResize(primaryStage));
        primaryStage.heightProperty().addListener(e -> onResize(primaryStage));
        primaryStage.fullScreenProperty().addListener(e -> onResize(primaryStage));
        onResize(primaryStage);
        draw(canvas);
    }

    private void onResize(Stage primaryStage){
        canvas.setWidth(primaryStage.getWidth());
        canvas.setHeight(primaryStage.getHeight() - 100);
        draw(canvas);
    }

    static void draw(Canvas canvas){
        if (scale % 2 != 0.0) scale--;
        if (startendXY % 2 != 0.0) startendXY++;
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        gc.setFill(COLORS.background);
        gc.fillRect(0,0,canvasWidth, canvasHeight);
        gc.setFill(COLORS.main_grid);
        gc.setStroke(COLORS.main_grid);
        gc.setLineWidth(2);
        gc.setLineCap(StrokeLineCap.SQUARE);

        if (scale > 35) {
            gc.setLineWidth(1);
            for (double i = -startendXY; i < startendXY + 1; i += 0.1) {
                if (i != 0) {
                    gc.strokeLine(centerX + (i * scale), centerY - 3, centerX + (i * scale), centerY + 3);
                    gc.strokeLine(centerX - 3, centerY + (i * scale), centerX + 3, centerY + (i * scale));
                    gc.setFill(COLORS.grid01);
                    gc.setStroke(COLORS.grid01);
                    gc.strokeLine(0, centerY + (i * scale), canvasWidth, centerY + (i * scale));           //   horizontal
                    gc.strokeLine(centerX + (i * scale), 0, centerX + (i * scale), canvasHeight);           //   vertical
                }
                gc.setFill(COLORS.main_grid);
                gc.setStroke(COLORS.main_grid);
                if (scale > 129){
                    gc.strokeLine(centerX + (i * scale) - scale / 2, centerY - 3, centerX + (i * scale) - scale / 2, centerY + 3);
                    gc.strokeLine(centerX - 3, centerY + (i * scale), centerX + 3, centerY + (i * scale));
                }
            }
        }

        for (int i = (int) -startendXY; i < startendXY + 1;) {
            if (scale < 6) i += 8;
            else if (scale < 10) i += 4;
            else if (scale < 20) i += 2;
            else i++;

            gc.setFill(COLORS.grid1);
            gc.setStroke(COLORS.grid1);
            gc.setLineWidth(2);

            if (i != 0) {
                if (scale > 35) gc.setLineWidth(2);
                else gc.setLineWidth(1);
                gc.strokeLine(centerX-centerX, centerY + (i * scale), canvasWidth, centerY + (i * scale));    //   horizontal
                gc.strokeLine(centerX + (i * scale), centerY-centerY, centerX + (i * scale), canvasHeight);   //   vertical

                gc.setFill(COLORS.main_grid);
                gc.setStroke(COLORS.main_grid);
                gc.setLineWidth(2);

                gc.strokeLine(centerX + (i*scale), centerY - 4, centerX + (i*scale), centerY+4);   //   horizontal
                gc.strokeLine(centerX - 4, centerY+(i*scale), centerX+4, centerY+(i*scale));       //   vertical
            }

            if (i != 0) gc.fillText(String.valueOf(i * (-1)), centerX + 12, centerY+(i * scale) + 5);
            if (i == 0) gc.fillText(String.valueOf(i * (-1)), centerX + 5, centerY+(i * scale) + 15);
            else gc.fillText(String.valueOf(i), centerX+(i*scale)-5, centerY + 20);
        }

        gc.strokeLine(centerX,centerY + (-startendXY * scale) - 10,centerX,centerY + (startendXY * scale) + 5);
        gc.strokeLine(centerX + (-startendXY * scale) - 5,centerY,centerX + (startendXY * scale) + 10, centerY);

        if (!(chordsY.isEmpty() || chordsX.isEmpty())){
            double lx = chordsX.get(0);
            double ly = chordsY.get(0);
            double x;
            double y;
            for (int i = 1; i <= chordsY.size() - 1; i++) {
                x = chordsX.get(i);
                y = chordsY.get(i);
                drawLineWithCords(canvas, lx, ly, x, y);
                lx = x;
                ly = y;
            }
        }

        if (Controller.isMousePressed){
            double dotX = -(centerX-mouseX)/scale;
            double dotY = (centerY-mouseY)/scale;

            dotX *= 100;
            dotX = Math.round(dotX);
            dotX /= 100;

            dotY *= 1000;
            dotY = Math.round(dotY);
            dotY /= 1000;

            String chords = "x: "+String.valueOf(dotX)+" y: "+String.valueOf(dotY);
            gc.setStroke(COLORS.grid1);
            gc.setFill(Color.valueOf("#2b2b2b"));
            gc.fillRoundRect(mouseX+10, mouseY+10, chords.length()*7, 20, 10, 10);
            gc.setFill(Color.WHITE);
            gc.fillText(chords, mouseX+28, mouseY+25);
        }

    }

    private static void drawLineWithCords(Canvas canvas, double x1, double y1, double x2, double y2){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(COLORS.function);
        gc.strokeLine(centerX + (x1 * scale), centerY -(y1 * scale), centerX + (x2 * scale), centerY - (y2 * scale));
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void round(double number, int count){

        for (int i = 0; i < count; i++) {

        }

    }
}


