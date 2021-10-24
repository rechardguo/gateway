package rechard.learn.gw.lb;

import rechard.learn.gw.hystrix.HystrixConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rechard
 **/
public class DefaultLoadBalancer implements ILoadBalancer{
    private List<Server> servers;
    private String ipList;
    public DefaultLoadBalancer(String ipList) {
        this.ipList=ipList;
        initWithNiwsConfig(ipList);
    }

    private void initWithNiwsConfig(String ipList) {
        servers=new ArrayList<>();
        //servers = 192.168.1.100:8080|3,192.168.1.200:8081
        String[] serverList= ipList.split(",");
        for(String s:serverList){
            Server server=null;
            if(s.indexOf("\\|")!=-1){
                String[] serverInfo=s.split("\\|");
                String[] ipPort = serverInfo[0].split(":");
                server = new Server(ipPort[0],Integer.parseInt(ipPort[1]),Integer.parseInt(serverInfo[1]));
            }else {
                String[] ipPort =s.split(":");
                server = new Server(ipPort[0],Integer.parseInt(ipPort[1]));
            }
            servers.add(server);
        }
    }


   /* *//**
     * Choose a server from load balancer.
     *
     * @param key An object that the load balancer may use to determine which server to return. null if
     *         the load balancer does not use this parameter.
     * @return server chosen
     *//*
    public Server chooseServer(Object key){
        return null;
    }*/

    /**
     * @return All known servers, both reachable and unreachable.
     */
    public List<Server> getAllServers(){
        return servers;
    }
}
