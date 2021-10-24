package rechard.learn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

/**
 * @author Rechard
 **/
@RestController
public class TestController {
    @Value("${url}")
    private String url;

   // @Autowired
    private RestTemplate restTemplate=new RestTemplate();


    @GetMapping(value = "/block/{times}")
    public String block(@PathVariable int times) {
        return restTemplate.getForObject(url + "/hello/" + times, String.class);
    }

    @GetMapping(value = "/async/{times}")
    public Callable<String> async(@PathVariable int times) {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                return restTemplate.getForObject(url + "/hello/" + times, String.class);
            }
        };
    }


}
