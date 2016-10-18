/**
 * Created by zxx_1 on 2016/9/18.
 */
public class Test {
    public Test() {

    }
    public String completeMultiplication(String expression) {
        String innerString = expression.replaceAll("([\\)|\\d])([\\(||\\w])", "$1*$2");
        return innerString.replaceAll("(\\w)(\\()", "$1*$2");
    }
}
