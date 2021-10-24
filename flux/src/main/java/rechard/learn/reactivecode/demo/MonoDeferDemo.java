package rechard.learn.reactivecode.demo;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 *Mono.defer创建的数据源时间相应的延迟了5秒钟，原因在于Mono.just会在声明阶段构造Date对象，只创建一次，但是Mono.defer却是在subscribe阶段才会创建对应的Date对象，每次调用subscribe方法都会创建Date对象
 *
 * 观察下面结果
 *
 * Mono的学习使用defer的使用
 * @author Rechard
 **/
public class MonoDeferDemo {
    private static Scheduler fixedPool= Schedulers.newParallel("soul-work-threads", (Runtime.getRuntime().availableProcessors() << 1) + 1);
    public static void main(String[] args) throws Exception {

        Mono<Date> m1 = Mono.just(new Date());
        //订阅才生效
        Mono<Date> m2 = Mono.defer(()->Mono.just(new Date()));
        m1.subscribe(System.out::println);
        m2.subscribe(System.out::println);
        //延迟5秒钟
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        m1.subscribe(System.out::println);
        m2.subscribe(System.out::println);
    }


}
