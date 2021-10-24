package rechard.learn.gw;

import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Rechard
 **/
public class GWServlet  extends HttpServlet {

    private static final long serialVersionUID = -3374242278843351500L;
    //private ZuulRunner zuulRunner;
    //private AsyncTaskExecutor taskExecutor= new SimpleAsyncTaskExecutor("MvcAsync");

    private final RestTemplate restTemplate=new RestTemplate();
    private ExecutorService taskExecutor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String bufferReqsStr = config.getInitParameter("buffer-requests");
        boolean bufferReqs = bufferReqsStr != null && bufferReqsStr.equals("true") ? true : false;

        //  zuulRunner = new ZuulRunner(bufferReqs);
        taskExecutor=Executors.newFixedThreadPool(200);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

/*
       HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest( request, response);
        asyncWebRequest.setTimeout(3000l);

        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        asyncManager.setTaskExecutor(this.taskExecutor);
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        try {
            asyncManager.startCallableProcessing(()->{
                String method = request.getMethod();
                String requestURI = request.getRequestURI();
                int sl=-1;
                if((sl=requestURI.indexOf("/",1))==-1){
                    //todo throw exception
                }
                String service=requestURI.substring(1,sl);
                //service这里是sample
                String leftUri=requestURI.substring(sl);
                String proxyURI="http://localhost:8081"+leftUri;
                System.out.println("转成新得uri-》"+proxyURI);

                Map m=new HashMap<>();
                //重新组装成
                if(method.equalsIgnoreCase("POST")) {
                    ResponseEntity<Object> responseEntity = restTemplate.postForEntity(proxyURI, m, null);
                    return responseEntity;
                }else  if(method.equalsIgnoreCase("GET")) {
                    ResponseEntity<Object> responseEntity = restTemplate.getForEntity(proxyURI, null);
                    return responseEntity;
                }
                return null;
            },null);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       // System.out.println("controller"+Thread.currentThread());
        AsyncContext asyncContext = req.startAsync();
        asyncContext.addListener(getListener());
        taskExecutor.submit(new ProcessingThread(asyncContext));
        // asyncContext.start(new ProcessingThread(asyncContext));
        /*try {
            init((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);

            // Marks this request as having passed through the "Zuul engine", as opposed to servlets
            // explicitly bound in rechard.learn.web.xml, for which requests will not have the same data attached
            RequestContext context = GWRequestContext.getCurrentContext();
            context.setZuulEngineRan();

            try {
                preRoute();
            } catch (ZuulException e) {
                error(e);
                postRoute();
                return;
            }
            try {
                route();
            } catch (ZuulException e) {
                error(e);
                postRoute();
                return;
            }
            try {
                postRoute();
            } catch (ZuulException e) {
                error(e);
                return;
            }

        } catch (Throwable e) {
            error(new ZuulException(e, 500, "UNHANDLED_EXCEPTION_" + e.getClass().getName()));
        } finally {
            RequestContext.getCurrentContext().unset();
        }*/
    }

    private AsyncListener getListener() {
        return new AsyncListener() {
            public void onComplete(AsyncEvent asyncEvent) throws IOException {
                asyncEvent.getSuppliedResponse().getWriter().close();
                System.out.println("thread completed.");
            }
            public void onError(AsyncEvent asyncEvent) throws IOException {
                System.out.println("thread error.");
            }
            public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                System.out.println("thread started.");
            }
            public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                System.out.println("thread timeout.");
            }
        };

    }
}
