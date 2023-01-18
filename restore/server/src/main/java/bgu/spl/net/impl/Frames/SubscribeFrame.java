package bgu.spl.net.impl.Frames;

import bgu.spl.net.impl.stomp.StompFrame;
import bgu.spl.net.impl.stomp.StompCommand;

import java.util.Map;

import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.Game;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;



public class SubscribeFrame extends StompFrame {
    int subscriptionId;
    String destination;

    public SubscribeFrame(Map<String,String> headers) {
        super(StompCommand.SUBSCRIBE, headers);
    }

    @Override
    public StompFrame create(String[] body){
        
        if(!stompsHeaders.containsKey("id")){
            missingHeaders.add("id");;
            errorAccourd = true;
        }
        if(!stompsHeaders.containsKey("destination")){
           missingHeaders.add("destination");;
           errorAccourd = true;
       }
       if(stompsHeaders.containsKey("receipt")){
            receiptId = stompsHeaders.get("receipt");
            errorHeaders.put("receipt", receiptId);
       }

       if(errorAccourd){
            return frameUtil.buildHeaderMissingErrorFrame(errorHeaders,missingHeaders);
       }

        subscriptionId = Integer.parseInt(stompsHeaders.get("id"));
        destination = stompsHeaders.get("destination");
        return this;
    }

    @Override
    public void excute(GamesFeed gamesFeed, User user){
        // to do: add error if can not subscribe??
        destination = destination.toLowerCase();
        // if channel doesn't exsit create
        if(!gamesFeed.allGames.containsKey(destination)){
            gamesFeed.allGames.put(destination, new Game(destination));
        }
        // add the user to the game
        gamesFeed.allGames.get(destination).subscribeNewUser(user.getConnectionId(),subscriptionId);
        // add the game to the user
        user.subscribeToGame(subscriptionId, gamesFeed.allGames.get(destination));
        
        
    }
}
