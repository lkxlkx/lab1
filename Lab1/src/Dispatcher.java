/**
 * Created by zxx_1 on 2016/9/18.
 */
public class Dispatcher {
    Dispatcher() {
        expression = new Expression();
        commandRecognizer = new CommandRecognizer();
    }
    public void receiveInputString(String inputString) {
        this.inputString = inputString;
        commandRecognizer.recognise(inputString);
        testForNextLoop();
        dispatch();
    }
    public String outputString;                                         // Êä³öµÄ×Ö·û´®
    public boolean readyForNextLoop;
    private String inputString;
    private Expression expression;
    private CommandRecognizer commandRecognizer;
    private void testForNextLoop() {
        if (commandRecognizer.inputType == InputType.End)
            readyForNextLoop = false;
        else
            readyForNextLoop = true;
    }
    private void dispatch() {
        if (commandRecognizer.inputType == InputType.Expression) {
            try {
                expression.compile(inputString);
                outputString = expression.toString();
            } catch (ExpressionCompileException e) {
                outputString = e.getMessage();
            }
            return;
        }
        if (commandRecognizer.inputType == InputType.Derivation) {
            if (expression.isCompiled()) {
                String variable = commandRecognizer.operand.replaceAll("\\s", "");
                if (expression.hasVariable(variable)) {
                    outputString = expression.derivate(variable);
                }
                else
                    outputString = "No such variable in this expression.";
            }
            else
                outputString = "No valid Expression has been given.";
        }
        if (commandRecognizer.inputType == InputType.Simplification) {
            if (expression.isCompiled()) {
                outputString = expression.simplify(commandRecognizer.operand);
            }
            else
                outputString = "No such variable in this expression.";
        }
        //TODO
    }
}
