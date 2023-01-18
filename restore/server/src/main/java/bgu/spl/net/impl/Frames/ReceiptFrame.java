package bgu.spl.net.impl.Frames;

import java.util.Map;

import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.impl.stomp.StompCommand;
import bgu.spl.net.impl.stomp.StompFrame;

public class ReceiptFrame extends StompFrame{
    int receiptId;

    public ReceiptFrame(Map<String,String> headers) {
        super(StompCommand.RECEIPT, headers);
    }

    @Override
    public StompFrame create(String[] body){
        if(!stompsHeaders.containsKey("receipt-id")){
            missingHeaders.add("receipt-id");
            errorAccourd = true;
        }

       if(errorAccourd){
            return frameUtil.buildHeaderMissingErrorFrame(errorHeaders,missingHeaders);
        }
        receiptId = Integer.parseInt(stompsHeaders.get("receipt-id"));
        return this;
    }
    @Override
    public void excute(GamesFeed gamesFeed, User user){
        gamesFeed.connections.send(user.getConnectionId(), this);
    }
}
