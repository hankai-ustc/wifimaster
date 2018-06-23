package org.wifimaster.app.VNFManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wifimaster.app.WifiManager.WifiAgent;


import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hankai on 1/4/18.
 */
public class VNFAgentManager {
    private final ConcurrentHashMap<InetAddress, VNFAgent> agentMap = new ConcurrentHashMap<InetAddress,VNFAgent>();
    protected static Logger log = LoggerFactory.getLogger(VNFAgentManager.class);

    public VNFAgentManager(){

    }
    public void addVNFAgent(InetAddress inetAddress,VNFAgent vnfAgent){
        if (!agentMap.containsKey(inetAddress)){
            agentMap.put(inetAddress,vnfAgent);
            log.info("Add a VNFAgent:"+inetAddress.getHostAddress());
        }
    }

    public VNFAgent getVNFAgent(InetAddress inetAddress){
        if (agentMap.containsKey(inetAddress)){
            return agentMap.get(inetAddress);}
        return null;
    }

    public void removeVNFAgent(InetAddress inetAddress){
        if(agentMap.containsKey(inetAddress)){
            VNFAgent vnfAgent = getVNFAgent(inetAddress);
            vnfAgent.closeSocket();
            agentMap.remove(inetAddress);
            log.info("Remove VNFAgent:"+inetAddress.getHostAddress());
        }
    }

    public void removeAllVNFAgent(){
        for(Map.Entry<InetAddress,VNFAgent> entry:agentMap.entrySet()){
            VNFAgent vnfAgent=entry.getValue();
            vnfAgent.closeSocket();
            log.info("Remove VNFAgent:"+vnfAgent.getIpAddress().getHostAddress());
            agentMap.remove(entry.getKey());
        }
        log.info("Remove All VNFAgents");
    }

}
