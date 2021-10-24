package rechard.learn.gw.service;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import rechard.learn.gw.auth.BasicAuthCommand;
import rechard.learn.gw.auth.JWTAuthCommand;
import rechard.learn.gw.auth.PluginAuthCommand;
import rechard.learn.gw.classloader.PluginClassLoader;
import rechard.learn.gw.exception.AuthenticationException;
import rechard.learn.gw.exception.UnknownServiceException;
import rechard.learn.gw.utils.GWUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务
 * @author Rechard
 **/
@Service
public class AuthService {
    private PluginClassLoader pluginClassLoader;
    @Autowired
    public AuthService( PluginClassLoader pluginClassLoader){
        this.pluginClassLoader=pluginClassLoader;
    }

    //配置方式可以从redis来,这里先写死
    private Map<String,String> serviceAuthMap=new HashMap<>();
    private Map<String,String> customerAuthMap=new HashMap<>();

    public AuthService(){
        //配置service对应的认证方式
        serviceAuthMap.put("acctService","basic-auth");
        serviceAuthMap.put("userService","jwt");
        serviceAuthMap.put("orderService","custom");

        //配置某个service的custom auth的配置
        String authConfig="{'pluginJarName':'gw-plugin-demo','pluginClassName':'rechard.learn.plugin.TestPlugin'}";
        customerAuthMap.put("orderService_custom_auth_config",authConfig);
    }

    @PostConstruct
    public void init(){
        //监听redis的auth_pluginjar_change_channel ,一旦有plugin jar上传
        //hotswap jar
        //pluginClassLoader.hotswap("gw-plugin-demo");
    }

    /**
     * 认证验证
     * @param request
     */
    public boolean canAccess(ServerHttpRequest request) throws AuthenticationException {
        String service= GWUtils.getService(request);
        String authStretage=serviceAuthMap.get(service);
        if(StringUtils.isEmpty(service)){
            throw new UnknownServiceException("service is null");
        }
        if(authStretage.equals("basic-auth")){
            return new BasicAuthCommand().doValid(request);
        }else if(authStretage.equals("jwt")){
            return new JWTAuthCommand().doValid(request);
        }else{
            //扩展方式
            //例如service=order
            //网关管理service管理里取到jar名和class,这里先写死
            //service一旦修改了就会由redis推送
            String config = customerAuthMap.get(service + "_custom_auth_config");
            String pluginJarName = JSONUtil.parseObj(config).getStr("pluginJarName");
            String pluginClassName = JSONUtil.parseObj(config).getStr("pluginClassName");
            return new PluginAuthCommand(pluginJarName,pluginClassName,pluginClassLoader)
                       .doValid(request);
        }
    }
}
