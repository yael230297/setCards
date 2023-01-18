package bgu.spl.net.impl.stomp;

import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.srv.Server;


public class StompServer {
    public static void main(String[] args) {
        GamesFeed gamesFeed = new GamesFeed();

        Server.threadPerClient(
                7777, //port
                () -> new StompProtocol(gamesFeed), //protocol factory
                StompEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();

        // Server.reactor(
        //         Runtime.getRuntime().availableProcessors(),
        //         7777, //port
        //         () -> new StompProtocol<>(gamesFeed), //protocol factory
        //         StompEncoderDecoder::new //message encoder decoder factory
        // ).serve();
    }
}

/*EXAMPLE FOR STOMP FRAME */
// StompFrame x = new StompFrame();
// x.stompCommand = StompCommand.SEND;
// x.stompsHeaders = new HashMap <String,String>();
// x.stompsHeaders.put("destination", "/spain_japan");
// Map <String,String> general = new HashMap<String, String>();
// general.put("active", "true");
// general.put("before half time", "true");
// Map <String,String> teamA = new HashMap<String, String>();
// teamA.put("goals", "4");
// Map <String,String> teamB = new HashMap<String, String>();
// teamB.put("goals", "2");
// Event meni = new Event("meni", "kickoff", "And we're off!", 0,"spain", "germany" ,general, teamA, teamB);
// x.body = meni;

// String s = x.toString();
// System.out.println(s);
// StompFrame i = new StompFrame(s);
// int a = 4;