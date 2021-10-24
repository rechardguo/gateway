package rechard.learn.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @author Rechard
 **/
@RestController
public class HelloController {

    @GetMapping(value = "/hello/{times}")
    public Mono<String> hello(@PathVariable int times) {
        return Mono.delay(Duration.ofMillis(times)).thenReturn("Hello");
    }
}
