package rechard.learn.gw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rechard.learn.gw.classloader.PluginClassLoader;
import rechard.learn.gw.classloader.PluginJarURLClassLoader;

/**
 * @author Rechard
 **/
@Configuration
public class GWConfig {

    @Bean
    public PluginClassLoader classLoader(){
        return new PluginClassLoader();
    }
}
