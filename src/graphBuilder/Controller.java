/*
    2*sqrt((-abs(abs(x)-1))*abs(3-abs(x))/((abs(x)-1)*(3-abs(x))))*(1+abs(abs(x)-3))*sqrt(1-pow2(x/7))+(5+0.97*(abs(x-0.5)+abs(x+0.5))-3*(abs(x-0.75)+abs(x+0.75)))*(1+abs(1-abs(x))/(1-abs(x)))
    (-3)*sqrt(1-pow2(x/7))*sqrt(abs(abs(x)-4)/(abs(x)-4))


    (2.71052+(1.5-0.5*abs(x))-1.35526*sqrt(4-pow2(abs(x)-1)))*sqrt(abs(abs(x)-1)/(abs(x)-1))+0.9
*/

package graphBuilder;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static graphBuilder.Main.*;
import static graphBuilder.Calculator.*;


public class Controller {
    public TextField funcField;
    public Label labelOfMessages;
    public Slider zoomSlider;
    public BorderPane mainPane;
    public ProgressIndicator loading;
    public Button buttonAnim;
    @FXML Canvas mainCanvas;
    static boolean isMousePressed;

    private static BorderPane mainPaneStat;

    public void initialize(){
        mainPaneStat = mainPane;
        mainCanvas.setCursor(new ImageCursor(new Image(Main.class.getResourceAsStream( "resources/cursor.png" )), 16, 16));
        funcField.setText("2");
    }

    public void clickBuild(){
        try {
            if (funcField.getText().equals("")) return;
            String radiusStr = funcField.getText();
            coordsX.clear();
            coordsY.clear();

            double x;
            double y;
            double r = Ideone.calc(ExpressionParser.parse(radiusStr));
            if (!ExpressionParser.flag){
                switch (ExpressionParser.error){
                    case bracketsNotMatched:
                        showError("Скобки не согласованы!", "err");
                        break;
                    case incorrectExpression:
                        showError("Некорректное выражение", "err");
                        break;
                }
                return;
            }

            for (double t = 0; t <= 360; t += delta) {

                x = 2 * r * Math.cos(Math.toRadians(t)) - r * Math.cos(Math.toRadians(2 * t));
                y = 2 * r * Math.sin(Math.toRadians(t)) - r * Math.sin(Math.toRadians(2 * t));

                x = round(x, 4);
                y = round(y, 4);
                coordsX.add(x);
                coordsY.add(y);
            }
            Main.draw(mainCanvas);
        } catch (Exception e) {
            showError(e.toString(), "err");
        }

    }

    static void showError(String message, String type){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        switch (type){
            case "err":
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle("Произошла ошибка!");
                alert.setHeaderText("Операция не может быть выполнена!");
                break;
            case "msg":
                alert.setTitle("Сообщение");
                alert.setHeaderText("Сообщение:");
                break;
            default:
                break;
        }
        alert.setContentText(message);
        alert.show();
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
        if (mouseEvent.isShiftDown()){
            centerX = mainCanvas.getWidth() / 2;
            centerY = mainCanvas.getHeight() / 2;
        }
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


    private Circle circle = new Circle();
    private TranslateTransition transition = new TranslateTransition(Duration.millis(1), circle);
    private int countMove = 1;

    private double getXByCoordinates(double x){
        double centerXRoot = mainCanvas.getLayoutX() + centerX;
        return centerXRoot + (x*scale);
    }

    private double getYByCoordinates(double y){
        double centerYRoot = mainCanvas.getLayoutY() + centerY;
        return centerYRoot - (y*scale);
    }

    public void animationClick() {
        if (coordsX.isEmpty()){
            showError("Нет графика!", "err");
            return;
        }
        loading.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        buttonAnim.setDisable(true);
        countMove = 1;
        circle.setRadius(25);
        circle.setCenterX(coordsX.get(0));
        circle.setCenterY(coordsY.get(0));
        circle.setFill(Color.RED);
        mainPane.getChildren().add(circle);
        moveTo(getXByCoordinates(coordsX.get(countMove)), getYByCoordinates(coordsY.get(countMove)));
    }

    private void moveTo(double x, double y){
        if (countMove >= coordsX.size()-1){
            mainPane.getChildren().remove(circle);
            buttonAnim.setDisable(false);
            loading.setProgress(1);
            return;
        }
        transition.setToX(x-circle.getCenterX());
        transition.setToY(y-circle.getCenterY());
        transition.play();
        transition.setOnFinished(event -> moveTo(getXByCoordinates(coordsX.get(countMove)), getYByCoordinates(coordsY.get(countMove))));
        countMove++;
    }
}
