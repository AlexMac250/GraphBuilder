package graphBuilder;

import java.util.*;


class Calculator {
    enum ERROR { bracketsNotMatched, incorrectExpression}

    static class ExpressionParser {
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
            return token.equals("sqrt") || token.equals("cube") || token.equals("pow2") || token.equals("sin") || token.equals("cos") || token.equals("abs");
        }

        private static int priority(String token) {
            if (token.equals("(")) return 1;
            if (token.equals("+") || token.equals("-")) return 2;
            if (token.equals("*") || token.equals("/")) return 3;
            return 4;
        }

        static List<String> parse(String infix) {
            flag = true;
            List<String> postfix = new ArrayList<>();
            Deque<String> stack = new ArrayDeque<>();
            StringTokenizer tokenizer = new StringTokenizer(infix, delimiters, true);
            String prev = "";
            String curr;
            try {
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
                        switch (curr) {
                            case "(":
                                stack.push(curr);
                                break;
                            case ")":
                                assert stack.peek() != null;
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
                                break;
                            default:
                                if (curr.equals("-") && (prev.equals("") || (isDelimiter(prev) && !prev.equals(")")))) {
                                    // унарный минус
                                    curr = "u-";
                                } else {
                                    assert stack.peek() != null;
                                    while (!stack.isEmpty() && (priority(curr) <= priority(stack.peek()))) {
                                        postfix.add(stack.pop());
                                    }

                                }
                                stack.push(curr);
                                break;
                        }

                    } else {
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
            } catch (Exception e){
                Controller.showError(e.toString(), "Исключение!");
            }
            return postfix;
        }
    }

    static class Ideone {
        static Double calc(List<String> postfix) {
            Deque<Double> stack = new ArrayDeque<>();
            try {
                for (String x : postfix) {
                    switch (x) {
                        case "sqrt":
                            stack.push(Math.sqrt(stack.pop()));
                            break;
                        case "cube":
                            Double tmp = stack.pop();
                            stack.push(tmp * tmp * tmp);
                            break;
                        case "pow2":
                            stack.push(Math.pow(2, stack.pop()));
                            break;
                        case "sin":
                            stack.push(Math.sin(Math.toRadians(stack.pop())));
                            break;
                        case "cos":
                            stack.push(Math.cos(Math.toRadians(stack.pop())));
                            break;
                        case "abs":
                            stack.push(Math.abs(stack.pop()));
                            break;
                        case "+":
                            stack.push(stack.pop() + stack.pop());
                            break;
                        case "-": {
                            Double b = stack.pop(), a = stack.pop();
                            stack.push(a - b);
                            break;
                        }
                        case "*":
                            stack.push(stack.pop() * stack.pop());
                            break;
                        case "/": {
                            Double b = stack.pop(), a = stack.pop();
                            stack.push(a / b);
                            break;
                        }
                        case "u-":
                            stack.push(-stack.pop());
                            break;
                        default:
                            stack.push(Double.valueOf(x));
                            break;
                    }
                }
            } catch (Exception e){
                Controller.showError(e.toString(), "Исключение!");
            }
            return stack.pop();
        }
    }
}
