package bgu.spl.net.impl.gamesUpdate;

import java.util.Map;

public class Event {
    
    private String user;
    private String teamAName;
    private String teamBName;
    private String eventName;
    private int time;
    // private GameUpdateProperties generalGameUpdate;
    // private TeamUpdate teamAUpdate;
    // private TeamUpdate teamBUpdate;
    private Map<String,String> generalGameUpdate;
    private Map<String,String> teamAUpdate;
    private Map<String,String> teamBUpdate;
    private String description;

    public Event(String _user,String _eventName,String _description,int _time,String teamA, String teamB,
                Map<String,String> _generalGameUpdate,Map<String,String> _teamAUpdate,
                Map<String,String> _teamBUpdate){
        user = _user;
        eventName = _eventName;
        teamAName = teamA;
        teamBName = teamB;
        description = _description;
        time = _time;
        generalGameUpdate = _generalGameUpdate;
        teamAUpdate = _teamAUpdate;
        teamBUpdate = _teamBUpdate;
    }

    public String toString(){ 
        String s = "user: " + user + '\n' ;
        s += "team a: " + teamAName +'\n';
        s += "team b: " + teamBName + '\n';
        s += "event name: " + eventName +'\n';
        s += "time: " + time + '\n';
        s += "general game updates:" + '\n';
        if(generalGameUpdate!=null){
            for(String i : generalGameUpdate.keySet()){
                s +='\t'+ i + ": " + generalGameUpdate.get(i) + '\n';
            }
        }
        s += "team a updates:" + '\n';
        if(teamAUpdate!=null){
                for(String i : teamAUpdate.keySet()){
                s +='\t'+ i + ": " + teamAUpdate.get(i) + '\n';
            }
        }
        s += "team b updates:"+'\n' ;
        if(teamBUpdate!=null){
        for(String i : teamBUpdate.keySet()){
            s +='\t'+ i + ": " + teamBUpdate.get(i) + '\n';
            }
        }
        s += "description:" + '\n' + description + '\n';
        return s; 
    }
}

