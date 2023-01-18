package bgu.spl.net.impl.gamesUpdate;

import java.io.Serializable;
import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.impl.stomp.StompFrame;

public class PublishUpdateCommand implements Command<StompFrame>{
    
    public Serializable execute(StompFrame arg){
        return null;
    }
    
}
