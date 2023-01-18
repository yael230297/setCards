package bgu.spl.net.srv;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import bgu.spl.net.impl.gamesUpdate.Game;
import bgu.spl.net.impl.stomp.StompFrame;

public class ConnectionsImpl implements Connections<StompFrame> {
    Map<String,Game> games;
    public Map<Integer, ConnectionHandler<StompFrame>> connectionsMap = 
    new HashMap<Integer,ConnectionHandler<StompFrame>>();
    int connectionId;

    public ConnectionsImpl(Map<String,Game> allGames) {
        games = allGames;
        connectionId = 0;
    }

    @Override
    public boolean send(int connectionId, StompFrame msg){
        ConnectionHandler<StompFrame> x = connectionsMap.get(connectionId);
        x.send(msg);
        return true;
    }

    @Override
    public void send(String channel, StompFrame msg){
        Game currentGame = games.get(channel.toLowerCase());
        Set<Integer> connectionsIds = currentGame.getSubscriptionUsers().keySet();
        for(int connectionId : connectionsIds){
            send(connectionId, msg);
        }   
    }

    @Override
    public void disconnect(int connectionId){
        // remove from connected client
        try{
            connectionsMap.get(connectionId).close();
            connectionsMap.remove(connectionId);
        }
        catch(IOException e){

        }
    }

    @Override
    public int addNewConnection(ConnectionHandler<StompFrame> handler){
        connectionsMap.put(++connectionId, handler);
        return connectionId;
    }

}
