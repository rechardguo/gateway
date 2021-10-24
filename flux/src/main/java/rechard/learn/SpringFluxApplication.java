package rechard.learn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * @author Rechard
 **/
@SpringBootApplication
public class SpringFluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringFluxApplication.class, args);
    }

}
