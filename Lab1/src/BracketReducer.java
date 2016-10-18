import java.util.ArrayList;
/**
 * Created by zxx_1 on 2016/9/18.
 */
public class BracketReducer {
    public BracketReducer(String expression) throws ExpressionCompileException {
        this.expression = expression;
        resultTerms = breakAtPlusAndSub(expression);
    }
    public ArrayList<Term> resultTerms;
    public String toString(){
        String result = "";
        int len = resultTerms.size();
        for (int i = 0;i<len;i++) {
            if (i == 0 && resultTerms.get(i).sign == 1) {
                result += resultTerms.get(i).content;
            }
            else {
                if (resultTerms.get(i).sign == 1) result += "+";
                else result += "-";
                result += resultTerms.get(i).content;
            }
        }
        return result;
    }
    private String expression;
    private ArrayList<Term> breakAtPlusAndSub(String exp) throws ExpressionCompileException{   // 对一个由加减连接各个项的式子进行处理
        ArrayList<Term> rel = new ArrayList<Term>();
        ArrayList<Integer> notation = divideAtLowLevel(exp);
        if (exp.charAt(0) == '-')
            return breakAtPlusAndSub("0" + exp);
        int len = notation.size();
        if (divideWithCharacters(exp,"+-*").size() == 1 && exp.charAt(0) == '(')            // 如果整个表达式由一个括号包络
            return breakAtPlusAndSub(exp.substring(1, exp.length() - 1));                     // 就剥去括号再进行函数操作
        for (int i = 0;i<len;i++) {
            int head = notation.get(i).intValue();
            int tail;
            if (i == len -1) {
                tail = exp.length();
            }
            else {
                tail = notation.get(i+1).intValue()-1;
            }
            String substr = exp.substring(head,tail);
            int signal;
            ArrayList<Term> temp = new ArrayList<Term>();
            if (substr.contains("(")) {
                temp = breakAtMultiplification(substr);
            }
            else {
                temp.add(new Term(substr,'\1'));
            }
            if (i != 0 && exp.charAt(notation.get(i).intValue() - 1) == '-') {
                int tlen = temp.size();
                for (int j = 0;j<tlen;j++) {
                    temp.get(j).sign = 1- temp.get(j).sign;
                }
            }
            rel.addAll(temp);
        }
        return rel;
    }
    private ArrayList<Term> breakAtMultiplification(String exp) throws ExpressionCompileException{
        ArrayList<Integer> Splits = divideAtHighLevel(exp);
        int len = Splits.size();
        if (divideWithCharacters(exp,"+-*").size() == 1 && exp.charAt(0) == '(')               // 如果整个表达式由一个括号包络
            return breakAtMultiplification(exp.substring(1, exp.length() - 1));                // 就剥去括号再进行函数操作
        ArrayList<Term> a = new ArrayList<Term>();
        ArrayList<Term> b,c = new ArrayList<Term>();
        a.add(new Term("1",'\1'));
        Term temp = new Term();
        String subStr;
        for (int i = 0;i<len;i++) {
            int head = Splits.get(i).intValue();
            int tail;
            if (i == len -1) {
                tail = exp.length();
            }
            else {
                tail = Splits.get(i+1).intValue()-1;
            }
            subStr = exp.substring(head,tail);
            b = breakAtPlusAndSub(subStr);
            a = multiplyTerm(a, b);
        }
        return a;

    }
    private ArrayList<Term> multiplyTerm(ArrayList<Term> a, ArrayList<Term> b) {
        ArrayList rel = new ArrayList<Term>();
        int lena = a.size(),lenb = b.size();
        Term temp;
        for (int i = 0;i<lena;i++) {
            for (int j = 0;j < lenb;j++) {
                temp = new Term();
                temp.content = a.get(i).content+"*"+b.get(j).content;
                if (a.get(i).sign != b.get(j).sign) {
                    temp.sign = '\0';
                }
                else {
                    temp.sign = '\1';
                }
                rel.add(temp);
            }
        }
        return rel;
    }
    private ArrayList<Integer> divideAtLowLevel(String inputString) throws ExpressionCompileException {
        return divideWithCharacters(inputString, "+-");
    }
    private ArrayList<Integer> divideAtHighLevel(String inputString) throws ExpressionCompileException {
        return divideWithCharacters(inputString, "*");
    }
    private ArrayList<Integer> divideWithCharacters(String inputString, String characters) throws ExpressionCompileException {
        int depth = 0;                                          // 括号嵌套深度
        int index = 0;
        ArrayList<Integer> result = new ArrayList<Integer>();
        result.add(0);
        while (index < inputString.length()) {
            if (characters.contains(inputString.substring(index, index + 1)) && depth == 0) {
                if (index != 0)
                    result.add(index+1);
            }
            if (inputString.charAt(index) == '(')
                depth++;
            if (inputString.charAt(index) == ')')
                depth--;
            if (depth < 0)
                throw new ExpressionCompileException("Brackets not match.");
            index++;
        }
        if (depth != 0)
            throw new ExpressionCompileException("Brackets not match.");
        return result;
    }
}
