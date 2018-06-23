package org.wifimaster.app.WifiManager;


import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.onosproject.net.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hankai on 11/22/17.
 */
public class WifiAgent {
    protected  static Logger log = LoggerFactory.getLogger(WifiAgent.class);
    private final Map<String,VirtualAP> vapMap= new ConcurrentHashMap<String,VirtualAP>();
    private Socket socketAgent=null;
    private int channel;
    private String phy;
    private InetAddress ipAddress;
    private Device pofSwitch;
    private PrintWriter outBuf;
    private StringBuffer msgBuffer;

    public WifiAgent(InetAddress ipAddress,Socket socket){
        this.ipAddress = ipAddress;
        this.socketAgent = socket;
        this.msgBuffer = new StringBuffer();
        try {
            this.outBuf = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void setPofSwitch(Device pofswitch){
        pofSwitch = pofswitch;
    }

    public Device getPofSwitch(){
        return pofSwitch;
    }

    public int getChannel(){
        return channel;
    }

    public void setChannel(int chan){
        if(chan != channel)
            channel=chan;
    }

    public InetAddress getIpAddress(){
        return ipAddress;
    }

    public void addVirtualAP(VirtualAP vap){
        String ifname = vap.getIfname();
        if (!vapMap.containsKey(ifname)){
            vapMap.put(ifname,vap);
        }
    }

    public void delVirtualAP(String ifname){
        if (vapMap.containsKey(ifname)){
            vapMap.remove(ifname);
        }
    }

    public VirtualAP getVirtualAP(String ifname){
        if(vapMap.containsKey(ifname)){
            return vapMap.get(ifname);
        }
        return null;
    }

    public void sendMsg(String msg){
        outBuf.println(msg);

    }


    public void readerHandler(String msg){
        msgBuffer.append(msg);
    }

    public void setPhy(String phyName){
        phy = phyName;
    }

    public String getPhy(){
        return phy;
    }



    public void createVap(String ifname,String ssid){
        JsonObject vap = Json.object().add("type","add_vap").add("interface",ifname).add("ssid",ssid);
        String json = vap.toString();
        log.info("json value is:"+json);
        this.sendMsg(json);
    }

    public void destroyVap(String ifname){
        JsonObject vap = Json.object().add("type","remove_vap").add("interface",ifname);
        String json = vap.toString();
        this.sendMsg(json);
    }

    public void sendHello(String msg){
        JsonObject hello = Json.object().add("type",msg);
        String json = hello.toString();
        this.sendMsg(json);
    }
    public void createNvaps(String vapNumber){
        JsonObject nvap = Json.object().add("type","add_nvaps").add("vap_number",vapNumber);
        String json = nvap.toString();
        this.sendMsg(json);
    }

    public void removeNvaps(String vapNumber){
        JsonObject nvap = Json.object().add("type","remove_nvaps").add("vap_number",vapNumber);
        String json = nvap.toString();
        this.sendMsg(json);
    }
}
