package bgu.spl.net.srv;

public interface Connections<T> {

    /*
     * send a request to create a new connection ?
     */
    boolean send(int connectionId, T msg);

     /*
     * send a request to publish a msg in a channel
     */
    void send(String channel, T msg);

    /*
     * send a request to disconnect
     */
    void disconnect(int connectionId);

    int addNewConnection(ConnectionHandler<T> handler);
}
