package rechard.learn.gw;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * 目标
 * 拦截请求 http://localhost:9999/sample/test
 * 封装成http://loclahost:8080/sample/test
 * 将请求通过servlet3.0的方法发出
 *  最后要能正确的接收到请求
 *
 * 该类写法参考了zuul
 *
 * @author Rechard
 **/
public class GWHandlerMapping  extends AbstractUrlHandlerMapping {

     private GWController gwController;

  //  private ErrorController errorController;

   // private PathMatcher pathMatcher = new AntPathMatcher();

    private volatile boolean dirty = true;

    public GWHandlerMapping(GWController gwController) {
        this.gwController = gwController;
        setOrder(-20);
    }

    @Override
    protected HandlerExecutionChain getCorsHandlerExecutionChain(
            HttpServletRequest request, HandlerExecutionChain chain,
            CorsConfiguration config) {
        if (config == null) {
            // Allow CORS requests to go to the backend
            return chain;
        }
        return super.getCorsHandlerExecutionChain(request, chain, config);
    }


    @Override
    protected Object lookupHandler(String urlPath, HttpServletRequest request)
            throws Exception {
//        RequestContext ctx = RequestContext.getCurrentContext();
//        if (ctx.containsKey("forward.to")) {
//            return null;
//        }
        if (this.dirty) {
            synchronized (this) {
                if (this.dirty) {
                    registerHandlers();
                    this.dirty = false;
                }
            }
        }

        return super.lookupHandler(urlPath, request);
    }

    private boolean isIgnoredPath(String urlPath, Collection<String> ignored) {
        return false;
    }

    //先定死sample开头的由gwController来转发
    private void registerHandlers() {
      registerHandler("/sample/**", this.gwController);
    }
}
