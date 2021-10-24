package rechard.learn.reactivecode.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * stream流编程2
 * @author Rechard
 **/
public class StreamDemo2 {


    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.parallelStream();
        list.stream();

        //数组创建
        Arrays.stream(new int[]{1,2,3});

         //创建数字流
        IntStream.of(1,2,3);
        IntStream.rangeClosed(1,30)
;

        //random创建无线流
        //limit 10表示10个终止
        new Random().ints().limit(10);

        Random random = new Random();
        //自己产生流
        Stream<Integer> limit = Stream.generate(() -> random.nextInt()).limit(20);
        System.out.println(limit.count());
    }
}
