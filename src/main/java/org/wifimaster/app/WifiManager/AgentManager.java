package org.wifimaster.app.WifiManager;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hankai on 11/22/17.
 */
public class AgentManager {
    private final ConcurrentHashMap <InetAddress, WifiAgent> agentMap = new ConcurrentHashMap<InetAddress,WifiAgent>();
    protected static Logger log = LoggerFactory.getLogger(AgentManager.class);

    public AgentManager(){

    }

    public void addWifiAgent(InetAddress inetAddress,WifiAgent wifiAgent){
        if (!agentMap.containsKey(inetAddress)){
            agentMap.put(inetAddress,wifiAgent);
            log.info("Add a WifiAgent:"+inetAddress.getHostAddress());
        }
    }

    public WifiAgent getWifiAgent(InetAddress inetAddress){
        if (agentMap.containsKey(inetAddress)){
            return agentMap.get(inetAddress);}
        return null;
        }

    public void removeWifiAgent(InetAddress inetAddress){
        if(agentMap.containsKey(inetAddress)){

            WifiAgent wifiAgent = getWifiAgent(inetAddress);
            wifiAgent.closeSocket();
            agentMap.remove(inetAddress);
            log.info("Remove WifiAgent:"+inetAddress.getHostAddress());
        }
    }

    public ConcurrentHashMap <InetAddress, WifiAgent> getAllWifiAgent() {
        return agentMap;
    }

    public void removeAllWifiAgent(){
        for(Map.Entry<InetAddress,WifiAgent> entry:agentMap.entrySet()){
            WifiAgent wifiAgent=entry.getValue();
            wifiAgent.closeSocket();
            log.info("Remove WifiAgent:"+wifiAgent.getIpAddress().getHostAddress());
            agentMap.remove(entry.getKey());
        }
        log.info("Remove All WifiAgents");
    }

}
