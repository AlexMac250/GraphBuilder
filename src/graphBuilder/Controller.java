/*
    2*sqrt((-abs(abs(x)-1))*abs(3-abs(x))/((abs(x)-1)*(3-abs(x))))*(1+abs(abs(x)-3))*sqrt(1-pow2(x/7))+(5+0.97*(abs(x-0.5)+abs(x+0.5))-3*(abs(x-0.75)+abs(x+0.75)))*(1+abs(1-abs(x))/(1-abs(x)))
    (-3)*sqrt(1-pow2(x/7))*sqrt(abs(abs(x)-4)/(abs(x)-4))


    (2.71052+(1.5-0.5*abs(x))-1.35526*sqrt(4-pow2(abs(x)-1)))*sqrt(abs(abs(x)-1)/(abs(x)-1))+0.9
*/

package graphBuilder;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static graphBuilder.Main.*;
import static graphBuilder.Calculator.*;


public class Controller {
    public TextField funcField;
    public Label labelOfMessages;
    public Slider zoomSlider;
    public BorderPane mainPane;
    @FXML Canvas mainCanvas;
    static boolean isMousePressed;

    private static BorderPane mainPaneStat;

    public void initialize(){
        mainPaneStat = mainPane;
    }

    public void clickBuild(){
        if (funcField.getText().equals("")) return;
        String func = funcField.getText();
        chordsX.clear();
        chordsY.clear();

        CharSequence value = "x";

        for (double x = -startendXY; x < startendXY; x += delta) {
            x *= 100;
            x = Math.round(x);
            x /= 100;
            List<String> polsk = ExpressionParser.parse(func.replace(value,String.valueOf(x)));
            if (!ExpressionParser.flag) {
                switch (ExpressionParser.error){
                    case bracketsNotMatched:
                        showError("Скобки в заданном выражении не согласованы", "err");
                        break;
                    case incorrectExpression:
                        showError("Некорректное выражение", "err");
                        break;

                }
                ((Lighting) funcField.getEffect()).getLight().setColor(COLORS.wrong_color);
                return;
            }
            if(!func.contains(value)) {
                showError(func+" = "+Ideone.calc(ExpressionParser.parse(func)), "Пример решён)");
                return;
            }
            double y = Ideone.calc(polsk);

            y *= 100;
            y = Math.round(y);
            y /= 100;

            chordsX.add(x);
            chordsY.add(y);
        }
        for (int i = 0; i < chordsX.size(); i++) {
            System.out.println("x="+chordsX.get(i)+", y="+chordsY.get(i));
        }
        Main.draw(mainCanvas);
    }

    public void zoom() {
        Main.startendXY = (int) map(scale, zoomSlider.getMin(), zoomSlider.getMax(), 1000, 10);
        Main.scale = (int) zoomSlider.getValue();
        Main.draw(mainCanvas);
    }

    static void showError(String message, String type){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting native LAF: " + e);
        }
        mainPaneStat.setDisable(true);
        JFrame frame = new JFrame();
        final JDialog frMess = new JDialog(frame);
        final JPanel panel = new JPanel();
        final JPanel panel1 = new JPanel();
        final JLabel text = new JLabel(message);
        final JButton butOK = new JButton("Закрыть");
        switch (type){
            case "err":
                frMess.setTitle("Ошибка!");
                break;
            case "msg":
                frMess.setTitle("Сообщение");
                break;
            default:
                frMess.setTitle(type);
                break;
        }
        frMess.setResizable(false);
        frMess.setAlwaysOnTop(true);
        frMess.add(panel, BorderLayout.NORTH);
        frMess.add(panel1, BorderLayout.SOUTH);
        panel.add(text, BorderLayout.WEST);
        panel1.add(butOK, BorderLayout.EAST);
        frMess.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        butOK.addActionListener(e -> {
            frame.dispose();
            frMess.dispose();
            mainPaneStat.setDisable(false);
        });
        frMess.pack();
        frMess.setSize(new Dimension(frMess.getWidth()+70, frMess.getHeight()+20));
        frMess.setLocationRelativeTo(null);
        frMess.setVisible(true);
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
