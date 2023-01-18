package bgu.spl.net.impl.gamesUpdate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
   // todo : add games details
   String gameName;
   List<Event> events;
   //List<Integer> usersSubscribed;
   Map<Integer,Integer> usersConnectionsIdsAndSubscriptionIds;


   public Game(String name/*todo: add game details */) {
      gameName = name;
      events = new ArrayList<Event>();
      usersConnectionsIdsAndSubscriptionIds = new HashMap<Integer,Integer>();
   }

   public void subscribeNewUser(int connectionId, int subscriptionId){
      usersConnectionsIdsAndSubscriptionIds.put(connectionId,subscriptionId);
   }

   public void unsubscribeUser(int connectionId){
      usersConnectionsIdsAndSubscriptionIds.remove(connectionId);
   }

   public String getName(){
      return gameName;
   }

   public int getSubscriptionId(int connectionId){
      return usersConnectionsIdsAndSubscriptionIds.get(connectionId);
   }

   public Map<Integer,Integer> getSubscriptionUsers(){
      return usersConnectionsIdsAndSubscriptionIds;
   }

   public void addEvent(Event newEvent){
      events.add(newEvent);
   }
}
