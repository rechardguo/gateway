package rechard.learn.gw.hystrix;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import rechard.learn.gw.constant.Constants;

@Getter
@Setter
@EqualsAndHashCode
public class HystrixConfig {

    /**
     * hystrix group key is required.
     */
    private String groupKey;

    /**
     * hystrix command key is required.
     */
    private String commandKey;

    /**
     * hystrix withCircuitBreakerErrorThresholdPercentage.
     */
    private int errorThresholdPercentage = Constants.ERROR_THRESHOLD_PERCENTAGE;

    /**
     * hystrix withCircuitBreakerRequestVolumeThreshold.
     */
    private int requestVolumeThreshold = Constants.REQUEST_VOLUME_THRESHOLD;

    /**
     * hystrix withCircuitBreakerSleepWindowInMilliseconds.
     */
    private int sleepWindowInMilliseconds = Constants.SLEEP_WINDOW_INMILLISECONDS;

   //超时时间，通过通过网关管理平台配置
    private Integer timeout ;

    //通过网关管理平台配置,信号量限制
    private int maxConcurrentRequests ;

    //通过网关管理平台配置,iplist
    private String ipList ;

    //通过网关管理平台配置,降级文字返回
    private String fallbackResp ;

    //通过网关管理平台配置,负载均衡策略
    private String loadbalanceStretage;
}
