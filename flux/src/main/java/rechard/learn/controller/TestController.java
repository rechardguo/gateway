package rechard.learn.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Rechard
 **/
@RestController
public class TestController {

    @Value("${url}")
    private String url;

    private final WebClient client = WebClient.create();

    @GetMapping(value = "/reactor/{times}")
    public Mono<String> reactor(@PathVariable int times) {
        return Mono.just(times)
                .flatMap(t -> client.get()
                        .uri(url + "/hello/" + times).retrieve().bodyToMono(String.class));
    }
}
