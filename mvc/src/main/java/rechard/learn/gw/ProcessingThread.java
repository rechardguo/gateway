package rechard.learn.gw;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 代理转换处理
 *
 * @author Rechard
 **/
public class ProcessingThread implements Runnable {
    private AsyncContext asyncContext;

    private RestTemplate restTemplate=new RestTemplate();

    public ProcessingThread(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread());
        HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        int sl=-1;
        if((sl=requestURI.indexOf("/",1))==-1){
            //todo throw exception
            return;
        }
        String service=requestURI.substring(1,sl);
        //service这里是sample
        String leftUri=requestURI.substring(sl);
        String proxyURI="http://localhost:8080"+leftUri;
        System.out.println("转成新得uri-》"+proxyURI);

        Map m=new HashMap<>();
        request.getHeaderNames();

        String resp="";
        ResponseEntity<String> responseEntity =null;
        if(method.equalsIgnoreCase("POST")) {
            responseEntity = restTemplate.postForEntity(proxyURI, m, String.class);
            resp=responseEntity.getBody();
        }else  if(method.equalsIgnoreCase("GET")) {
            responseEntity = restTemplate.getForEntity(proxyURI, String.class);
            resp=responseEntity.getBody();
        }else{
            resp="method not support" ;
        }

        try {
            System.out.println(responseEntity.getBody());
            asyncContext.getResponse().setCharacterEncoding("UTF-8");
            asyncContext.getResponse().getWriter().write(resp);
            asyncContext.getResponse().getWriter().flush();

            asyncContext.complete();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
