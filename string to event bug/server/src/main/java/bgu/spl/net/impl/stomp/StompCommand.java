package bgu.spl.net.impl.stomp;

public enum StompCommand {
    // server's
    CONNECTED,
    MESSAGE,
    RECEIPT,
    ERROR,
    // client's
    CONNECT,
    SEND,
    SUBSCRIBE,
    UNSUBSCRIBE,
    DISCONNECTED,
    // invalid
    INVALID
}