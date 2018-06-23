package org.wifimaster.app.WifiManager;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;


/**
 * Created by hankai on 11/22/17.
 */
public class AgentProtocolServer implements Runnable{
    protected static Logger log = LoggerFactory.getLogger(AgentProtocolServer.class);
    private final WifiMaster wifiMaster;
    private final int port;
    private ExecutorService executor;
    private ServerSocket serverSocket;
    private AgentManager agentManager;
    private ClientManager clientManager;
    public AgentProtocolServer(WifiMaster wm, int port, AgentManager agentManager,ClientManager clientManager,ExecutorService executor){
        this.wifiMaster = wm;
        this.port = port;
        this.agentManager = agentManager;
        this.executor = executor;
        this.clientManager = clientManager;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(port);
            while (wifiMaster.getFlag()) {
                Socket socket = null;
                log.info("Listen port is:" + port);
                log.info("Waiting connection from wifi agent ...");
                socket = serverSocket.accept();
                InetAddress inetAddress = socket.getInetAddress();
                log.info("A wifi agent start connection:" + inetAddress.getHostAddress());
                WifiAgent wifiAgent = new WifiAgent(inetAddress, socket);
                agentManager.addWifiAgent(inetAddress, wifiAgent);
                executor.execute(new AgentConnectionHandler(socket));
            }
            serverSocket.close();
            log.info("Close server socket +++++++++++++++++++++++++++++++++++++++++");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveAddVap(JsonObject object,WifiAgent wifiAgent){
        String ifname = object.get("ifname").asString();
        String bssid = object.get("bssid").asString();
        String ssid = object.get("ssid").asString();
        String portId  = object.get("portId").asString();
        VirtualAP vap = new VirtualAP(ifname,MACAddress.valueOf(bssid),ssid,Short.parseShort(portId),wifiAgent);
        wifiAgent.addVirtualAP(vap);
        log.info("Add a virtual access point");

    }

    public void receiveDelVap(JsonObject object,WifiAgent wifiAgent){
        String ifname = object.get("ifname").asString();
        wifiAgent.delVirtualAP(ifname);
        log.info("Remove a virtual access point");
    }



    public void receiveHello(JsonObject object, WifiAgent wifiAgent){
        wifiAgent.setPhy(object.get("phy").asString());
        wifiAgent.setChannel(Integer.parseInt(object.get("channel").asString()));
        receiveAddVap(object,wifiAgent);
        wifiAgent.sendHello("HELLO");
    }


    public void receiveAssociation(JsonObject object, WifiAgent wifiAgent){
        VirtualAP vap= wifiAgent.getVirtualAP(object.get("ifname").asString());
        if (vap !=null){
            try{
                WifiClient wc = new WifiClient(MACAddress.valueOf(object.get("client").asString()),InetAddress.getByName("0.0.0.0"),vap);
                clientManager.addClient(wc);
                log.info("Add a new client");
                //wifiAgent.createVap("wlan2","pof-hankai2");
                //og.info("Crteate a new virtual AP");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public void receiveDeauth(JsonObject object,WifiAgent wifiAgent){
        VirtualAP vap = wifiAgent.getVirtualAP(object.get("ifname").asString());
        if(vap !=null){
            clientManager.removeClient(MACAddress.valueOf(object.get("client").asString()));
            log.info("Remove a client");
            //wifiAgent.destroyVap("wlan2");
            //log.info("destroy a virtual AP");
        }
    }

    private class AgentConnectionHandler implements Runnable {
        private Socket client = null;

        public AgentConnectionHandler(Socket client) {
            this.client = client;
        }
        @Override
        public void run() {
            InputStream inputStream = null;
            String msg_type = null;
            boolean flag=true;
            WifiAgent wa = agentManager.getWifiAgent(client.getInetAddress());
            try {
                inputStream = client.getInputStream();
                byte buf[] = new byte[1024];
                int len =-1;
                while(flag){
                    if ((len=inputStream.read(buf))>0){
                        String msg= new String(buf,0,len);
                        if (msg == null || " ".equals(msg)) {
                            break;
                        } else {
                            log.info("Receive message from agent:" + msg);
                            JsonObject object = Json.parse(msg).asObject();
                            String msgType = object.get("type").asString();

                            if (msgType.equals("HELLO")){
                                receiveHello(object,wa);
                            }
                            else if (msgType.equals("AP-STA-CONNECTED")){
                                receiveAssociation(object,wa);
                            }
                            else if(msgType.equals("AP-STA-DISCONNECTED")){
                                receiveDeauth(object,wa);
                            }
                            else if(msgType.equals("ADD_VAP")){
                                receiveAddVap(object,wa);
                            }
                            else if(msgType.equals("DEL_VAP")){
                                receiveDelVap(object,wa);
                            }

                        }
                    }
                    else
                        break;
                }
                inputStream.close();
                log.info("************Agent closed*******************");
                agentManager.removeWifiAgent(client.getInetAddress());
            } catch (IOException e1) {
                log.info("Client closed");
                agentManager.removeWifiAgent(client.getInetAddress());
            }
        }
    }

}
