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
    public StompFrame create(String[] body){
        if(!stompsHeaders.containsKey("msgId")){
            missingHeaders.add("msgId");
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

       msgId = Integer.parseInt(stompsHeaders.get("msgId"));
       subscription = stompsHeaders.get("subscription");
       destination = stompsHeaders.get("destination");

        // frame body
        FrameUtil frameUtil = new FrameUtil();
        Map<String,String> bodyFields = frameUtil.getBodyContent(body);
        Map<String,String> props = frameUtil.getGameGeneralUpdates(bodyFields.get("general game updates"));
        Map<String,String> aUpdates = frameUtil.getTeamUpdates(bodyFields.get("team A updates"));
        Map<String,String> bUpdates = frameUtil.getTeamUpdates(bodyFields.get("team B updates"));

        event = frameUtil.assignToFields(bodyFields, props, aUpdates, bUpdates);

        return this;
    }


    @Override
    public void excute(GamesFeed gamesFeed, User user){
        gamesFeed.connections.send(destination, this);
    }
}
