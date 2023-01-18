package bgu.spl.net.impl.Frames;

import java.util.Map;

import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.impl.stomp.StompCommand;
import bgu.spl.net.impl.stomp.StompFrame;

public class ErrorFrame extends StompFrame{
    String msg;

    public ErrorFrame(Map<String,String> headers, String errorMsg) {
        super(StompCommand.ERROR, headers);   
        msg = errorMsg;
    }

    @Override 
    public StompFrame create(String[] body){
        return this;    
    }

    @Override
    public void excute(GamesFeed gameFeed, User user){
        gameFeed.disconnectUser(user.getUsername());
    }


}
