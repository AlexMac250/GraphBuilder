//package graphBuilder;
//
//import javafx.application.Application;
//import javafx.beans.binding.Bindings;
//import javafx.geometry.*;
//import javafx.scene.Scene;
//import javafx.scene.chart.NumberAxis;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.*;
//import javafx.stage.Stage;
//
//import java.util.function.Function;
//
//// Java 8 code
//public class Main extends Application {
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(final Stage stage) {
//        Axes axes = new Axes(400, 300, -8, 8, 1, -6, 6, 1);
//
//        Plot plot = new Plot(x -> .25 * (x + 4) * (x + 1) * (x - 2), -8, 8, 0.1, axes);
//
//        StackPane layout = new StackPane(plot);
//        layout.setPadding(new Insets(20));
//        layout.setStyle("-fx-background-color: rgb(35, 39, 50);");
//
//        stage.setTitle("y = \u00BC(x+4)(x+1)(x-2)");
//        stage.setScene(new Scene(layout, Color.rgb(35, 39, 50)));
//        stage.show();
//    }
//
//    class Axes extends Pane {
//        private NumberAxis xAxis;
//        private NumberAxis yAxis;
//
//        public Axes(int width, int height, double xLow, double xHi, double xTickUnit, double yLow, double yHi, double yTickUnit) {
//            setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
//            setPrefSize(width, height);
//            setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
//
//            xAxis = new NumberAxis(xLow, xHi, xTickUnit);
//            xAxis.setSide(Side.BOTTOM);
//            xAxis.setMinorTickVisible(false);
//            xAxis.setPrefWidth(width);
//            xAxis.setLayoutY(height / 2);
//
//            yAxis = new NumberAxis(yLow, yHi, yTickUnit);
//            yAxis.setSide(Side.LEFT);
//            yAxis.setMinorTickVisible(false);
//            yAxis.setPrefHeight(height);
//            yAxis.layoutXProperty().bind(Bindings.subtract((width / 2) + 1, yAxis.widthProperty()));
//
//            getChildren().setAll(xAxis, yAxis);
//        }
//
//        public NumberAxis getXAxis() {
//            return xAxis;
//        }
//
//        public NumberAxis getYAxis() {
//            return yAxis;
//        }
//    }
//
//    class Plot extends Pane {
//        public Plot(Function<Double, Double> f, double xMin, double xMax, double xInc, Axes axes) {
//            Path path = new Path();
//            path.setStroke(Color.ORANGE.deriveColor(0, 1, 1, 0.6));
//            path.setStrokeWidth(2);
//
//            path.setClip(new Rectangle(0, 0, axes.getPrefWidth(), axes.getPrefHeight()));
//
//            double x = xMin;
//            double y = f.apply(x);
//
//            path.getElements().add(new MoveTo(mapX(x, axes), mapY(y, axes)));
//
//            x += xInc;
//            while (x < xMax) {
//                y = f.apply(x);
//
//                path.getElements().add(new LineTo(mapX(x, axes), mapY(y, axes)));
//
//                x += xInc;
//            }
//
//            setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
//            setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
//            setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
//
//            getChildren().setAll(axes, path);
//        }
//
//        private double mapX(double x, Axes axes) {
//            double tx = axes.getPrefWidth() / 2;
//            double sx = axes.getPrefWidth() / (axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound());
//
//            return x * sx + tx;
//        }
//
//        private double mapY(double y, Axes axes) {
//            double ty = axes.getPrefHeight() / 2;
//            double sy = axes.getPrefHeight() / (axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound());
//
//            return -y * sy + ty;
//        }
//    }
//}

package graphBuilder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    static GraphicsContext gc;
    Canvas canvas;
    private static double canvasHeight;
    private static double canvasWidth;
    static double centerX;
    static double centerY;
    static double delta = 0.01;
    static double startendX = 100;
    static int scale = 30;

    static ArrayList<Double> chordsY = new ArrayList<>();
    static ArrayList<Double> chordsX = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Graph Builder by Aleksandr Vasilenko");
        primaryStage.setScene(new Scene(root, 810, 540));
        primaryStage.setMinWidth(655);
        primaryStage.setMinHeight(600);
        canvas = ((Canvas) root.lookup("#mainCanvas"));
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        centerX = canvasWidth/2;
        centerY = canvasHeight/2;
        reDraw(canvas);
        primaryStage.show();
        primaryStage.widthProperty().addListener(e -> onResize(primaryStage));
        primaryStage.heightProperty().addListener(e -> onResize(primaryStage));
        primaryStage.fullScreenProperty().addListener(e -> onResize(primaryStage));
        onResize(primaryStage);
        gc = canvas.getGraphicsContext2D();
    }

    private void onResize(Stage primaryStage){
        canvas.setWidth(primaryStage.getWidth());
        canvas.setHeight(primaryStage.getHeight()-100);
        reDraw(canvas);
    }

    static void reDraw(Canvas canvas){
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        gc.setFill(COLORS.background);
        gc.fillRect(0,0,canvasWidth, canvasHeight);
        gc.setFill(COLORS.btn1);
        gc.setStroke(COLORS.btn1);
        gc.setLineWidth(2);
        gc.setLineCap(StrokeLineCap.SQUARE);
        gc.strokeLine(centerX,20,centerX,canvasHeight-20);
        gc.strokeLine(centerX+(-startendX*scale)-5,centerY,centerX+(startendX*scale)+10,centerY);
        gc.setLineWidth(2);

        for (int i = (int) -startendX; i < startendX + 1;) {
            if (scale < 10) i+=4;
            else if (scale < 20) i+=2;
            else i++;
            gc.strokeLine(centerX+(i*scale), centerY-4, centerX+(i*scale), centerY+4);
            gc.fillText(String.valueOf(i), centerX+(i*scale)+5, centerY+15);
            gc.strokeLine(centerX-4, centerY+(i*scale), centerX+4, centerY+(i*scale));
            if (i != 0) gc.fillText(String.valueOf(i*(-1)), centerX+12, centerY+(i*scale)+5);
        }

        if (scale > 35) {
            gc.setLineWidth(1);
            for (double i = -startendX; i < startendX + 1; i += 0.1) {
                gc.strokeLine(centerX + (i * scale), centerY - 3, centerX + (i * scale), centerY + 3);
                gc.strokeLine(centerX - 3, centerY + (i * scale), centerX + 3, centerY + (i * scale));
            }
        }


        if (chordsY.isEmpty() || chordsX.isEmpty()){
            return;
        }
        double lx = chordsX.get(0);
        double ly = chordsY.get(0);
        double x;
        double y;
        for (int i = 1; i <= chordsY.size()-1; i++) {
            x = chordsX.get(i);
            y = chordsY.get(i);
            drawLineWithCords(canvas, lx, ly, x, y);
            lx = x;
            ly = y;
        }
    }

    static void drawLineWithCords(Canvas canvas, double x1, double y1, double x2, double y2){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(COLORS.main_color);
        gc.strokeLine(centerX+(x1*scale), centerY-(y1*scale), centerX+(x2*scale), centerY-(y2*scale));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class ExpressionParser {

    enum ERROR { bracketsNotMatched, incorrectExpression }

    private static String operators = "+-*/";
    private static String delimiters = "() " + operators;
    static boolean flag = true;
    static ERROR error = null;

    private static boolean isDelimiter(String token) {
        if (token.length() != 1) return false;
        for (int i = 0; i < delimiters.length(); i++) {
            if (token.charAt(0) == delimiters.charAt(i)) return true;
        }
        return false;
    }

    private static boolean isOperator(String token) {
        if (token.equals("u-")) return true;
        for (int i = 0; i < operators.length(); i++) {
            if (token.charAt(0) == operators.charAt(i)) return true;
        }
        return false;
    }

    private static boolean isFunction(String token) {
        if (token.equals("sqrt") || token.equals("cube") || token.equals("pow2")) return true;
        return false;
    }

    private static int priority(String token) {
        if (token.equals("(")) return 1;
        if (token.equals("+") || token.equals("-")) return 2;
        if (token.equals("*") || token.equals("/")) return 3;
        return 4;
    }

    public static List<String> parse(String infix) {
        List<String> postfix = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        StringTokenizer tokenizer = new StringTokenizer(infix, delimiters, true);
        String prev = "";
        String curr = "";
        while (tokenizer.hasMoreTokens()) {
            curr = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens() && isOperator(curr)) {
                System.out.println("Некорректное выражение.");
                error = ERROR.incorrectExpression;
                flag = false;
                return postfix;
            }
            if (curr.equals(" ")) continue;
            if (isFunction(curr)) stack.push(curr);
            else if (isDelimiter(curr)) {
                if (curr.equals("(")) stack.push(curr);
                else if (curr.equals(")")) {
                    while (!stack.peek().equals("(")) {
                        postfix.add(stack.pop());
                        if (stack.isEmpty()) {
                            System.out.println("Скобки не согласованы.");
                            error = ERROR.bracketsNotMatched;
                            flag = false;
                            return postfix;
                        }
                    }
                    stack.pop();
                    if (!stack.isEmpty() && isFunction(stack.peek())) {
                        postfix.add(stack.pop());
                    }
                } else {
                    if (curr.equals("-") && (prev.equals("") || (isDelimiter(prev)  && !prev.equals(")")))) {
                        // унарный минус
                        curr = "u-";
                    } else {
                        while (!stack.isEmpty() && (priority(curr) <= priority(stack.peek()))) {
                            postfix.add(stack.pop());
                        }

                    }
                    stack.push(curr);
                }

            }

            else {
                postfix.add(curr);
            }
            prev = curr;
        }

        while (!stack.isEmpty()) {
            if (isOperator(stack.peek())) postfix.add(stack.pop());
            else {
                System.out.println("Скобки не согласованы.");
                error = ERROR.bracketsNotMatched;
                flag = false;
                return postfix;
            }
        }
        return postfix;
    }
}

class Ideone {
    public static Double calc(List<String> postfix) {
        Deque<Double> stack = new ArrayDeque<Double>();
        for (String x : postfix) {
            System.out.print(x);
            if (x.equals("sqrt")) stack.push(Math.sqrt(stack.pop()));
            else if (x.equals("cube")) {
                Double tmp = stack.pop();
                stack.push(tmp * tmp * tmp);
            }
            else if (x.equals("pow2")) stack.push(Math.pow(2, stack.pop()));
            else if (x.equals("+")) stack.push(stack.pop() + stack.pop());
            else if (x.equals("-")) {
                Double b = stack.pop(), a = stack.pop();
                stack.push(a - b);
            }
            else if (x.equals("*")) stack.push(stack.pop() * stack.pop());
            else if (x.equals("/")) {
                Double b = stack.pop(), a = stack.pop();
                stack.push(a / b);
            }
            else if (x.equals("u-")) stack.push(-stack.pop());
            else stack.push(Double.valueOf(x));
        }
        return stack.pop();
    }
}
