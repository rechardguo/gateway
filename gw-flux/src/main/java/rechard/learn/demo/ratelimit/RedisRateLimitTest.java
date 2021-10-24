package rechard.learn.demo.ratelimit;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用lua 基于令牌桶的限流
 * @author Rechard
 **/
public class RedisRateLimitTest {

    private JedisPool jedisPool;
    private String ratelimitSha;

    public RedisRateLimitTest() {
       this.jedisPool=new JedisPool("localhost");
    }

    public void loadLuaScript() throws UnsupportedEncodingException {
        byte[] bytes = FileUtil.readBytes("ratelimit.lua");
        Jedis resource = this.jedisPool.getResource();
        byte[] result = resource.scriptLoad(bytes);
        this.ratelimitSha=new String(result,"UTF-8");
        resource.close();
    }

    /**
     *
     * @param api 某个api
     * @param qps 最大允许的qps
     */
    public void adminSetApiLimit(String api,int qps){
        /**
         * api::product_sec_kill::ratelimit
         * {
         *        last_access_sec:当前时间
         *        curr_permits:100  //初始化就是最大qps
         *        max_burst:100
         *        rate: 100
         * }
         */
        Map<String,String> map=new HashMap<>();
        long now=System.currentTimeMillis()/1000;
        map.put("last_access_sec",now+"");
        map.put("curr_permits",qps+"");
        map.put("max_burst",qps+"");
        map.put("rate",qps+"");
        Jedis resource = this.jedisPool.getResource();
        resource.hmset("api::"+api+"::ratelimit",map);
        resource.close();
    }

    public int leftToken(String api){
        try(Jedis resource = this.jedisPool.getResource()) {
            return Integer.parseInt(resource.hget("api::" + api + "::ratelimit", "curr_permits"));
        }
    }


    /**
     * 获取token
     */
    public boolean accquire(String api){
        try(Jedis resource = this.jedisPool.getResource()) {
            long now=System.currentTimeMillis()/1000;
            Long result = (Long) resource.evalsha(this.ratelimitSha, 2, "api::" + api + "::ratelimit",now+"");
            return result > 0;
        }
    }

    public static void main(String[] args) throws Exception {

        final RedisRateLimitTest rateLimit=new RedisRateLimitTest();
        //假定给定是1秒是3个
        rateLimit.adminSetApiLimit("product_sec_kill",4);
        rateLimit.loadLuaScript();

        for(int i=0;i<20;i++){
            Thread.sleep(200);
            new Thread(()->{
                if(rateLimit.accquire("product_sec_kill")){
                    System.out.println(Thread.currentThread().getName()+":"+DateUtil.now()+"->access");
                }else{
                    System.err.println(Thread.currentThread().getName()+":"+DateUtil.now()+"->deny");
                }
            },"thread"+i).start();
        }
    }
}
