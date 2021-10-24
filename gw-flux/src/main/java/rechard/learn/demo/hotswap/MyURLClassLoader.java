package rechard.learn.demo.hotswap;

import cn.hutool.Hutool;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import rechard.learn.gw.classloader.PluginClassLoader;
import sun.misc.Launcher;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Rechard
 **/
public class MyURLClassLoader extends URLClassLoader {
    //hotswap和loadClass需要互斥
    private Object lock=new Object();
    private JarURLConnection cachedJarFile = null;

    public MyURLClassLoader() {
        super(new URL[] {}, MyURLClassLoader.class.getClassLoader());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException{
      synchronized (lock) {
          return super.loadClass(name, resolve);
      }
    }

    /**
     * 将指定的文件url添加到类加载器的classpath中去，并缓存jar connection，方便以后卸载jar
     * 一个可想类加载器的classpath中添加的文件url
     * @param
     */
    public void addURLFile(URL file) {
        try {
            // 打开并缓存文件url连接
            URLConnection uc = file.openConnection();
            if (uc instanceof JarURLConnection) {
                uc.setUseCaches(true);
                ((JarURLConnection) uc).getManifest();
                cachedJarFile = (JarURLConnection)uc;
            }
        } catch (Exception e) {
            System.err.println("Failed to cache plugin JAR file: " + file.toExternalForm());
        }
        addURL(file);
    }


    public void unloadJarFile(){
        JarURLConnection jarURLConnection = cachedJarFile;
        if(jarURLConnection==null){
            return;
        }
        try {
            System.err.println("Unloading plugin JAR file " + jarURLConnection.getJarFile().getName());
            jarURLConnection.getJarFile().close();
            jarURLConnection=null;
        } catch (Exception e) {
            System.err.println("Failed to unload JAR file\n"+e);
        }
    }

    /**
     *
     * 热替换
     * @param oldJarFile 旧的jar包
     * @param newJarFile 新的jar包
     * @param backupJar 备份的 jar包
     * @param myClassLoader
     * @param jarName
     */
    public void hotswap(String oldJarFile, String newJarFile, String backupJar,
                        MyClassLoader myClassLoader, String jarName) {
        synchronized (lock){
            //先关闭旧的jar
            unloadJarFile();
            //替换掉jar
            //1.拷贝旧的jar到backup目录里
            FileUtil.copyFile(oldJarFile,backupJar);
            //2.拷贝新的jar替换旧的jar
            FileUtil.copy(newJarFile,oldJarFile,true);
            //3.从缓存里清除
            myClassLoader.cleanCache(jarName);
        }
    }
}