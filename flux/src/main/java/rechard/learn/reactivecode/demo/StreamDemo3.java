package rechard.learn.reactivecode.demo;

import java.util.stream.IntStream;

/**
 * stream流
 * 中间操作：
 * 无状态操作
 *
 * 有状态操作
 *
 * @author Rechard
 **/
public class StreamDemo3 {
    public static void main(String[] args) {
        int[] nums=new int[]{1,2,3};
        int sum = IntStream.of(nums).map(StreamDemo3::doubleValue).sum();
        System.out.println("结果为"+sum);
        System.out.println("没有执行sum，则中间操作不执行");
        IntStream.of(nums).map(StreamDemo3::doubleValue);

    }

    public static int doubleValue(int i){
        System.out.println("执行了i*i");
        return i*i;
    }
}
