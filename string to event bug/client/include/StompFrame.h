#pragma once
#include "user.h"
#include <map>
#include <string>
using std::string;
using std::vector;
using std::map;

class StompFrame
{
private:
    
protected:
    stompCommand command;
    map<string, string> headers;
    string body;
    utils utilsClass;
    int port_;
    string host_;

public:
    StompFrame();
    StompFrame(string frameStr);
    StompFrame(stompCommand command, map<string, string> headers, string body);
    ~StompFrame();
    
    string commandToString(stompCommand _command);
    stompCommand stringToCommand(string _command);
    string toString();
    stompCommand getCommand();
    void excute(user* curUser);
    void create(map<string, string> headers,string body); 
};

class SummaryFrame: public StompFrame{
    private:
        string mGamename;
        string mUsername;
        string mFile;

    public:
    // SummaryFrame();
    SummaryFrame(string gamename,string username, string file);
    ~SummaryFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
    vector<Event> orderByTime(vector<Event> original);

};

class SendFrame: public StompFrame{
    private:
        string mDestination;
        string mBody;

    public:
    SendFrame();
    SendFrame(string name, string event);
    ~SendFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
};

class MessageFrame: public StompFrame{
   
    private:
    string destination;
    int subscription;
    int messageId;
    string body;

    public: 
    MessageFrame();
    ~MessageFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
};

class RecieptFrame: public StompFrame{
   
    protected:    
    int receiptId;    

    public: 
    RecieptFrame();
    RecieptFrame(stompCommand command, map<string, string> headers, string body);
    ~RecieptFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
};

class ErrorFrame: public StompFrame{
    
    private:
        string message;
        int receiptId;    

    public:
     ErrorFrame();
    ErrorFrame(stompCommand command, map<string, string> headers, string body);
    ~ErrorFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
};

class UnsubscribeFrame: public StompFrame{
    
    private:
        int mSubscriptionId;
        int mReceipt;

    public:
    // UnsubscribeFrame();
    UnsubscribeFrame(int subscriptionId, int receipt);
    ~UnsubscribeFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
};

class SubscribeFrame: public StompFrame{
  
    private:
        string mDestination;
        int mReceipt;
        int mSubscriptionId;

    public:
    SubscribeFrame(string gameName,int subscriptionId);
    ~SubscribeFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
};

class ConnectedFrame: public StompFrame{
    
    private:
        string version;

    public:
    ConnectedFrame();
    ConnectedFrame(string _version);
    ConnectedFrame(stompCommand command, map<string, string> headers, string body);
    ~ConnectedFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
};

class DisconnectFrame: public StompFrame{
    
    private:        
        int mReceipt;

    public:
    DisconnectFrame(int receipt);
    ~DisconnectFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
};


// login {host:port} {username} {password}
class ConnectFrame: public StompFrame{
    
    private:
        std::string mVersion;
        std::string mHost;
        std::string mLogin;
        std::string mPasscode;
        
    public:
    ConnectFrame();
    ConnectFrame(string host, int port, string login, string passcode);
    ~ConnectFrame();
    void excute(user* curUser);
    void create(map<string, string> headers,string body);
};