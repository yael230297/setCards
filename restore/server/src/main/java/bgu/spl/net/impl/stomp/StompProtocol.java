package bgu.spl.net.impl.stomp;

import java.util.HashMap;
import java.util.Map;

import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.impl.Frames.FrameUtil;
import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.srv.Connections;

public class StompProtocol implements StompMessagingProtocol<StompFrame> { 
    
    boolean shouldFinish = false;
    GamesFeed gamesFeed;
    User connectedUser;
    FrameUtil frameUtil = new FrameUtil();
    
    public StompProtocol(GamesFeed feed) {
        gamesFeed = feed;
        connectedUser = null;
    }

    @Override
    public void start(int connectionId, Connections<StompFrame> connections){
        if(connectedUser != null && connectedUser.getIsConnected()){
            String errorStr = "The client is already logged in, log out before trying again";
            StompFrame error = frameUtil.buildErrorFrameForCases(null, errorStr); // todo : receipt ???
            error.excute(gamesFeed, connectedUser);
            return;
        }
        connectedUser = new User(connectionId);
    }
    
    @Override
    public void process(StompFrame message){
        try{
            message.excute(gamesFeed, connectedUser);
        }catch(Exception ex){
            // return ErrorFrame
        }

        // return receiptFrame if needed - after the exctue sucseed
        if(message.stompsHeaders.containsKey("receipt")){
            Map<String,String> receiptHeader = new HashMap<String,String>();
            receiptHeader.put("receipt-id", message.stompsHeaders.get("receipt"));
            StompFrame receiptAns = frameUtil.createFrame(StompCommand.RECEIPT, receiptHeader, null);
            receiptAns.excute(gamesFeed, connectedUser);
        }
    }
	
    @Override
    public boolean shouldTerminate(){
        //todo ?
        return shouldFinish;
    }

    public Connections<StompFrame> getConnections(){
        return gamesFeed.connections;
    }
}
