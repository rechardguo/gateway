package rechard.learn.gw;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rechard
 **/
public class GWRequestContext extends ConcurrentHashMap<String, Object> {

    //每个请求进来后都是1个单独的上下文
    protected static final ThreadLocal<? extends GWRequestContext> threadLocal = new ThreadLocal<GWRequestContext>() {
        @Override
        protected GWRequestContext initialValue() {
            try {
                return new GWRequestContext();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static GWRequestContext getCurrentContext() {
        GWRequestContext context = threadLocal.get();
        return context;
    }

    public void unset() {
        threadLocal.remove();
    }

}
