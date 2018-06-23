package org.wifimaster.app.VNFManager;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

/**
 * Created by hankai on 2/2/18.
 */
public class Image {
    private String type;
    private int portNumber;
    private JsonObject context=null;

    public void Image(String type,int portNumber ){
        this.type = type;
        this.portNumber = portNumber;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type;
    }
    public void setPortNumber(int portNumber){
        this.portNumber = portNumber;
    }

    public void setContext(JsonObject object){
        this.context = object;
    }

    public JsonObject getContext(){
        return this.context;
    }

    public JsonObject toJson(){
        JsonObject object = Json.object().add("type",this.type).add("portnumber",this.portNumber).add("context",this.context);
        return object;
    }

    public String toString(){
        JsonObject object = this.toJson();
        return object.toString();
    }
}
