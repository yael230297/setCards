package bgu.spl.net.impl.gamesUpdate;

import java.io.Serializable;
import bgu.spl.net.impl.stomp.StompFrame;


import bgu.spl.net.impl.rci.Command;

public class FetchGameCommand implements Command<StompFrame>{
    
    public Serializable execute(StompFrame arg){
        return null;
    }
}
