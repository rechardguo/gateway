package rechard.learn.demo.hotswap;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rechard
 **/
public class MyClassLoader {
    private static String pluginHome="D:\\dev-code\\opensource-code\\springcloud_ry_code\\api-gateway-workspace\\gateway\\pluginjar";
    private static String pluginHome_backup=pluginHome+"\\backup";
    private static String pluginHome_replacement=pluginHome+"\\replacement";

    private final static ConcurrentHashMap<String,MyURLClassLoader> LOADER_CACHE = new ConcurrentHashMap<>();

    public void loadJar(String jarName) throws MalformedURLException {
        URL jarUrl = new URL("jar:file:/"+pluginHome+"/"+jarName+".jar!/");
        MyURLClassLoader urlClassLoader = LOADER_CACHE.get(jarName);
        if(urlClassLoader!=null){
            return;
        }
        urlClassLoader = new MyURLClassLoader();
        urlClassLoader.addURLFile(jarUrl);
        LOADER_CACHE.put(jarName,urlClassLoader);
    }

    public Class loadClass(String jarName,String name) throws ClassNotFoundException, MalformedURLException {
        MyURLClassLoader urlClassLoader = LOADER_CACHE.get(jarName);
        if(urlClassLoader==null){
            loadJar(jarName);
            urlClassLoader = LOADER_CACHE.get(jarName);
        }
        return urlClassLoader.loadClass(name);
    }

    public void unloadJarFile(String jarName) throws MalformedURLException {
        MyURLClassLoader urlClassLoader = LOADER_CACHE.get(jarName);
        if(urlClassLoader==null){
            return;
        }
        urlClassLoader.unloadJarFile();
        urlClassLoader = null;
        LOADER_CACHE.remove(jarName);
    }

    public void cleanCache(String jarName){
        LOADER_CACHE.remove(jarName);
    }

    public void hotswap(String jarName) {
        MyURLClassLoader urlClassLoader = LOADER_CACHE.get(jarName);
        long timestamp=System.currentTimeMillis();
        urlClassLoader.hotswap(pluginHome+"/"+jarName+".jar",
                pluginHome_replacement+"/"+jarName+".jar",
                pluginHome_backup+"/"+jarName+"_"+timestamp+".jar",this,jarName);
    }
}
