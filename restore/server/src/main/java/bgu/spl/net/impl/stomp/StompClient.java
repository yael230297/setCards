package bgu.spl.net.impl.stomp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.rmi.CORBA.Util;

import bgu.spl.net.impl.Frames.ConnectFrame;
import bgu.spl.net.impl.Frames.FrameUtil;
import bgu.spl.net.impl.Frames.SubscribeFrame;
import bgu.spl.net.impl.gamesUpdate.GamesFeed;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.ConnectionsImpl;

public class StompClient {

    public static void main(String[] args) throws IOException {
        FrameUtil util = new FrameUtil();

    /*     if (args.length == 0) {
    //         args = new String[]{"localhost", "hello"};
    //     }

    //     if (args.length < 2) {
    //         System.out.println("you must supply two arguments: host, message");
    //         System.exit(1);
    //     }

    //     //BufferedReader and BufferedWriter automatically using UTF-8 encoding
    //     try (Socket sock = new Socket(args[0], 7777);
    //             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    //             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {
    //         Map<String,String> connectHeaders = new HashMap<String,String>();
    //         connectHeaders.put("accept-version", "1.2");
    //         connectHeaders.put("host", "localhost");
    //         connectHeaders.put("login", "yael");
    //         connectHeaders.put("passcode", "yael");

    //         StompFrame connectFrame = new ConnectFrame(connectHeaders);
    //         String msg = connectFrame.toString();
    //         out.write(msg);
    //         out.newLine();
    //         out.flush();

    //         System.out.println("awaiting response");
    //         String line="";
    //         String msg2="";
    //         while(line!=null){
    //             line = in.readLine();
    //             if(line!=null){
    //                 msg2+=line;
    //             }
    //         }

    //         Map<String,String> subscribeHeaders = new HashMap<String,String>();
    //         subscribeHeaders.put("destination", "israel_france");
    //         subscribeHeaders.put("id", "45");

    //         StompFrame subscribeFrame = new SubscribeFrame(subscribeHeaders);
    //         String msgSubscribe = subscribeFrame.toString();
    //         out.write(msgSubscribe);
    //         out.newLine();
    //         out.flush();

    //         System.out.println("awaiting response");
    //         line="";
    //         msg2="";
    //         while(line!=null){
    //             line = in.readLine();
    //             if(line!=null){
    //                 msg2+=line;
    //             }
    //         }

    //         //StompFrame receivedFrame = util.buildStompFrame(msg2);

    //         System.out.println("message from server: " + line);
    //     }*/
        
    GamesFeed gf = new GamesFeed();
    StompProtocol protocol = new StompProtocol(gf);
    ConnectionHandler<StompFrame> ch = new BlockingConnectionHandler<>(null, new StompEncoderDecoderImpl(), protocol);
    Connections<StompFrame> connections = new ConnectionsImpl(gf.allGames);
    protocol.start(1,connections);

    Map<String,String> connectHeaders = new HashMap<String,String>();
    connectHeaders.put("accept-version", "1.2");
    connectHeaders.put("host", "localhost");
    connectHeaders.put("login", "yael");
    connectHeaders.put("passcode", "yael");

    StompFrame connectFrame = new ConnectFrame(connectHeaders);
    connectFrame = util.createFrame(StompCommand.CONNECT, connectHeaders, null);
    protocol.process(connectFrame);

    Map<String,String> subscribeHeaders = new HashMap<String,String>();
    subscribeHeaders.put("destination", "israel_france");
    subscribeHeaders.put("id", "45");

    StompFrame subscribeFrame = new SubscribeFrame(subscribeHeaders);
    subscribeFrame = util.createFrame(StompCommand.SUBSCRIBE, subscribeHeaders, null);
    protocol.process(subscribeFrame);

    Map<String,String> unsubscribeHeaders = new HashMap<String,String>();
    unsubscribeHeaders.put("id", "45");

    StompFrame unsubscribeFrame = new SubscribeFrame(unsubscribeHeaders);
    unsubscribeFrame = util.createFrame(StompCommand.UNSUBSCRIBE, unsubscribeHeaders, null);
    protocol.process(unsubscribeFrame);

    protocol.process(subscribeFrame);

    Map<String,String> disconnectHeaders = new HashMap<String,String>();
    disconnectHeaders.put("receipt", "77");

    StompFrame disconnectFrame = new SubscribeFrame(disconnectHeaders);
    disconnectFrame = util.createFrame(StompCommand.DISCONNECTED, disconnectHeaders, null);
    protocol.process(disconnectFrame);
    }
}
