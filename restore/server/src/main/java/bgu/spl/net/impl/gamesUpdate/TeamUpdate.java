package bgu.spl.net.impl.gamesUpdate;

import java.util.Map;

public class TeamUpdate {
    public int goals;
    public int possession;

    public TeamUpdate(int _goals, int _possession) {
        goals = _goals;
        possession = _possession;
    }

    public TeamUpdate(Map<String,String> fields) {
        if(fields.containsKey("goals")){
            goals = Integer.parseInt(fields.get("goals"));
        }
        if(fields.containsKey("possession")){
            possession = Integer.parseInt(fields.get("possession"));
        }
    }
}
