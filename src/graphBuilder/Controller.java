/*
    2*sqrt((-abs(abs(x)-1))*abs(3-abs(x))/((abs(x)-1)*(3-abs(x))))*(1+abs(abs(x)-3))*sqrt(1-pow2(x/7))+(5+0.97*(abs(x-0.5)+abs(x+0.5))-3*(abs(x-0.75)+abs(x+0.75)))*(1+abs(1-abs(x))/(1-abs(x)))
    (-3)*sqrt(1-pow2(x/7))*sqrt(abs(abs(x)-4)/(abs(x)-4))


    (2.71052+(1.5-0.5*abs(x))-1.35526*sqrt(4-pow2(abs(x)-1)))*sqrt(abs(abs(x)-1)/(abs(x)-1))+0.9
*/

package graphBuilder;

import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import static graphBuilder.Main.*;


public class Controller {
    public TextField funcField;
    public Label labelOfMessages;
    public Slider zoomSlider;
    public BorderPane mainPane;
    public ProgressIndicator loading;
    @FXML Canvas mainCanvas;
    static boolean isMousePressed;

    public void initialize(){
        mainCanvas.setCursor(new ImageCursor(new Image(Main.class.getResourceAsStream( "resources/cursor.png" )), 16, 16));
        funcField.setText("1");
    }

    public void clickBuild(){
        if (funcField.getText().equals("")) return;
        loading.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        String radiusStr = funcField.getText();
        chordsX.clear();
        chordsY.clear();

        double x;
        double y;
        double r = Double.parseDouble(radiusStr);

        for (double t = 0; t <= 360; t += delta) {
            x = 2*r*Math.cos(Math.toRadians(t)) - r*Math.cos(Math.toRadians(2*t));
            y = 2*r*Math.sin(Math.toRadians(t)) - r*Math.sin(Math.toRadians(2*t));
            x = round(x,2);
            y = round(y, 2);
            chordsX.add(x);
            chordsY.add(y);
            degs.add(t);
        }
        Main.draw(mainCanvas);
        loading.setProgress(1);

    }

    public void zoom() {
        Main.startEndXY = (int) map(scale, zoomSlider.getMin(), zoomSlider.getMax(), 1000, 10);
        Main.scale = (int) zoomSlider.getValue();
        Main.draw(mainCanvas);
    }

    private double lxM;
    private double lyM;
    public void move(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        centerX -= lxM-x;
        centerY -= lyM-y;
        lxM = x;
        lyM = y;
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
        Main.draw(mainCanvas);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        isMousePressed = true;
        lxM = mouseEvent.getX();
        lyM = mouseEvent.getY();
        mouseX = mouseEvent.getX();
        mouseY = mouseEvent.getY();
        draw(mainCanvas);
    }

    public void mouseReleased() {
        isMousePressed = false;
        draw(mainCanvas);
    }

    public void scroll(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() > 0){
            if (!(zoomSlider.getValue() == zoomSlider.getMax())) zoomSlider.setValue(zoomSlider.getValue()+8);
        } else {
            if (!(zoomSlider.getValue() == zoomSlider.getMin())) zoomSlider.setValue(zoomSlider.getValue()-8);
        }
        zoom();
    }

    public void clickFuncField() {
        ((Lighting) funcField.getEffect()).getLight().setColor(Color.valueOf("#ffffff"));
    }

    @SuppressWarnings("SameParameterValue")
    private double map(long x, double in_min, double in_max, long out_min, long out_max) {
        return ((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }

}
