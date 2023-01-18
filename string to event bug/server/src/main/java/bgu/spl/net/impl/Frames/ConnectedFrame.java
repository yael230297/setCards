package bgu.spl.net.impl.Frames;

import java.util.Map;

import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.impl.stomp.StompCommand;
import bgu.spl.net.impl.stomp.StompFrame;

public class ConnectedFrame extends StompFrame{
    String version;

    public ConnectedFrame(Map<String,String> headers) {
        super(StompCommand.CONNECTED, headers);
    }

    @Override 
    public StompFrame create(String[] body){
        if(!stompsHeaders.containsKey("version")){
            missingHeaders.add("version");
            errorAccourd = true;
        }
        if(stompsHeaders.containsKey("receipt")){
            receiptId = stompsHeaders.get("receipt");
            errorHeaders.put("receipt", receiptId);
       }
       if(errorAccourd){
            return frameUtil.buildHeaderMissingErrorFrame(errorHeaders,missingHeaders);
       }

        version = stompsHeaders.get("version");
        return this;
    }


    @Override
    public void excute(GamesFeed gamesFeed, User user){
        gamesFeed.connections.send(user.getConnectionId(), this);
    }
}

