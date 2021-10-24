/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package rechard.learn.gw.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import rechard.learn.gw.constant.Constants;

public class HystrixBuilder {
    public static HystrixObservableCommand.Setter build(final HystrixConfig hystrixConfig) {

        if (hystrixConfig.getMaxConcurrentRequests() == 0) {
            hystrixConfig.setMaxConcurrentRequests(Constants.MAX_CONCURRENT_REQUESTS);
        }
        if (hystrixConfig.getErrorThresholdPercentage() == 0) {
            hystrixConfig.setErrorThresholdPercentage(Constants.ERROR_THRESHOLD_PERCENTAGE);
        }
        if (hystrixConfig.getRequestVolumeThreshold() == 0) {
            hystrixConfig.setRequestVolumeThreshold(Constants.REQUEST_VOLUME_THRESHOLD);
        }
        if (hystrixConfig.getSleepWindowInMilliseconds() == 0) {
            hystrixConfig.setSleepWindowInMilliseconds(Constants.SLEEP_WINDOW_INMILLISECONDS);
        }

        HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey(hystrixConfig.getGroupKey());

        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey(hystrixConfig.getCommandKey());

        final HystrixCommandProperties.Setter propertiesSetter =
                HystrixCommandProperties.Setter()
                        //超时设置
                        .withExecutionTimeoutInMilliseconds(hystrixConfig.getTimeout())
                        //开启短路
                        .withCircuitBreakerEnabled(true)
                        //使用信号量作为隔离策略
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        //设置使用SEMAPHORE隔离策略的时候，允许访问的最大并发量，超过这个最大并发量，请求直接被reject
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(hystrixConfig.getMaxConcurrentRequests())
                        //设置异常请求量的百分比，当异常请求达到这个百分比时，就触发打开短路器，默认是50，也就是50%
                        .withCircuitBreakerErrorThresholdPercentage(hystrixConfig.getErrorThresholdPercentage())
                        //配置10s内请求数超过3个时熔断器开始生效
                        .withCircuitBreakerRequestVolumeThreshold(hystrixConfig.getRequestVolumeThreshold())
                        //熔断后的重试时间窗口，且在该时间窗口内只允许一次重试。即在熔断开关打开后，在该时间窗口允许有一次重试，如果重试成功，则将重置Health采样统计并闭合熔断开关实现快速恢复，否则熔断开关还是打开状态，执行快速失败。
                        //默认是5s
                        .withCircuitBreakerSleepWindowInMilliseconds(hystrixConfig.getSleepWindowInMilliseconds());

        return HystrixObservableCommand.Setter
                .withGroupKey(groupKey)
                .andCommandKey(commandKey)
                .andCommandPropertiesDefaults(propertiesSetter);
    }

}
