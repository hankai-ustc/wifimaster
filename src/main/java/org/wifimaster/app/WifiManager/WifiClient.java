package org.wifimaster.app.WifiManager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by hankai on 11/28/17.
 */
public class WifiClient {
    protected final Logger log = LoggerFactory.getLogger(WifiClient.class);
    private final VirtualAP vap;
    private final MACAddress hwAddress;
    private InetAddress ipAddress;

    public WifiClient(MACAddress macAddress,InetAddress ipAddress,VirtualAP vap){
        this.hwAddress = macAddress;
        this.ipAddress = ipAddress;
        this.vap = vap;
    }

    public MACAddress gethwAddress(){
        return hwAddress;
    }

    public VirtualAP getVap(){
        return vap;
    }

    public void setIpAddress(InetAddress ipAddr){
        ipAddress = ipAddr;
    }


}
