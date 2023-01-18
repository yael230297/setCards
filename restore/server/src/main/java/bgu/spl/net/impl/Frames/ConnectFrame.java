package bgu.spl.net.impl.Frames;

import bgu.spl.net.impl.User.User;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.impl.stomp.StompCommand;
import bgu.spl.net.impl.stomp.StompFrame;

import java.util.HashMap;
import java.util.Map;

public class ConnectFrame extends StompFrame{
    String acceptVersion;
    String host;
    String login;
    String passcode;

    public ConnectFrame(Map<String,String> headers) {
        super(StompCommand.CONNECT, headers);
    }

    @Override 
    public StompFrame create(String[] body){
         if(!stompsHeaders.containsKey("accept-version")){
            missingHeaders.add("accept-version");
            errorAccourd = true;
        }
        if(!stompsHeaders.containsKey("host")){
            missingHeaders.add("host");
            errorAccourd = true;
            
        }
        if(!stompsHeaders.containsKey("login")){
           missingHeaders.add("login");
           errorAccourd = true;
        }
        if(!stompsHeaders.containsKey("passcode")){
           missingHeaders.add("passcode");
           errorAccourd = true;
        }
        if(stompsHeaders.containsKey("receipt-id")){
            receiptId = stompsHeaders.get("receipt-id");
            errorHeaders.put("receipt-id", receiptId);
       }

       if(errorAccourd){
            return frameUtil.buildHeaderMissingErrorFrame(errorHeaders,missingHeaders);
       }

        login = stompsHeaders.get("login");
        passcode = stompsHeaders.get("passcode");
        acceptVersion = stompsHeaders.get("accept-version");
        host = stompsHeaders.get("host");
        return this;
    }


    @Override
    public void excute(GamesFeed gamesFeed, User user){
        Map<String,User> users = gamesFeed.allUsers;
        String errorStr="";
        errorAccourd = false;
        
        // new connection
        if(user.getUsername()==null){
            user.setDetails(login, passcode);
        }

        //checks if the password is worng or if this user is already logged in
        // and sends an Error frame 
        if(users.keySet().contains(login)){
            User currUser = users.get(login);
            if(currUser.getIsConnected()){
                errorAccourd = true;
                errorStr = "User already logged in";
            }
            else if(user.getPassword() != currUser.getPassword()){
                errorStr = "Wrong password";
                errorAccourd = true;
            }
        }

        receiptId = null;
        if(stompsHeaders.containsKey("receipt")){
            receiptId = stompsHeaders.get("receipt");
        }

        if(errorAccourd){
            StompFrame error = frameUtil.buildErrorFrameForCases(receiptId, errorStr);
            error.excute(gamesFeed, user);
            return;
        }

        user.connect();
        gamesFeed.allUsers.put(login,user); // move to connectFrame?
        StompFrame msg = buildConnectedFrame();
        // excute the connected frame that will be send to the connected user
        msg.excute(gamesFeed, user);
    }

    private StompFrame buildConnectedFrame(){
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("version", "1.2");

        return frameUtil.createFrame(StompCommand.CONNECTED,headers,null);
    }
}
