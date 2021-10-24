package rechard.learn.gw.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * @author Rechard
 **/
public class GWUtils {

    /**
     * 截取url的path中第1个/和第2个/里的字符串
     * 例如/web/a/b/c
     * web就是service
     * @param request
     * @return service名
     */
    public static String getService(ServerHttpRequest  request){
        String requestURI = request.getURI().getPath();
        int sl=-1;
        if((sl=requestURI.indexOf("/",1))==-1){
            return null;
        }
        return requestURI.substring(1,sl);
    }
    /**
     * 截取url的path中第2个/后的字符串
     * 例如/web/a/b/c
     * 返回 /a/b/c
     * @param request
     * @return url
     */
    public static String getURL(ServerHttpRequest  request){
        String requestURI = request.getURI().getPath();
        int sl=-1;
        if((sl=requestURI.indexOf("/",1))==-1){
            return null;
        }
        return requestURI.substring(sl);
    }

    public static void main(String[] args) {
        URI uri= UriComponentsBuilder.fromPath("http://192.168.0.1:8089/a/b/c").build(true).toUri();
        System.out.println(uri.getRawPath());
        System.out.println(uri.getScheme());
    }
}
