import java.util.Comparator;

/**
 * Created by zxx_1 on 2016/9/19.
 */
public class CompareNumericTermByPowersHash implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        NumericTerm term1 = (NumericTerm) o1;
        NumericTerm term2 = (NumericTerm) o2;
        if (term1.powers.hashCode() > term2.powers.hashCode())
            return 1;
        return 0;
    }
}
