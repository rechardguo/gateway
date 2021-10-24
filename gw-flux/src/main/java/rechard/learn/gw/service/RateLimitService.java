package rechard.learn.gw.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import rechard.learn.gw.auth.BasicAuthCommand;
import rechard.learn.gw.auth.JWTAuthCommand;
import rechard.learn.gw.auth.PluginAuthCommand;
import rechard.learn.gw.classloader.PluginClassLoader;
import rechard.learn.gw.exception.RateLimitException;
import rechard.learn.gw.exception.UnknownServiceException;
import rechard.learn.gw.plugin.RateLimitCommand;
import rechard.learn.gw.ratelimit.PluginRateLimitCommand;
import rechard.learn.gw.ratelimit.RateLimitCommandFactory;
import rechard.learn.gw.utils.GWUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 限流可以应用于多种维度如下
 * - 接口管理->接口->接口流控->指定流控算法，最大qps，降级文本
 * - service管理->service->servic流控->指定流控算法，最大qps，降级文本
 * - client管理->client->client流控->指定流控算法，最大qps，降级文本
 * 限流算法选择
 * - 令牌桶
 *
 * @auhtor rechard
 */
@Service
public class RateLimitService {

    private PluginClassLoader pluginClassLoader;
    //不同的target限流
    //key:service#api val: json
    private Map<String,String> apiRateLimitMap=new HashMap<>();
    //key:service val: json
    private Map<String,String> serviceRateLimitMap=new HashMap<>();
    //客户化限流
    private Map<String,String> customApiRateLimitMap=new HashMap<>();
    private Map<String,String> customServiceRateLimitMap=new HashMap<>();


    @Autowired
    public RateLimitService( PluginClassLoader pluginClassLoader){
        this.pluginClassLoader=pluginClassLoader;
        //数据来源来自redis,这里写死
        //service总的限流
        String serviceRateLimitConfJson="{'qps':100,'stretage':'tokenLimit','failRespCode':500,'failRespTxt':'稍后再试'}";
        serviceRateLimitMap.put("orderService_serviceRatelimit",serviceRateLimitConfJson);
         //service某个api限流
        String apiRateLimitConfJson="{'qps':10,'stretage':'funnelLimit','failRespCode':500,'failRespTxt':'稍后再试'}";
        apiRateLimitMap.put("orderService#/order/create_apiRatelimit",apiRateLimitConfJson);
        //客户化
        String customServiceRateLimitConfJson="{'qps':100,'failRespCode':500,'failRespTxt':'稍后再试'" +
                ",'pluginJarName':'gw-plugin-demo','pluginJarClass':'rechard.learn.plugin.TestRateLimit'}";
        serviceRateLimitMap.put("orderService_serviceRatelimit_custom",customServiceRateLimitConfJson);
    }

    //todo 返回response,可以含有具体的限流,而不是boolean
    public boolean isAllowed(ServerHttpRequest request) throws RateLimitException {
        String service= GWUtils.getService(request);
        String url=GWUtils.getURL(request);

        String serviceRateLimitConf=serviceRateLimitMap.get(service+"_serviceRatelimit");
        JSONObject serviceRateLimitConfJson = JSONUtil.parseObj(serviceRateLimitConf);
        String stretage = serviceRateLimitConfJson.getStr("stretage");
        if(!RateLimitCommandFactory.of(stretage).isAllowed(request)){
            return false;
        }

        String apiRateLimitConf=serviceRateLimitMap.get(service+"#"+url+"_apiRatelimit");
        JSONObject apiRateLimitConfJson =JSONUtil.parseObj(serviceRateLimitConf);
        stretage = apiRateLimitConfJson.getStr("stretage");
        if(!RateLimitCommandFactory.of(stretage).isAllowed(request)){
            return false;
        }

        //plugin
        String serviceRateLimitCustomConf=serviceRateLimitMap.get(service+"_serviceRatelimit_custom");
        JSONObject serviceRateLimitCustomJson =JSONUtil.parseObj(serviceRateLimitCustomConf);
        String pluginJarName = serviceRateLimitCustomJson.getStr("pluginJarName");
        String pluginClassName = serviceRateLimitCustomJson.getStr("pluginClassName");
        return new PluginRateLimitCommand(pluginJarName,pluginClassName,pluginClassLoader)
                .isAllowed(request);
    }


}
