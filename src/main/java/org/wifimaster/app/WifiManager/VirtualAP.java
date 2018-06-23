package org.wifimaster.app.WifiManager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hankai on 11/28/17.
 */
public class VirtualAP {
    private final Logger log = LoggerFactory.getLogger(VirtualAP.class);
    private final WifiAgent wifiAgent;
    private int channel;
    private String ssid;
    private MACAddress bssid;
    private String ifname;
    private short outport;

    public VirtualAP(String ifname, MACAddress bssid,String ssid,short outport,WifiAgent wifiAgent){
        this.ifname = ifname;
        this.bssid = bssid;
        this.ssid = ssid;
        this.outport = outport;
        this.wifiAgent = wifiAgent;
    }


    public void setBssid(MACAddress macAddress){
        bssid = macAddress;
    }

    public MACAddress getBssid(){
        return bssid;
    }

    public void setSsid(String ssid){
        this.ssid=ssid;
    }

    public String getSsid(){
        return ssid;
    }

    public String getIfname(){
        return ifname;
    }

    public short getOutport(){
        return outport;
    }

    public WifiAgent getWifiAgent(){
        return wifiAgent;
    }
}
