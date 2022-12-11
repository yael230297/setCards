package bguspl.set.ex;

import bguspl.set.Env;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import java.util.*;


/**
 * This class manages the dealer's threads and data
 */
public class Dealer implements Runnable {

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

    public Dealer(Env env, Table table, Player[] players) {
        this.env = env;
        this.table = table;
        this.players = players;
        // ???
        deck = IntStream.range(0, env.config.deckSize).boxed().collect(Collectors.toList());
    }

    // todo : remove
    public Dealer() {
        this.env = null;
        this.table = null;
        this.players = null;
        // ???
        deck = null;
    }

    /**
     * The dealer thread starts here (main loop for the dealer thread).
     */
    @Override
    public void run() {
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " starting.");
        while (!shouldFinish()) {
            placeCardsOnTable();
            timerLoop();
            updateTimerDisplay(false);
            removeAllCardsFromTable();
        }
        announceWinners();
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
    }

    /**
     * The inner loop of the dealer thread that runs as long as the countdown did not time out.
     */
    private void timerLoop() {
        while (!terminate && System.currentTimeMillis() < reshuffleTime) {
            sleepUntilWokenOrTimeout(); // ישנים עד שמעירים אותנו- שחקנים מעצבנים / נגמר הזמן
            updateTimerDisplay(false); // מאפסים את הטיימר
            removeCardsFromTable(); // מורידים את הקלפים מהשולחן
            placeCardsOnTable(); // שמים חדשים
        }
    }

    /**
     * Called when the game should be terminated due to an external event.
     */
    public void terminate() {
        // TODO implement
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
        // check if set is legal 
        // if no return 
        // if yes remove 3 cards
        
    }

    /**
     * Check if any cards can be removed from the deck and placed on the table.
     */
    private void placeCardsOnTable() {
        for(int slot=0; slot<table.slotToCard.length; slot++){
            // check if there is an empty slot
            if(table.slotToCard[slot]==null){
            // select the first card from the deck
            Integer cardId = deck.remove(0);
            // place the new card on table
            table.placeCard(cardId, slot);
            }
        }
    }

    /**
     * Sleep for a fixed amount of time or until the thread is awakened for some purpose.
     */
    private void sleepUntilWokenOrTimeout() {
        // TODO implement
    }

    /**
     * Reset and/or update the countdown and the countdown display.
     */
    private void updateTimerDisplay(boolean reset) {
        // TODO implement
        // +1
    }

    /**
     * Returns all the cards from the table to the deck.
     */
    private void removeAllCardsFromTable() {
          // return the cards to the deck
          for(int slot=0; slot<table.slotToCard.length; slot++){
            deck.add(table.slotToCard[slot]);
        }
        this.shuffleDeck();
        // reset the table
        for(int slot=0; slot<table.slotToCard.length; slot++){
            table.slotToCard[slot] = null;
        }    
    }

    /**
     * Check who is/are the winner/s and displays them.
     */
    private void announceWinners() {
        // TODO implement
    }

// ======== from here we created ===== 

    /**
     * Shuffle the cards in the deck.
     */
    private void shuffleDeck() {
        Collections.shuffle(deck);
    }


    /**
     * Find sets and send them to check.
     */
    private boolean findSet() {
        // how to check cards with token
        // 
        return false;
     }

    /**
     * Checks if the set is legal.
     */
    //private boolean isSetLegal(int[] cards) {
        // todo: return to be private
    private boolean isSetLegal(int firstCardId,int secondCardId,int thirdCardId) {
        int[] firstCard = getCardsFeatures(firstCardId);
        int[] secondCard= getCardsFeatures(secondCardId);
        int[] thirdCard= getCardsFeatures(thirdCardId);

        for(int i = 0; i <= 3; i++){
            boolean isLegal = isLegalFeature(firstCard, secondCard, thirdCard, i);
            if(!isLegal){
                return false;
            }
        }
       return true;
    }


    /**
     * Find the card's features.
     */
    private int[] getCardsFeatures(int cardId){
        int[] features = new int[4]; // Todo : change to const value
        int currentValue = cardId;
        for (int i=features.length-1; i>=0; i--){
            features[i] = currentValue/(int)(Math.pow(3, i));
            currentValue = currentValue - features[i]*(int)(Math.pow(3, i));
        }

        return features;
    }

    /**
     * Find if the feature is legal(all the same or all different)
     */
    private boolean isLegalFeature(int [] firstCardFeat, int [] secondCardFeat,int [] thirdCardFeat, int feature){
        
        //todo: make it pretty
        if(firstCardFeat[feature] != secondCardFeat[feature] && 
            secondCardFeat [feature] != thirdCardFeat[feature]&& 
            firstCardFeat[feature] != thirdCardFeat[feature]){
                return true;
            } 
            if(firstCardFeat[feature] == secondCardFeat[feature] && 
            secondCardFeat [feature] == thirdCardFeat[feature]&& 
            firstCardFeat[feature] == thirdCardFeat[feature]){
                return true;
            }
            return false;
    }
        
    
}
