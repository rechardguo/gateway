package rechard.learn.demo.redispubsub;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

/**
 * 监听redis的某个channel变化
 *
 * redis-cli
 * publish auth_plugin_change_channel xxxx
 *
 * 放入某条消息
 *
 * @author Rechard
 **/
public class RedisChannelListener {

    private JedisPool jedisPool;
    private String ratelimitSha;

    public RedisChannelListener() {
        this.jedisPool=new JedisPool("localhost");
    }


    public static void main(String[] args) {
        new RedisChannelListener().listenChange();

    }

    private void listenChange() {
        final Throwable[] throwable = new Throwable[1];
        final boolean[] b = {false};

        this.jedisPool.getResource().subscribe(new JedisPubSub(){
            //这里会获取到某条消息
            public void onMessage(String channel, String message) {
                System.out.println(channel+"->"+message);
            }
            public void onSubscribe(String channel, int subscribedChannels) {
                System.out.println(channel+"->"+subscribedChannels);
            }

        },"auth_plugin_change_channel");
    }
}
