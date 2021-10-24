package rechard.learn.reactivecode.demo;

import java.util.stream.IntStream;

/**
 * stream流
 * 中间操作：返回还是流
 *
 * 中止操作： 返回不是流
 * 惰性求值:中止操作没有指定的情况下，中间操作是不会执行的
 *
 * @author Rechard
 **/
public class StreamDemo {
    public static void main(String[] args) {
        int[] nums=new int[]{1,2,3};
        int sum = IntStream.of(nums).map(StreamDemo::doubleValue).sum();
        System.out.println("结果为"+sum);
        System.out.println("没有执行sum，则中间操作不执行");
        IntStream.of(nums).map(StreamDemo::doubleValue);

    }

    public static int doubleValue(int i){
        System.out.println("执行了i*i");
        return i*i;
    }
}
