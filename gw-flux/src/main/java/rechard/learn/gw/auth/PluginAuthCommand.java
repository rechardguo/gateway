package rechard.learn.gw.auth;

import org.springframework.http.server.reactive.ServerHttpRequest;
import rechard.learn.gw.classloader.PluginClassLoader;
import rechard.learn.gw.classloader.PluginJarURLClassLoader;
import rechard.learn.gw.exception.AuthenticationException;
import rechard.learn.gw.exception.UnknownServiceException;
import rechard.learn.gw.plugin.AuthCommand;
import sun.misc.Launcher;
import sun.tools.jar.resources.jar;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Rechard
 **/
public class PluginAuthCommand  implements AuthCommand {

    private static String pluginHome="D:\\dev-code\\opensource-code\\springcloud_ry_code\\api-gateway-workspace\\gateway\\pluginjar";

    private String pluginJarName;
    private String pluginClassName;
    private PluginClassLoader classLoader;

    public PluginAuthCommand(String pluginJarName, String pluginClassName, PluginClassLoader classLoader) {
        this.pluginJarName = pluginJarName;
        this.pluginClassName=pluginClassName;
        this.classLoader = classLoader;
    }

    @Override
    public boolean doValid(ServerHttpRequest request) throws AuthenticationException {

        File jarFile = new File(pluginHome, pluginJarName+".jar");

        if (!jarFile.exists()) {
            throw new IllegalStateException("can not find arthas-core.jar under arthasHome: " + pluginHome);
        }
        try {
           Class clazz=classLoader.loadClass(pluginJarName,pluginClassName);
           AuthCommand cmd= (AuthCommand) clazz.newInstance();
           return cmd.doValid(request);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | MalformedURLException e) {
            //todo handle excpetion
            e.printStackTrace();
        }
        return false;
    }

    public static class PluginAuthCommandClassLoader extends URLClassLoader{

        public PluginAuthCommandClassLoader(URL[] urls) {
            super(urls, Launcher.getLauncher().getClassLoader());
        }
    }



}
