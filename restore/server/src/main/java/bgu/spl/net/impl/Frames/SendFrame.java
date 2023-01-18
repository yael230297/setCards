package bgu.spl.net.impl.Frames;

import java.util.HashMap;
import java.util.Map;

import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.Event;
import bgu.spl.net.impl.gamesUpdate.Game;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.impl.stomp.StompCommand;
import bgu.spl.net.impl.stomp.StompFrame;

public class SendFrame extends StompFrame{
    String destination;
    Event event; // maybe a list

    public SendFrame(Map<String,String> headers) {
        super(StompCommand.SEND, headers);
    }

    @Override 
    public StompFrame create(String[] body){
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

        destination = stompsHeaders.get("destination");

         // frame body
        Map<String,String> bodyFields = frameUtil.getBodyContent(body);
        Map<String,String> props = frameUtil.getGameGeneralUpdates(bodyFields.get("general game updates"));
        Map<String,String> aUpdates = frameUtil.getTeamUpdates(bodyFields.get("team A updates"));
        Map<String,String> bUpdates = frameUtil.getTeamUpdates(bodyFields.get("team B updates"));
 
        event = frameUtil.assignToFields(bodyFields, props, aUpdates, bUpdates);
        return this;
    }

    @Override
    public void excute(GamesFeed gamesFeed, User user){
        Game currentGame = gamesFeed.allGames.get(destination.toLowerCase());
        errorAccourd = false;
        String errorStr="";

        // check if regeister to channel and channel exsits
        // if not - return error.
        if(currentGame == null){
            errorStr = "game doesn't exists";
            errorAccourd = true;
        } 
        
        if(!currentGame.getSubscriptionUsers().containsKey(user.getConnectionId())){
            errorStr = "client doesn't subscribed";
            errorAccourd = true;
        }
        receiptId = null;
        if(stompsHeaders.containsKey("receipt-id")){
            receiptId = stompsHeaders.get("receipt-id");
        }
        
        if(errorAccourd){
            StompFrame error = frameUtil.buildErrorFrameForCases(receiptId, errorStr);
            error.excute(gamesFeed, user);
            return;
        }

        gamesFeed.messageindex++;
        currentGame.addEvent(event);
        // send message for all subscribed users
        StompFrame msg = buildMessageFrame(gamesFeed.messageindex, user);
        int x=0;
        // excute the message frame that will send to all users subscribed
        msg.excute(gamesFeed, user);
       
    }

    private StompFrame buildMessageFrame(int messageindex, User user){
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("destination", destination);
        headers.put("message-id", "" + messageindex); 
        headers.put("subscription", "" + user.getSubscriptionId(destination));

        return frameUtil.createFrame(StompCommand.MESSAGE,headers,event.toString().split("\n"));
    }

}
