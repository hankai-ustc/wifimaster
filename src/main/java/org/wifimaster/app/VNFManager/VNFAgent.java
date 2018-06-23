package org.wifimaster.app.VNFManager;

import org.onosproject.net.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wifimaster.app.WifiManager.WifiAgent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by hankai on 1/4/18.
 */
public class VNFAgent {
    protected  static Logger log = LoggerFactory.getLogger(WifiAgent.class);
    private InetAddress ipAddress;
    private Device pofSwitch;
    private PrintWriter outBuf;
    private StringBuffer msgBuffer;
    private Socket socketAgent=null;


    public VNFAgent(InetAddress inetAddress, Socket socket){
        this.ipAddress = inetAddress;
        this.socketAgent = socket;
    }
    public void setPofSwitch(Device pofswitch){
        pofSwitch = pofswitch;
    }

    public Device getPofSwitch(){
        return pofSwitch;
    }

    public void closeSocket(){
        if (socketAgent !=null){
            try{
                socketAgent.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public InetAddress getIpAddress(){
        return ipAddress;
    }

    public void sendMsg(String msg){
        outBuf.println(msg);
    }

}
