package rechard.learn.gw.auth;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import rechard.learn.gw.exception.BadCredentialsException;
import rechard.learn.gw.plugin.AuthCommand;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现基本的认证
 * @author Rechard
 **/
public class BasicAuthCommand implements AuthCommand {

    public static final String AUTHORIZATION = "Authorization";
    public static final String AUTHENTICATION_SCHEME_BASIC = "Basic";

    Map<String,String> m =new HashMap<>();

    @PostConstruct
    public void init(){
       //rechard:rechard
       m.put("rechard","cmVjaGFyZDpyZWNoYXJk");

       //tom:tom
       m.put("tom","dG9tOnRvbQ==");
    }

    @Override
    public boolean doValid(ServerHttpRequest request) {

        String header = request.getHeaders().getFirst(AUTHORIZATION);
        if (header == null) {
            return false;
        }

        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
            return false;
        }

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        }
        catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        String user=token.substring(0, delim);
        String password=token.substring(delim + 1);
        String userPwd=m.get(user);
        if(userPwd!=null && userPwd.equalsIgnoreCase(password))
            return true;
        return false;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        System.out.println(new String(Base64.getEncoder().encode(String.format("%s:%s","tom","tom").getBytes()),"UTF-8"));
    }
}
