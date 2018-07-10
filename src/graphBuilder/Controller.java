package graphBuilder;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

import java.util.List;

import static graphBuilder.Main.*;


public class Controller {
    public TextField funcField;
    public Label labelOfMessages;
    public Slider zoomSlider;
    @FXML Canvas mainCanvas;

    private static GraphicsContext gc;
    public void initialize(){
        gc = mainCanvas.getGraphicsContext2D();
    }

    public void clickBuild(){
        String func = funcField.getText();
        chordsX.clear();
        chordsY.clear();
        for (double i = -startendXY; i < startendXY; i+=delta) {
            chordsX.add(i);
            chordsY.add(/*FUNCTION*/ Math.sin(i));
        }
        labelOfMessages.setText("");
        Main.draw(mainCanvas);
        if (funcField.getText().equals("")) return;
        List<String> expression = ExpressionParser.parse(funcField.getText());
        boolean flag = ExpressionParser.flag;
        if (flag) {
            labelOfMessages.setText(funcField.getText()+" = "+String.valueOf(Ideone.calc(expression)));
//            System.out.println();
//            System.out.println(Ideone.calc(expression));
        } else {
            switch (ExpressionParser.error){
                case bracketsNotMatched:
                    labelOfMessages.setText("Скобки в заданном выражении не согласованы");
                    break;
                case incorrectExpression:
                    labelOfMessages.setText("Некорректное выражение");
                    break;

            }
            ((Lighting) funcField.getEffect()).getLight().setColor(COLORS.wrong_color);
        }
        //gc.fillText(funcField.getText()+" = "+String.valueOf(Ideone.calc(ExpressionParser.parse(funcField.getText()))), 10, centerY*2-20);
        Main.draw(mainCanvas);
    }

    private String convert(List<String> postfix){
        StringBuilder res = new StringBuilder();
        for (String s : postfix) {
            res.append(s);
        }
        return res.toString();
    }

    public void zoom() {
        Main.startendXY = (int) map(scale, zoomSlider.getMin(), zoomSlider.getMax(), 1000, 10);
        Main.scale = (int) zoomSlider.getValue();
        Main.draw(mainCanvas);
    }

    public void showInfo(){
        System.out.println("It works!");
    }

    double lxM;
    double lyM;
    public void move(MouseEvent mouseEvent) {
        double x = mouseEvent.getScreenX();
        double y = mouseEvent.getScreenY();
        centerX -= lxM-x;
        centerY -= lyM-y;
        lxM = x;
        lyM = y;
        Main.draw(mainCanvas);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        lxM = mouseEvent.getScreenX();
        lyM = mouseEvent.getScreenY();
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

    double map(long x, double in_min, double in_max, long out_min, long out_max) {
        return ((x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }

}
