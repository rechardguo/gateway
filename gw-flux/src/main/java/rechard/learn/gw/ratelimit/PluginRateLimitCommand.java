package rechard.learn.gw.ratelimit;

import org.springframework.http.server.reactive.ServerHttpRequest;
import rechard.learn.gw.classloader.PluginClassLoader;
import rechard.learn.gw.exception.AuthenticationException;
import rechard.learn.gw.exception.RateLimitException;
import rechard.learn.gw.plugin.AuthCommand;
import rechard.learn.gw.plugin.RateLimitCommand;
import sun.misc.Launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Rechard
 **/
public class PluginRateLimitCommand  implements RateLimitCommand {

    private static String pluginHome = "D:\\dev-code\\opensource-code\\springcloud_ry_code\\api-gateway-workspace\\gateway\\pluginjar";

    private String pluginJarName;
    private String pluginClassName;
    private PluginClassLoader classLoader;

    public PluginRateLimitCommand(String pluginJarName, String pluginClassName, PluginClassLoader classLoader) {
        this.pluginJarName = pluginJarName;
        this.pluginClassName = pluginClassName;
        this.classLoader = classLoader;
    }

    @Override
    public boolean isAllowed(ServerHttpRequest request) throws RateLimitException {

        File jarFile = new File(pluginHome, pluginJarName + ".jar");

        if (!jarFile.exists()) {
            throw new IllegalStateException("can not find arthas-core.jar under arthasHome: " + pluginHome);
        }
        try {
            Class clazz = classLoader.loadClass(pluginJarName, pluginClassName);
            AuthCommand cmd = (AuthCommand) clazz.newInstance();
            return cmd.doValid(request);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | MalformedURLException e) {
            //todo handle excpetion
            e.printStackTrace();
        }
        return false;
    }


}