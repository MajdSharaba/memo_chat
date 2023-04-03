package com.yawar.memo;

import org.junit.Test;

import static org.junit.Assert.*;

import com.yawar.memo.ui.dashBoard.MyClass;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        assertEquals(4,  2);
    }
    @Test
    public void addition_Check() throws Exception {
        MyClass myClass = new MyClass();
        int result = myClass.add(2, 2);
        int expected = 4;
        assertEquals(expected, result);
    }
}