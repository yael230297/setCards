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
// TODO : private
    public long freezeTime;

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
    private final Object monitorPressedQueue = new Object();
    private final Object playerMonitor = new Object();


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
        this.freezeTime = 0;

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
            synchronized(monitorPressedQueue){
                if(keyPressedQueue.isEmpty()){
                    try{monitorPressedQueue.wait();}catch(InterruptedException ex){
                        if(terminate){break;}
                        handelPress();
                    }
                }
                else{
                    handelPress();
                }
            }
        }
        
        if (!human) try { aiThread.join(); } catch (InterruptedException ignored){}
        env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " terminated.");
    }

    public void handelPress(){
        Integer slot = keyPressedQueue.poll();                    
        monitorPressedQueue.notify();
        if(slot != null){                        
            slotChosen(slot);
        }
    }

    /**
     * Creates an additional thread for an AI (computer) player. The main loop of this thread repeatedly generates
     * key presses. If the queue of key presses is full, the thread waits until it is not full.
     */
    private void createArtificialIntelligence() {
        // note: this is a very very smart AI (!)
        aiThread = new Thread(() -> {
            env.logger.log(Level.INFO, "Thread " + Thread.currentThread().getName() + " starting.");
                while (!terminate) {
                    // create key press - random number from 0-11.
                    int slot = (int)(Math.random()*(env.config.tableSize-1));
                    // push to queue
                    synchronized(monitorPressedQueue){
                        if(keyPressedQueue.size() < 3){
                            keyPressedQueue.add(slot);
                            try{Thread.sleep(100);}catch(InterruptedException ex){
                                if(terminate){break;}
                            }
                            monitorPressedQueue.notify();
                        }
                    
                        if(keyPressedQueue.size()==3){
                            try{monitorPressedQueue.wait();}catch(InterruptedException ex){
                                if(terminate){break;}
                            }
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
        if(!human){
            aiThread.interrupt();
        }
        playerThread.interrupt();
    }

    /**
     * This method is called when a key is pressed.
     *
     * @param slot - the slot corresponding to the key pressed.
     */
    public void keyPressed(int slot) {
        if(human){
            this.keyPressedQueue.add(slot);
            playerThread.interrupt();
        }
    }

    /**
     * 
     *
     */
    public void slotChosen(int slot) {
        // check if place is not empty
        if(!table.isReachable() || needToSleep || 
           table.slotToCard[slot]==null){
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
        //needToSleep=true;
        freezeTime = env.config.pointFreezeMillis;
        env.ui.setScore(id, ++score);
        env.ui.setFreeze(id, freezeTime);        
    }

    /**
     * Penalize a player and perform other related actions.
     */
    public void penalty() { // marina entered here first
        //needToSleep=true;
        freezeTime = env.config.penaltyFreezeMillis;
        env.ui.setFreeze(id, freezeTime);  
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
    public void placeToken(int slot) { // meni first
        synchronized(playerMonitor){
            if(myTokens.size() < MAX_TOKENS){
                myTokens.add(slot);
                table.placeToken(id, slot);
            
                if(myTokens.size() == MAX_TOKENS){
                    needToSleep = true;
                    dealer.askToCheckSet(this.id);
                    dealer.getThread().interrupt();
                    try{playerMonitor.wait();}catch(InterruptedException ex){
                        if(terminate){return;}
                    };
                    keyPressedQueue.clear();
                    }
                    // player sleep until dealer retrun answer
                }
            }
        //} 
    }

    // public void updateFreezeTime() {
    //     while(freezeTime>0){
    //         try{Thread.sleep(freezeTime);}catch(InterruptedException ex){}
    //         //freezeTime-=1000;
    //         //env.ui.setFreeze(id, freezeTime);
    //     }
    //     keyPressedQueue.clear();
    // }

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

    // TODO change
    public void continuePlay(){            
            //keyPressedQueue.clear();
            synchronized(playerMonitor){
                playerMonitor.notify();
            }
            needToSleep = false;

            // synchronized(monitor){
            //     monitor.notify();
            // }
            
    }


    public void receiveSetTestResault(){
        // synchronized(playerMonitor){
        //     playerMonitor.notify();
        // }       
        playerThread.interrupt();
    }

    // void reduceFreezeTime(){
    //     freezeTime--;
    //     env.ui.setFreeze(id,freezeTime);  
    //     if(freezeTime==0){
    //         wakeUp();
    //     }
    // }
}
