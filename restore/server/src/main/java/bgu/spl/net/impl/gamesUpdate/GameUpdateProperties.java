package bgu.spl.net.impl.gamesUpdate;

import java.util.Map;

public class GameUpdateProperties {
    boolean active;
    boolean beforeHalfTime;

    public GameUpdateProperties(Map<String,String> props){
        if(props.containsKey("active")){
            active = props.get("active")=="true";
        }
        if(props.containsKey("beforeHalfTime")){
            beforeHalfTime = props.get("beforeHalfTime")=="true";
        }
    }
}
