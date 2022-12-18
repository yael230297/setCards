package bguspl.set.ex;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import bguspl.set.Env;
import bguspl.set.UserInterfaceDecorator;
import bguspl.set.UserInterfaceSwing;
import bguspl.set.UtilImpl;

/**
 * This class manages the players' threads and data
 *
 * @inv id >= 0
 * @inv score >= 0
 */
public class Player implements Runnable {
    int MAX_TOKENS = 3;
    /**
     * The game environment object.
     */
    private final Env env;

    /**
     * Game entities.
     */
    private final Table table;

    /**
     * The id of the player (starting from 0).
     */
    public final int id;

    /**
     * The thread representing the current player.
     */
    private Thread playerThread;

    /**
     * The thread of the AI (computer) player (an additional thread used to generate key presses).
     */
    private Thread aiThread;

    /**
     * True iff the player is human (not a computer player).
     */
    private final boolean human;

    /**
     * True iff game should be terminated due to an external event.
     */
    private volatile boolean terminate;

    /**
     * The current score of the player.
     */
    
    private int score;

    // ours //

    private Dealer dealer;  

    /**
     * Queue for pressed keys
     */
    // TODO : check if we need to lock this queue.
    private BlockingQueue<Integer> keyPressedQueue;

    /**
     * holds the player's tokens.
     */
    private LinkedList<Integer> myTokens;

     /**
     * Monitor for synchronaize the ai and player threads.
     */
    private final Object monitor = new Object();


     /**
     * Should the player sleep;
     */
    private boolean needToSleep;


    /**
     * The class constructor.
     *
     * @param env    - the environment object.
     * @param dealer - the dealer object.
     * @param table  - the table object.
     * @param id     - the id of the player.
     * @param human  - true iff the player is a human player (i.e. input is provided manually, via the keyboard).
     */
    public Player(Env env, Dealer dealer, Table table, int id, boolean human) {
        this.env = env;
        this.table = table;
        this.id = id;
        this.human = human;
        this.keyPressedQueue = new LinkedBlockingQueue<Integer>();
        this.myTokens = new LinkedList<>();
        this.dealer = dealer;
        this.needToSleep = false;

    }

    /**
     * The main player thread of each player starts here (main loop for the player thread).
     */
    @Override
    public void run() {
        playerThread = Thread.currentThread();
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + "starting.");
        if (!human) createArtificialIntelligence();
            while (!terminate) {
                
                if(keyPressedQueue.isEmpty()){
                    // sleep until key pressed
                    //try { monitor.wait(); } catch (InterruptedException ignored) {}
                }
                else{
                    Integer slot = keyPressedQueue.poll();
                    if(slot != null){
                        keyPressed(slot);
                        //monitor.notify();
                    }
                }
            }
        
        if (!human) try { aiThread.join(); } catch (InterruptedException ignored) {}
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
    }

    /**
     * Creates an additional thread for an AI (computer) player. The main loop of this thread repeatedly generates
     * key presses. If the queue of key presses is full, the thread waits until it is not full.
     */
    // TODO : need to check why this thread run only once.
    private void createArtificialIntelligence() {
        // note: this is a very very smart AI (!)
        aiThread = new Thread(() -> {
            env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " starting.");
                while (!terminate) {
                    // create key press - random number from 0-11.
                    int slot = (int)(Math.random()*11);
                    // push to queue
                synchronized(monitor){
                    if(keyPressedQueue.size() < 3){
                        keyPressedQueue.add(slot);
                        monitor.notify();
                    }
                
                    if(keyPressedQueue.size()==3){ 
                    try {
                        monitor.wait();
                    } catch (InterruptedException ignored) {}
                    }
                }
            }
            env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
        }, "computer-" + id);
        aiThread.start();
    }

    /**
     * Called when the game should be terminated due to an external event.
     */
    public void terminate() {
        terminate = true;

    }

    /**
     * This method is called when a key is pressed.
     *
     * @param slot - the slot corresponding to the key pressed.
     */
    public void keyPressed(int slot) {
        // check if place is not empty
        if(!table.isReachable() || needToSleep || table.slotToCard[slot]==null){
            return;
        }
        if(myTokens.contains(slot)){
            removeToken(slot);
        }
        else{
            placeToken(slot);
        };  
    }

    /**
     * Award a point to a player and perform other related actions.
     *
     * @post - the player's score is increased by 1.
     * @post - the player's score is updated in the ui.
     */
    public void point() {
        //int ignored = table.countCards(); // this part is just for demonstration in the unit tests
        env.ui.setScore(id, ++score);
        env.ui.setFreeze(id, env.config.pointFreezeMillis);
        needToSleep=true;
    }

    /**
     * Penalize a player and perform other related actions.
     */
    public void penalty() {
        env.ui.setFreeze(id, env.config.penaltyFreezeMillis);  
        needToSleep=true;
    }

    public int score() {
        return score;
    }

    public void removeToken(int slot) {
        myTokens.remove(myTokens.indexOf(slot));
        // remove from table + ui 
        table.removeToken(id, slot);
    }
   
    /**
     * place a token on the table and when the player put the third token ask for the desler to check the set. 
     */
    public void placeToken(int slot) {
        if(myTokens.size() < MAX_TOKENS){
            myTokens.add(slot);
            table.placeToken(id, slot);
            
            if(myTokens.size() == MAX_TOKENS){
                dealer.askToCheckSet(this.id);
                dealer.getThread().interrupt();
            }
        } 
        
    }

    public void clearTokens() {
        myTokens.clear();
    }

    public void setThread(Thread thread){
        playerThread = thread;
    }

    public void startThread(){
        playerThread.start();
    }

    public Thread getThread(){
       return playerThread;
    }

    public void wakeUp(){
        needToSleep = false;
     }
}
