package bgu.spl.net.impl.stomp;

import java.util.Map;

import bgu.spl.net.impl.gamesUpdate.Event;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.impl.Frames.*;
import bgu.spl.net.impl.User.User;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public abstract class StompFrame {
    public StompCommand stompCommand;
    public String receiptId;
    public Map<String,String> stompsHeaders = new HashMap<String,String>();
    public Event body; // we need to support a list ?
    public boolean isBodyEmpty;
    public boolean errorAccourd = false;
    public List<String> missingHeaders = new ArrayList<String>();
    public Map<String,String> errorHeaders = new HashMap<String,String>();
    public FrameUtil frameUtil = new FrameUtil();
                        
    public StompFrame(StompCommand command, Map<String,String> headers){
        stompCommand = command;
        stompsHeaders = headers;
    }

    public StompFrame(String stompFrameString) {
    }

    public void excute(GamesFeed gameFeed, User user){
        // abstract method
    }

    public StompFrame create(String[] body){
        return null; // todo :?
    }

    public String toString(){
        String stompFrameStr = "" + stompCommand+ '\n';
        for(String i : stompsHeaders.keySet()){
            stompFrameStr += i + ": " + stompsHeaders.get(i) + '\n';
        }
        //stompFrameStr += '\n';
        // TODO : we need to add body that is not event..!
        // if(!isBodyEmpty){
        //     stompFrameStr += '\n' + body.toString();
        // }
        stompFrameStr += '\u0000';
        return stompFrameStr;
    }

}