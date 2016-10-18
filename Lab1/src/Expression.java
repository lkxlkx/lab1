import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zxx_1 on 2016/9/18.
 */
public class Expression {
    public Expression() {
        variableList = new HashMap<String, Integer>();
        compileResults = new ArrayList<NumericTerm>();
        compiledMark = false;
    }
    public void compile(String expression) throws ExpressionCompileException{
        String innerString;
        innerString = blankStrip(expression);
        innerString = completeMultiplication(innerString);
        try {
            innerString = replacePowerNotion(innerString);
            reduceBracket(innerString);
            generateVariableList();
            transformIntoNumeric();
            mergeResults();
        } catch(ExpressionCompileException e) {
            throw e;
        }
        compiledMark = true;
    }
    public String toString() {                                              // 将numericTerm形式存储的多项式转化为字符串输出
        String result = transformNumericTermToString(compileResults.get(0));
        for(int i = 1; i < compileResults.size(); i++) {
            String termString = transformNumericTermToString(compileResults.get(i));
            if (!termString.startsWith("-"))                                // 若项前没有负号，则需要添加正号
                result += "+";
            result += termString;
        }
        return result;
    }
    public boolean isCompiled() {
        return compiledMark;
    }
    public boolean hasVariable() {
        // 待填充
    }
    public String derivate(String variable) {
        // 待填充
    }
    public String simplify(String assignments) {
        // 待填充
    }
    private boolean compiledMark;
    private ArrayList<Term> resultTerms;
    private HashMap<String, Integer> variableList;
    private int variableNumber;
    private ArrayList<NumericTerm> compileResults;
    private ArrayList<String> variableIndexToName;
    public static final double numericError = 1e-6;
    private String blankStrip(String expression) {
        return expression.replaceAll("\\s+", "");                           // 先去除所有的空格
    }
    private String completeMultiplication(String expression) {
        String innerString = expression.replaceAll("([\\)|\\d])([\\(||a-z||A-Z])", "$1*$2");
        return innerString.replaceAll("([a-zA-Z])(\\()", "$1*$2");
    }
    private String replacePowerNotion(String expression) throws ExpressionCompileException{
        PowerNotationReplacer powerNotationReplacer = new PowerNotationReplacer(expression);
        return powerNotationReplacer.getResult();
    }
    private void reduceBracket(String expression) throws ExpressionCompileException {
        BracketReducer bracketReducer = new BracketReducer(expression);
        resultTerms = bracketReducer.resultTerms;
    }
    private void generateVariableList() {
        variableIndexToName = new ArrayList<>();
        for(Term term : resultTerms)                                        // 对每一项
            for(String fragments : term.content.split("\\*"))               // 用乘号分隔开
                if (fragments.matches("[a-zA-Z]+") == true && variableIndexToName.contains(fragments) == false)
                    variableIndexToName.add(fragments);                     // 如果是未出现过的纯字母字串，则加入到变量名列表
        for(int i = 0; i < variableIndexToName.size(); i++)
            variableList.put(variableIndexToName.get(i), i);                // 添加字串至词典
        variableNumber = variableIndexToName.size();
    }
    private void transformIntoNumeric() throws ExpressionCompileException{
        for(Term term : resultTerms) {
            ArrayList<Integer> powers = new ArrayList<Integer>();
            for(int i = 0; i < variableNumber; i++)
                powers.add(0);                                               // 幂计数初始化
            double coefficient = 1.0d;                                          // 系数初始化，对于负项处理为-1
            if (term.sign == '\0')
                coefficient *= -1;
            for(String fragments : term.content.split("\\*")) {                 // 对于乘号分隔开的每一部分
                if (fragments.matches("[a-zA-Z]+")) {
                    int index = variableList.get(fragments);
                    powers.set(index, powers.get(index) + 1);
                }
                else
                    try {
                        coefficient *= Double.parseDouble(fragments);
                    } catch(Exception e) {
                        throw new ExpressionCompileException("Can not resolve this expression");
                    }
            }
            if (Math.abs(coefficient) > numericError)
                compileResults.add(new NumericTerm(coefficient, powers));
        }
    }

    private void mergeResults() {
        boolean modified = false;
        do {
            modified = false;
            compileResults.sort(new CompareNumericTermByPowersHash());
            for(int i = 0; i < compileResults.size()-1; i++) {
                NumericTerm formerTerm = compileResults.get(i);
                NumericTerm latterTerm = compileResults.get(i + 1);
                if (formerTerm.powers.equals(latterTerm.powers)) {              // 若有相邻项幂指数相同, 选择合并
                    double coefficientSum = formerTerm.coefficient + latterTerm.coefficient;
                    if (Math.abs(coefficientSum) < numericError) {              // 若系数和为0，则删去两项，否则用一项代替原来的两项
                        compileResults.remove(i + 1);
                        compileResults.remove(i);
                    } else {
                        compileResults.set(i, new NumericTerm(coefficientSum, latterTerm.powers));
                        compileResults.remove(i + 1);
                    }
                    modified = true;                                            // 记录此次的合并
                    break;                                                      // 数组序列结构已被破坏，应进行下一轮排序和循环
                }
            }
        } while(modified == true);
    }

    private String transformNumericTermToString(NumericTerm term) {
        String result = Double.toString(term.coefficient);                      // 将系数转化为字符串
        for(int i = 0; i < variableNumber; i++) {
            int power = term.powers.get(i);                                     // 该位置的对应指数
            if (power > 0)
                result += "*" + variableIndexToName.get(i);                     // 指数非零则添加变量名
            if (power > 1)
                result += "^" + Integer.toString(power);                        // 指数大于1则添加幂次
        }
        if (result.startsWith("1*"))
            return result.substring(2);                                         // 若为1*a形式，则省略前面的1
        return result;
    }


}
