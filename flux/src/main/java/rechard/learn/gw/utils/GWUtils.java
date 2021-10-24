package rechard.learn.gw.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * @author Rechard
 **/
public class GWUtils {

    /**
     * 截取url的path第一个/里的字符串
     * 例如/web/a/b/c
     * web就是service
     * @param request
     * @return
     */
    public static String getService(ServerHttpRequest  request){
        String requestURI = request.getURI().getPath();
        int sl=-1;
        if((sl=requestURI.indexOf("/",1))==-1){
            return null;
        }
        return requestURI.substring(1,sl);
    }
}
