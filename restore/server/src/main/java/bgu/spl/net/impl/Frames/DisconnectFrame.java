package bgu.spl.net.impl.Frames;

import java.util.Map;

import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.impl.stomp.StompCommand;
import bgu.spl.net.impl.stomp.StompFrame;

public class DisconnectFrame extends StompFrame {

    int receipt;

    public DisconnectFrame(Map<String,String> headers) {
        super(StompCommand.DISCONNECTED, headers);
    }

    @Override 
    public StompFrame create(String[] body){
        if(!stompsHeaders.containsKey("receipt")){
            missingHeaders.add("receipt");
            errorAccourd = true;
        }

        if(errorAccourd){
            return frameUtil.buildHeaderMissingErrorFrame(errorHeaders,missingHeaders);
        }

        return this;
    }


    @Override
    public void excute(GamesFeed gamesFeed, User user){
        // checks if the user is connected - if is'nt - sends an error frame 
        if(!user.getIsConnected()){
            receiptId = null;
            if(stompsHeaders.containsKey("receipt")){
                receiptId = stompsHeaders.get("receipt");
            }
            String errorStr = "user isn't connected";
            StompFrame error = frameUtil.buildErrorFrameForCases(receiptId, errorStr);
            error.excute(gamesFeed, user);
            return;
        }
        
        gamesFeed.disconnectUser(user.getUsername());
    }
}
