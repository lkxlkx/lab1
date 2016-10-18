import org.junit.*;

import static org.junit.Assert.*;

/**
 * Created by zxx_1 on 2016/9/18.
 */
public class TestTest {

    @org.junit.Test
    public void testCompleteMultiplication() throws Exception {
        Test foo = new Test();
        assertEquals("2*(3*a+b)", foo.completeMultiplication("2(3a+b)"));
    }

}