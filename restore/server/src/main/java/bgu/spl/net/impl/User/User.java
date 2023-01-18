package bgu.spl.net.impl.User;

import bgu.spl.net.impl.gamesUpdate.Game;

import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class User {
     String username;
     String password;
     boolean isConnected;
     int connectionId;
     Map<Integer, Game> idsAndSubscribedGames;


     public User(int receivedConnectionId) {
          connectionId = receivedConnectionId;
          isConnected = false;
          username = null;
          password = null;
          idsAndSubscribedGames = new HashMap<>();
     }

     public boolean checkIfSubscribed(int subsciptionId){
          return idsAndSubscribedGames.containsKey(subsciptionId);
     }

     public int getConnectionId(){
          return connectionId;
     }
     public boolean getIsConnected(){
          return isConnected;
     }
     public String getPassword(){
          return password;
     }
     public String getUsername(){
          return username;
     }

     public Set<Integer> getSubscribedGamesIds(){
          return idsAndSubscribedGames.keySet();
     }

     public String getGameName(int subscriptionId){
          return idsAndSubscribedGames.get(subscriptionId).getName();
     }

     public void setDetails(String login, String passcode){
          username = login;
          password = passcode;
     }

     public int getSubscriptionId(String name){   
          for(Entry<Integer, Game> entry: idsAndSubscribedGames.entrySet()) {
               if(entry.getValue().getName().equals(name.toLowerCase())) {
                    return entry.getKey();
               }
          }
          // todo : error ? maybe we dont nees because we only use thos function for subscribed users.
          return -1;
     }

     public void subscribeToGame(int subscriptionId, Game destinationGame){
          idsAndSubscribedGames.put(subscriptionId, destinationGame);
     }

     public void unsubscribeToGame(int subscriptionId){
          idsAndSubscribedGames.remove(subscriptionId);
     }

     public void connect(){
          isConnected = true;
          idsAndSubscribedGames = new HashMap<Integer,Game>();
     }

     public void disconnect(){
          isConnected = false;
          idsAndSubscribedGames = null;
     }
}
