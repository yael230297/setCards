package bgu.spl.net.impl.Frames;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bgu.spl.net.impl.gamesUpdate.Event;
import bgu.spl.net.impl.stomp.StompCommand;
import bgu.spl.net.impl.stomp.StompFrame;

public class FrameUtil {
        /*
     * checks if exsits STOMP command
     */
    public StompFrame createFrame(StompCommand stompCommand,Map<String,String> stompsHeaders,String[] parts){

        try{
    // todo : add a check in every Frame and return Error frame.
    // seperate to another function that checks - if sucseed return ctor
    // if not return ERROR frame
    StompFrame frame;
    switch (stompCommand) {
        case SUBSCRIBE:        
        frame = new SubscribeFrame(stompsHeaders);
        break;
        case UNSUBSCRIBE:
        frame = new UnsubscribeFrame(stompsHeaders);
        break;
        case CONNECT:
        // ConnectFrame connectFrame = new ConnectFrame(stompsHeaders);
        // frame = connectFrame;
        frame = new ConnectFrame(stompsHeaders);
        break;
        case DISCONNECTED:
        frame = new DisconnectFrame(stompsHeaders);
        break;
        case CONNECTED:
        frame = new ConnectedFrame(stompsHeaders);
        break;
        case SEND:
        frame = new SendFrame(stompsHeaders);
        break;
        case MESSAGE:   
        frame = new MessageFrame(stompsHeaders);
        break;
        case RECEIPT:   
        frame = new ReceiptFrame(stompsHeaders);
        break;
        case ERROR:   
        frame = new ErrorFrame(stompsHeaders,"");
        break;
        default:
        frame = new ErrorFrame(stompsHeaders,"");
        break;
    }
    frame.create(parts);
    int x=0;
    return frame;
    }
    catch(Exception ex){
        StompFrame frame = new ErrorFrame(stompsHeaders,"");
        return frame.create(parts);
        }
    }


    public StompFrame buildStompFrame(String stompFrameString){
        String[] parts = stompFrameString.split("\n");
        int i=0;
        String stompCommandString = parts[i++];
        StompCommand stompCommand = toEnum(stompCommandString);
        
        if(stompCommand == StompCommand.INVALID){
            Map<String,String> stompsHeaders = new HashMap<String,String>();
            stompsHeaders.put("msg", "invalid frame header");
            String errorMsg = "invalid stomp frame received: " + stompCommandString;
            return new ErrorFrame(stompsHeaders,errorMsg);
        }
        
        Map<String,String> stompsHeaders = new HashMap<String,String>();
        // headers
        while(i < parts.length && parts[i].length()!=0){
            // parse headers
            String[] headersParts = parts[i++].split(": ");
            stompsHeaders.put(headersParts[0], headersParts[1]);
           }
        
       if(stompCommand == StompCommand.ERROR){return null;} // TODO : handel error
       StompFrame sf;
       if(i+1 <= parts.length){
        sf = this.createFrame(stompCommand, stompsHeaders,Arrays.copyOfRange(parts, ++i, parts.length));}
        else{
        sf = this.createFrame(stompCommand, stompsHeaders,null);}
        return sf;
    }
  
    
    /*
     * checks if exsits STOMP command
     */
    private StompCommand toEnum(String command){
        try{
            StompCommand enumCommand = StompCommand.valueOf(command);
            return enumCommand;
        }
        catch(IllegalArgumentException ex){
            return StompCommand.INVALID;
        }
    }

    /* 
     * parse body content
     */
    public Map<String,String> getBodyContent(String[] body){
        if(body==null){return null;}
        Map<String,String> eventFields = new HashMap<String,String>();
        int i=0;
        while(i<body.length && body[i].length()!=0){
            String[] fieldParts = body[i++].split(":");
            if(fieldParts.length>1)
                eventFields.put(fieldParts[0], fieldParts[1]);
            else{ // an inside object
                if(fieldParts[0].equals("description")){
                    eventFields.put(fieldParts[0],body[i]);
                }
                else{
                    String allObjectProperties="";
                    if(i!=body.length){
                    while(body[i].contains("\t")){
                        allObjectProperties+= body[i++].replace("\t", "\n");
                    }}
                    eventFields.put(fieldParts[0],allObjectProperties);
                }
            }
        }
        return eventFields;
    }

    /* 
     * parse team updates
     */
    public Map<String,String> getTeamUpdates(String teamUpdates){
        int i=0;
        if(teamUpdates==null){return null;}
        Map<String,String> teamFields = new HashMap<String,String>();
        String[] teamUpdatesParts = teamUpdates.split("\n");
        while(i<teamUpdatesParts.length){
            if(teamUpdatesParts[i].length()!=0){
            String[] fieldParts = teamUpdatesParts[i++].split(":");
            teamFields.put(fieldParts[0], fieldParts[1]);
            }
            else{
                i++;
            }

    }
        return teamFields;
    }

    /* 
     * parse game gneral updates
     */
    public Map<String,String> getGameGeneralUpdates(String generalProps){
        if(generalProps==null){return null;}
        Map<String,String> propsFields = new HashMap<String,String>();
        int i=0;
        String[] teamUpdatesParts = generalProps.split("\n");
        while(i<teamUpdatesParts.length){
            String[] fieldParts = teamUpdatesParts[i++].split(":");
            if(fieldParts.length>1)
            propsFields.put(fieldParts[0], fieldParts[1]);
        }
        return propsFields;
    }

    /* 
     * ...
     */
    public Event assignToFields(Map<String,String> bodyFields, Map<String,String> generalGameUpdate, 
    Map<String,String> teamAUpdate,Map<String,String> teamBUpdate){
        String[] timeStr = bodyFields.get("time").split(" ");
        int time = Integer.parseInt(timeStr[1]);
        // TODO : handle the situation there is no valid event in body
        return new Event(bodyFields.get("user"), bodyFields.get("event name"),bodyFields.get("description"),
            time, bodyFields.get("team a"),bodyFields.get("team b"),generalGameUpdate, teamAUpdate, teamBUpdate);
    }
    
    public StompFrame buildErrorFrameForCases(String receipt , String errorMsg){
        StompCommand command = StompCommand.ERROR;
        Map <String, String> headersForError = new HashMap<String, String>();
        headersForError.put("message", errorMsg);
        if(receipt != null){
            headersForError.put("receipt-id", receipt);
        } 
        return createFrame(command, headersForError, null);
    }

    public StompFrame buildHeaderMissingErrorFrame(Map<String,String> errorHeaders, 
    List<String> missingHeaders){
        errorHeaders.put("message", "malformed frame received");
        String msg = "Did not contain ";
        for(String missingHeader : missingHeaders){
            msg += "a " + missingHeader + "header, ";
        }
        msg += "\n which is REQUIRED for message propagation";

        return new ErrorFrame(errorHeaders, msg);
    }
}
