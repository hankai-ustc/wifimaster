package org.wifimaster.app.WifiManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created by hankai on 1/4/18.
 */
public class WifiMaster implements Runnable{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AgentManager agentManager =null;
    private ClientManager clientManager = null;
    private ExecutorService threadPool;
    private int port;
    private boolean flag;

    public WifiMaster(ExecutorService threadPool,int port){
        this.threadPool =threadPool;
        this.port = port;
    }
    @Override
    public void run() {
        log.info("Starting WifiMaster ......");
        flag =true;
        agentManager = new AgentManager();
        clientManager =new ClientManager();
        threadPool.execute(new AgentProtocolServer(this,port,agentManager,clientManager,threadPool));

    }

    public void setFlag(boolean flag){
        this.flag = flag;
    }

    public AgentManager getAgentManager(){
        return this.agentManager;
    }

    public ClientManager getClientManager(){
        return this.clientManager;
    }

    public boolean getFlag(){
        return flag;
    }
}
