package org.wifimaster.app.WifiManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hankai on 11/28/17.
 */
public class ClientManager {
    private final Map<MACAddress,WifiClient> clientMap= new ConcurrentHashMap<MACAddress,WifiClient>();

    public  void addClient(WifiClient wifiClient){
        if (clientMap.containsKey(wifiClient.gethwAddress())){
            return;
        }

        clientMap.put(wifiClient.gethwAddress(),wifiClient);

    }

    public void removeClient(final MACAddress macAddress){
        if (clientMap.containsKey(macAddress)){
            clientMap.remove(macAddress);
        }
    }

    public WifiClient getClient(final MACAddress macAddress){
        if(clientMap.containsKey(macAddress)) {
            return clientMap.get(macAddress);
        }
        return null;
    }

    public Map<MACAddress,WifiClient> getClientMap(){
        return clientMap;
    }
}
