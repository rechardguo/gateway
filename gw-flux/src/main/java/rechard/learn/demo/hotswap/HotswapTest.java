package rechard.learn.demo.hotswap;

import rechard.learn.gw.plugin.AuthCommand;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * gw-plugin-demo的里代码打包成jar放到pluginjar/replacement下
 *
 * 更改MyClassLoader里的pluginHome
 *
 * 然后直接运行main
 *
 * @author Rechard
 **/
public class HotswapTest {

    public static void main(String[] args) throws Exception {
        // 使用特定的classloader
        MyClassLoader classLoader = new MyClassLoader();
        //加载某个plugin的jar
        classLoader.loadJar("gw-plugin-demo");

        //线程不断的运行中，通过classloader从某个路径下的jar里的加载类
       new Thread(() -> {
           while (true) {
               try {
                   Class clz = classLoader.loadClass("gw-plugin-demo", "rechard.learn.plugin.TestPlugin");
                   AuthCommand cmd = (AuthCommand) clz.newInstance();
                   cmd.doValid(null);
                   Thread.sleep(1000);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
        }, "running-thread").start();

        //5秒后替换jar
        //模拟网关管理平台新上传1个jar，mq通知网关更新jar
        Thread.sleep(5000);
        System.out.println("替换jar");
        classLoader.hotswap("gw-plugin-demo");
        System.out.println("替换完毕");
    }
}
