package rechard.learn.gw.ratelimit;

import rechard.learn.gw.plugin.RateLimitCommand;

/**
 * @author Rechard
 **/
public class RateLimitCommandFactory {
    public static RateLimitCommand of(String stretage) {
        if(stretage.equalsIgnoreCase("tokenLimit")){
            return new TokenRateLimitCommand();
        }
        return null;
    }
}
