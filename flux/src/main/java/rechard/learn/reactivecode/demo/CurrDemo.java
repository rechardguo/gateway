package rechard.learn.reactivecode.demo;

import java.util.function.Function;

/**
 *
 * 级联表达式
 * 柯里化
 * @author Rechard
 **/
public class CurrDemo {

    public static void main(String[] args) {
        Function<Integer,Function<Integer,Integer>> f=x->y->x+y;
        System.out.println(f.apply(1).apply(2));
    }

}
