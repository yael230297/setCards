package bguspl.set.ex;

import bguspl.set.Env;
import bguspl.set.UtilImpl;
import bguspl.set.WindowManager;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//import org.omg.CORBA.INTERNAL;

/**
 * This class manages the dealer's threads and data
 */
public class Dealer implements Runnable {
    int SIXTY_SECONDS_IN_MILIES = 60000;

    /**
     * The game environment object.
     */
    private final Env env;

    /**
     * Game entities.
     */
    private final Table table;
    private final Player[] players;

    /**
     * The list of card ids that are left in the dealer's deck.
     */
    private final List<Integer> deck;

    /**
     * True if game should be terminated due to an external event.
     */
    private volatile boolean terminate;

    /**
     * The time when the dealer needs to reshuffle the deck due to turn timeout.
     */
    private long reshuffleTime = Long.MAX_VALUE;

    // ===== we added ====

    /**
     * The array of cards that needs be removed - save as cardId.
     */
    private LinkedList<Integer> cardsToRemove;

    /**
     * The current second of the current turn.
     */
    long currentSecond;

    /**
     * The queue for set checks requests (holds players id).
     */
    private BlockingQueue<Integer> setQueue;

    private Thread dealerThread;

    /**
     * field that represent if the dealer needs to update the table (after remove cards\ in the first round) 
     */
    private boolean needToUpdate;
    
    private int[] playerFreeze;
    
    Object playerFreezeMonitor = new Object();

    public Dealer(Env env, Table table, Player[] players) {
        this.env = env;
        this.table = table;
        this.players = players;
        reshuffleTime = System.currentTimeMillis() + SIXTY_SECONDS_IN_MILIES;
        deck = IntStream.range(0, env.config.deckSize).boxed().collect(Collectors.toList());
        
        // ours // 
        cardsToRemove = new LinkedList<>();
        currentSecond = SIXTY_SECONDS_IN_MILIES;
        needToUpdate = true;
        playerFreeze = new int[env.config.players];
        for(int i=0;i<playerFreeze.length;i++){
            playerFreeze[i]=0;
        }
        this.setQueue = new LinkedBlockingQueue<>();
    }

    /**
     * The dealer thread starts here (main loop for the dealer thread).
     */
    @Override
    public void run() {
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " starting.");
        // creates and run the players threads s
        dealerThread = Thread.currentThread();
        runPlayers();
        while (!shouldFinish()) {     
            placeCardsOnTable();
            table.updateIsReachable(true);
            timerLoop();
            updateTimerDisplay(true);
            table.updateIsReachable(false);
            removeAllCardsFromTable();
        }
        announceWinners();
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
    }

    /**
     * The inner loop of the dealer thread that runs as long as the countdown did not time out.
     */
    private void timerLoop() {
        reshuffleTime = System.currentTimeMillis() + SIXTY_SECONDS_IN_MILIES;
        updateTimerDisplay(false);
        
        while (!terminate && System.currentTimeMillis() < reshuffleTime) {
            sleepUntilWokenOrTimeout();
            updatePlayers();
            updateTimerDisplay(false);
            table.updateIsReachable(false);
            removeCardsFromTable();
            placeCardsOnTable();
            table.updateIsReachable(true);
        }
    }

    /**
     * Called when the game should be terminated due to an external event.
     */
    public void terminate() {
        // close all players thread - call the terminate function
        for(Player player : players){
            player.terminate();
            try{player.getThread().join(); } catch(InterruptedException ex){}
        }
        terminate = true; 
    }
    

    /**
     * Check if the game should be terminated or the game end conditions are met.
     *
     * @return true iff the game should be finished.
     */
    private boolean shouldFinish() {
        return terminate || env.util.findSets(deck, 1).size() == 0;
    }

    /**
     * Checks cards should be removed from the table and removes them.
     */
    private void removeCardsFromTable() {
        synchronized(table.tableMonitor){
            if(cardsToRemove.isEmpty()){return;}
            LinkedList<Integer>[] tokens = table.getPlayersTokens(); 
            // for each card that needs to be remove:
            for(int cardId : cardsToRemove){            
                // remove tokens
                int slot = table.cardToSlot[cardId];
                if(table.slotToCard[slot]!=null){
                    for(int playerId=0;playerId<players.length;playerId++){
                        if(tokens[playerId].contains(slot)){
                            // remove token from the table field and the player's field
                            players[playerId].removeToken(slot);
                        }
                    }
                    // remove card
                    table.removeCard(slot);
                }
            }
            cardsToRemove.clear();
            needToUpdate = true;
        }
    }

    /**
     * Check if any cards can be removed from the deck and placed on the table.
     */
    private void placeCardsOnTable() {
        Collections.shuffle(deck);
        synchronized(table.tableMonitor){
            if(!needToUpdate){return;}
            for(int slot=0; slot<table.slotToCard.length;slot++){
                if(table.slotToCard[slot]==null){
                    //check if there is no more cards to draw
                    if(!deck.isEmpty()){ 
                        int cardId = deck.remove(0);
                        this.table.placeCard(cardId, slot);
                    }
                }
            }
            needToUpdate=false;
        }
    }

    /**
     * Sleep for a fixed amount of time or until the thread is awakened for some purpose.
     */
    private void sleepUntilWokenOrTimeout() {
        //sleep until timeout 
        try{Thread.sleep(950);}
        // thread interrupted - there is a set to check.
        catch(InterruptedException ex){
            checkSet();
        }
    }

    /**
     * Reset and/or update the countdown and the countdown display.
     */
    private void updateTimerDisplay(boolean reset) {
        if(reset){
            reshuffleTime = System.currentTimeMillis() + SIXTY_SECONDS_IN_MILIES;
            env.ui.setCountdown(reshuffleTime - System.currentTimeMillis(), terminate);
        }else{
            env.ui.setCountdown(reshuffleTime - System.currentTimeMillis(), terminate);
        }
    }

    /**
     * Update the players seconds that they need to wait.
     */
    private void updatePlayers(){
        synchronized(playerFreezeMonitor){
            for(Player player : players){
                int playerId = player.id;
                
                if(playerFreeze[playerId]>0){
                    playerFreeze[playerId]--;
                    env.ui.setFreeze(playerId, playerFreeze[playerId]*1000);
                    
                    if(playerFreeze[playerId]==0){
                        player.wakeUp();
                    }
                }
            }
        }
    }

    /**
     * Returns all the cards from the table to the deck.
     */
    private void removeAllCardsFromTable() {
        // remove all tokens
        synchronized(table.tableMonitor){
            removeAllTokensFromTable(table.getPlayersTokens());
            for(int i=0; i< table.slotToCard.length;i++){
                int cardId = table.slotToCard[i];
                int slot = table.cardToSlot[cardId];
                // remove card and return it to the deck
                if(table.slotToCard[slot]!=null){
                    deck.add(cardId);
                    table.removeCard(slot);
                }
            }
            needToUpdate=true;
        } 
    }

    /**
     * Check who is/are the winner/s and displays them.
     */
    // TODO : make it pretty
    private void announceWinners() {
        int maxScore = 0;
       
        //find the max score
        for(Player player : players){
            if(player.score() > maxScore){
                maxScore = player.score();
            }
        }
        // count winners
        int countWinners = 0;
        for (int winner=0; winner<players.length;winner++) {
            if(players[winner].score()==maxScore){
                countWinners++;
            }
        }
        // create winners array
        int[] winners = new int[countWinners];
        int i = 0;
        for (int winner=0; winner<players.length;winner++) {
            if(players[winner].score()==maxScore){
                winners[i] = players[winner].id;
                i++;
            }
        }
        // display the winner/s
        env.ui.announceWinner(winners);
    }

    // ======= ours =======
    /**
     * Remove all tokens, update in players fields and in the table field.
     */
    public void removeAllTokensFromTable(LinkedList<Integer>[] tokens){
        for(Player player : players){
            player.clearTokens();
        }
        table.removeAllToken();
    }

    /**
     * Remove token from card and update in players fields and in the table field.
     */
    public void removeTokensFromCard(LinkedList<Integer>[] tokens, int slot){
        LinkedList<Player> playersOnCard = table.getPlayersOnCard(slot);
        for(Player player : playersOnCard){
            player.removeToken(slot);
        }
    }

    /**
     * creates and run the players threads
     */
    public void runPlayers(){
        for(Player player : players){
            player.setThread(new Thread(player)); 
            player.startThread();
        }
    }

    
    public void setThread(Thread thread){
        dealerThread = thread;
    }
    public Thread getThread(){
        return dealerThread;
    }

    /**
     * check the set of the first player that asked for it.
     * if he was right  -> give him a point, remove his cards and add time for freeze. 
     * other -> give him a penelty and add time for freeze.
     */
    
    public void checkSet(){
        
        int playerId = setQueue.poll();
        int [] set = table.getPlayerTokens(playerId);
        // if two players put tokens on the same legal set, the last player that put the token
        // may have a set that is smaler then three. 
        // TODO : make it pretty
        if(set.length < 3){ return; }
        
        boolean isLegalSet = env.util.testSet(set);
        synchronized(playerFreezeMonitor){
            if(isLegalSet){
                playerFreeze[playerId] += env.config.pointFreezeMillis/1000;
                players[playerId].point();
                for(int cardId : set){
                    cardsToRemove.add(cardId);
                }
            }
            else{
                playerFreeze[playerId] += env.config.penaltyFreezeMillis/1000;
                players[playerId].penalty();
            }
        }
    }

    /**
     * add the check set request to the set checks queue. 
     */
    public void askToCheckSet(int playerId){
        setQueue.add(playerId);
    }
}
