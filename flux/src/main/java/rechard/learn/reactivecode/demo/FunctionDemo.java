package rechard.learn.reactivecode.demo;

import org.aopalliance.intercept.MethodInterceptor;

import java.text.DecimalFormat;
import java.util.function.Function;

/**
 * 函数式编程  区别于 命令式编程
 *
 * 例子主要是 jdk1.8的函数接口使用
 * Function
 * @author Rechard
 **/
public class FunctionDemo {

    static interface MoneyFormat{
        String fomat(float m);
    }


    static class Money{
        private float val;

        public Money(float val) {
            this.val = val;
        }
        public String print(MoneyFormat format){
            return format.fomat(this.val);
        }

        //简化不需要写接口MoneyFormat
        public String print2(Function<Float,String> format){
            return format.apply(this.val);
        }
    }

    public static void main(String[] args) {
        String val = new Money(100.032323f).print(m -> {
            return new DecimalFormat("##.###").format(m);
        });
        System.out.println(val);

        String val2 = new Money(100.032323f).print2(m -> {
            return new DecimalFormat("##.###").format(m);
        });
        System.out.println(val2);

        //除了简化不使用接口，还可以支持链式编程
        Function f = m -> {
            return new DecimalFormat("##.###").format(m);
        };
        String val3 = new Money(100.032323f).print2(f.andThen(s->"RMB:"+s));
        System.out.println(val3);
    }
}
