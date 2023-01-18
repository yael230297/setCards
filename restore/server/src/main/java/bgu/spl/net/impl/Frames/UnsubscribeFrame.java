package bgu.spl.net.impl.Frames;

import bgu.spl.net.impl.stomp.StompFrame;
import bgu.spl.net.impl.stomp.StompCommand;
import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;

import java.util.Map;

public class UnsubscribeFrame extends StompFrame {
    int subscriptionId;

    public UnsubscribeFrame(Map<String,String> headers) {
        super(StompCommand.UNSUBSCRIBE, headers);
    }

    @Override
    public StompFrame create(String[] body){
        if(!stompsHeaders.containsKey("id")){
            // todo : 
            missingHeaders.add("id");;
            errorAccourd = true;
        }
        if(stompsHeaders.containsKey("receipt-id")){
            receiptId = stompsHeaders.get("receipt-id");
            errorHeaders.put("receipt-id", receiptId);
       }
        if(errorAccourd){
            return frameUtil.buildHeaderMissingErrorFrame(errorHeaders,missingHeaders);
       }

        subscriptionId = Integer.parseInt(stompsHeaders.get("id"));
        return this;
    }

    @Override
    public void excute(GamesFeed gamesFeed, User user){
        // check if client is subscribed to  that channel- if is'nt return error frame
        if(!user.checkIfSubscribed(subscriptionId)){
            String errorStr = "user isn't subscribe to that channel";
            String receiptId = null;
            if(stompsHeaders.containsKey("receipt")){
                receiptId = stompsHeaders.get("receipt");
            }
            StompFrame error = frameUtil.buildErrorFrameForCases(receiptId, errorStr);
            error.excute(gamesFeed, user);
            return;
        }
        
        String name = user.getGameName(subscriptionId);
        // remove user from game
        gamesFeed.allGames.get(name).unsubscribeUser(user.getConnectionId());
        // remove game from user
        user.unsubscribeToGame(subscriptionId);
    }
}
