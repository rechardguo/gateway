package rechard.learn.reactivecode.demo;

import org.springframework.http.converter.json.GsonBuilderUtils;

import java.text.DecimalFormat;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 * 函数式编程  区别于 命令式编程
 *  jdk 内置了很多的函数接口编程
 *
 * @author Rechard
 **/
public class FunctionDemo2 {


    public static void main(String[] args) {
       //1.断言 是否数字是负数
        // Predicate<Integer> numberNegtivePredicate=i->{return i<0;};

        IntPredicate numberNegtivePredicate=i->{return i<0;};
        System.out.println(numberNegtivePredicate.test(-1));


        //2.conumer
        Consumer s=str-> System.out.println(str+" world");
        s.accept("hello");
        //s.andThen(System.out::println);
    }
}
