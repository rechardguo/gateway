package rechard.learn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import rechard.learn.gw.GWController;
import rechard.learn.gw.GWHandlerMapping;


/**
 * @author w.ao on 2020/7/6
 */
@SpringBootApplication
public class SpringMvcApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringMvcApplication.class, args);
    }


    @Bean
    public GWController gwController(){
        return new GWController();
    }

    @Bean
    public GWHandlerMapping gwHandlerMapping(GWController controller){
        return new GWHandlerMapping(controller);
    }
}
