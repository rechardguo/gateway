package rechard.learn.reactivecode.demo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Flux.just 接受多个参数
 * Mono.just 只能1个接受参数
 *
 * flatMap 异步
 * Map 同步
 *
 * Mono的学习使用
 * @author Rechard
 **/
public class MonoDemo {

    public static void main(String[] args) throws IOException {

        //Flux.just 接受多个参数
        //Flux.just("key1","key2","keys")
        //Mono.just 只能1个接受参数
        Mono.just("key1")
             .flatMap(k -> callExternalService(k)
                     .onErrorResume(throwable -> {
                         System.err.println("发送异常");
                         return null;
                     })
                     .doOnSuccess(resp->{
                         System.out.println(resp);
                     })
                     .doFinally(signalType -> {
                         System.out.println("发送完毕"+signalType);
                     })
             ).block();
        //如果发送异常，这里打印不出来
        //System.out.println("发送完成");
    }

    private static Mono<Object> callExternalService(String k) {
        long times=new Random().nextInt(3000);
        if(times>1500){
            return Mono.error(()->{
                return new TimeoutException();
            });
        }
        return Mono.delay(Duration.ofMillis(times)).thenReturn(k+"->Hello");
    }
}
