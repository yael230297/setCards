#include "game.h"
#include "connectionHandler.h"
#include <string>
#include <map>
#include <vector>

using std::string;
using std::map;
using std::vector;

class user
{
private:
    /* data */
    string login;
    string passcode;
    int lastSubscriptionId;
    int lastReceiptId;
    // game name, game
    map<string,game> myGames;
    // int-receiptId ,map <gameName,stompCommand>
    map<int,vector<string>> receiptsHandlers;

protected:
    bool isConnected;
    utils utilsClass;
    ConnectionHandler handler;

public:
    user();
    user(string host, int port, string _login, string _passcode, int _lastSubId, int _lastRecId,
        map<string,game> _games ,map<int,vector<string>> _receiptsHandlers, bool _isConnected, utils _utils);
    ~user();
    int creatSubscribetId();
    int creatReceiptId();
    game getGame(string gameName);
    string getName();
    string getGameName(int subId);
    int subscribeToGame(string gameName, int subId);
    void unsubscribeFromGame(int subId);
    int getSubscriptionId(string gameName);
    void addReceipt(int receipt,stompCommand command, vector<string> neededDetails);
    void receiveEvent(string destination,Event newEventReceived);
    void setIsConnected(); 
    void disconnect();
    vector<string> getReceiptsHandlers(int receiptId);
    void deleteReceipt(int receiptId);
    void send(string framesMsg, char seperator);
    bool getIsConnected();
    bool read(string& ans);
};
