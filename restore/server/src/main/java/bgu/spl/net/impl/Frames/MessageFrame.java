package bgu.spl.net.impl.Frames;

import java.util.Map;

import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.Event;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.impl.stomp.StompCommand;
import bgu.spl.net.impl.stomp.StompFrame;

public class MessageFrame extends StompFrame{
   
    String destination;
    String subscription;
    int msgId;
    Event event; // maybe a list ?

    public MessageFrame(Map<String,String> headers) {
        super(StompCommand.MESSAGE, headers);
    }

    @Override
    public StompFrame create(String[] bodyFields){
        if(!stompsHeaders.containsKey("message-id")){
            missingHeaders.add("message-id");
            errorAccourd = true;
        }
        if(!stompsHeaders.containsKey("subscription")){
            missingHeaders.add("subscription");
            errorAccourd = true;
        }
        if(!stompsHeaders.containsKey("destination")){
            missingHeaders.add("destination");
            errorAccourd = true;
        }

        if(stompsHeaders.containsKey("receipt")){
            receiptId = stompsHeaders.get("receipt");
            errorHeaders.put("receipt", receiptId);
       }
       if(errorAccourd){
            return frameUtil.buildHeaderMissingErrorFrame(errorHeaders,missingHeaders);
       }

       msgId = Integer.parseInt(stompsHeaders.get("message-id"));
       subscription = stompsHeaders.get("subscription");
       destination = stompsHeaders.get("destination");

        // frame body
        FrameUtil frameUtil = new FrameUtil();
        Map<String,String> bodyheaders = frameUtil.getBodyContent(bodyFields);
        Map<String,String> props = frameUtil.getGameGeneralUpdates(bodyheaders.get("general game updates"));
        Map<String,String> aUpdates = frameUtil.getTeamUpdates(bodyheaders.get("team A updates"));
        Map<String,String> bUpdates = frameUtil.getTeamUpdates(bodyheaders.get("team B updates"));

        event = frameUtil.assignToFields(bodyheaders, props, aUpdates, bUpdates);
        body = event;
        return this;
    }


    @Override
    public void excute(GamesFeed gamesFeed, User user){
        gamesFeed.connections.send(destination, this);
    }
}
