package rechard.learn.gw.constant;

/**
 * @author Rechard
 **/
public class Constants {
    /**
     * hystrix withExecutionIsolationSemaphoreMaxConcurrentRequests.
     */
    public static final int  MAX_CONCURRENT_REQUESTS = 100;

    /**
     * hystrix  withCircuitBreakerErrorThresholdPercentage.
     */
    public static final int ERROR_THRESHOLD_PERCENTAGE = 50;

    /**
     * hystrix withCircuitBreakerRequestVolumeThreshold.
     */
    public static final int REQUEST_VOLUME_THRESHOLD = 20;

    /**
     * hystrix withCircuitBreakerSleepWindowInMilliseconds.
     */
    public static final int SLEEP_WINDOW_INMILLISECONDS = 5000;

    /**
     * The constant TIME_OUT.
     */
    public static final int TIME_OUT = 3000;

}
