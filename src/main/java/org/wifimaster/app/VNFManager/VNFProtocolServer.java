package org.wifimaster.app.VNFManager;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.onosproject.net.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wifimaster.app.VNFManager.NFMaster;
import org.wifimaster.app.WifiManager.AgentProtocolServer;
import org.wifimaster.app.WifiManager.WifiMaster;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * Created by hankai on 1/4/18.
 */
public class VNFProtocolServer implements Runnable{
    protected static Logger log = LoggerFactory.getLogger(VNFProtocolServer.class);
    private final NFMaster vnfMaster;
    private int port;
    private ExecutorService threadPool;
    private ServerSocket serverSocket;
    private VNFAgentManager vnfAgentManager;


    public VNFProtocolServer(NFMaster nfMaster, VNFAgentManager vnfAgentManager,ExecutorService threadPool, int port ){
        this.vnfMaster = nfMaster;
        this.vnfAgentManager = vnfAgentManager;
        this.threadPool = threadPool;
        this.port = port;
    }


    @Override
    public void run(){
        try {
            serverSocket = new ServerSocket(port);
            while (vnfMaster.getFlag()){
                Socket socket = null;
                log.info("VNF Listen port is:" + port);
                log.info("Waiting connection from VNF agent ...");
                socket = serverSocket.accept();
                InetAddress inetAddress = socket.getInetAddress();
                log.info("A VNF agent start connection:" + inetAddress.getHostAddress());
                VNFAgent vnfAgent = new VNFAgent(inetAddress,socket);
                vnfAgentManager.addVNFAgent(inetAddress,vnfAgent);
                threadPool.execute(new AgentConnectionHandler(socket));
            }
            serverSocket.close();
            log.info("Close server socket +++++++++++++++++++++++++++++++++++++++++");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void receiveHello(JsonObject object, VNFAgent va){
        Device pofswitch=vnfMaster.getSwitch(va);
        va.setPofSwitch(pofswitch);
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
            VNFAgent va = vnfAgentManager.getVNFAgent(client.getInetAddress());
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
                            //log.info("Msg type is:"+msgType);
                            if (msgType.equals("HELLO")){
                                log.info("Receive a hello message");
                                receiveHello(object,va);
                            }
                            else if(msgType.equals("")){
                                //receiveDeauth(object,wa);
                            }

                        }
                    }
                    else
                        break;
                }
                inputStream.close();
                log.info("************Agent closed*******************");
                vnfAgentManager.removeVNFAgent(client.getInetAddress());
            } catch (IOException e1) {
                log.info("Client closed");
                vnfAgentManager.removeVNFAgent(client.getInetAddress());
            }
        }
    }

}
