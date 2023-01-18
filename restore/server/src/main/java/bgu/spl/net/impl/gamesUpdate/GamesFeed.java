package bgu.spl.net.impl.gamesUpdate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import bgu.spl.net.impl.User.User;
import bgu.spl.net.srv.ConnectionsImpl;

public class GamesFeed {
    
    public Map<String,User> allUsers;
    public Map<String,Game> allGames;
    public int messageindex;
    public ConnectionsImpl connections;

    public GamesFeed() {
        allUsers = new HashMap<String,User>();
        allGames = new HashMap<String,Game>();
        messageindex = 0;
        connections = new ConnectionsImpl(allGames);
    }

    public void disconnectUser(String name){
        User user = allUsers.get(name);
        Set<Integer> ids = user.getSubscribedGamesIds();
        for(Integer id : ids){
            Game gameSubscribed = allGames.get(user.getGameName(id));
            gameSubscribed.unsubscribeUser(user.getConnectionId());;
        }
        user.disconnect();
        connections.disconnect(user.getConnectionId());
    }

}
