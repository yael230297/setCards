#include "../include/user.h"

user::user(string host, int port, string _login, string _passcode, int _lastSubId, int _lastRecId,
 map<string,game> _games ,map<int,vector<string>> _receiptsHandlers, bool _isConnected, utils _utils):
 login(_login), passcode(_passcode), lastSubscriptionId(_lastSubId), lastReceiptId(_lastRecId), 
 myGames(_games), receiptsHandlers(_receiptsHandlers), isConnected(_isConnected), utilsClass(_utils),handler(host, port)

{
    handler.connect();
}

user::~user()
{
}
int user::creatReceiptId(){
    return ++lastReceiptId;
}

int user::creatSubscribetId(){
    return ++lastSubscriptionId;
}

int user::subscribeToGame(string gameName, int subId){
    if(myGames.find(gameName) == myGames.end()){
        myGames.emplace(gameName,game(gameName, map<string,vector<Event>>(), subId));
        myGames[gameName].subscribeUser(subId);
        return subId;
    }
    return -1;
}

void user::unsubscribeFromGame(int subId){
    string gameName = getGameName(subId);
    if(gameName=="error"){
        // todo : handel error - do not subscrobed
    }
    myGames[gameName].unsubscribeUser();
    myGames.erase(gameName);
}

int user::getSubscriptionId(string gameName){
    if(myGames.find(gameName)==myGames.end()){
        return -1;
    }
    return myGames[gameName].getSubscriptionId();
}

game user::getGame(string name){
    return myGames[name];
}

string user::getName(){
    return login;
}

string user::getGameName(int subId){
    for(auto &it : myGames){
        if(it.second.getSubscriptionId() == subId){
            return it.first;
        }
    }
    return "error";
}

void user::addReceipt(int receipt,stompCommand command, vector<string> neededDetails){
    vector<string> receiptHandler;
    receiptHandler.push_back(utilsClass.commandToString(command));
    for(auto detail : neededDetails){
        receiptHandler.push_back(detail);
    }
    receiptsHandlers.emplace(receipt,receiptHandler);
}

void user::receiveEvent(string destination,Event newEventReceived){
    string username = ""; // todo : extract username from body?
    myGames[destination].addEvent(username,newEventReceived);
}


void user::setIsConnected(){
    isConnected = true;
}

// when the server received disconnect it's unsubscribing all games users.
void user::disconnect(){ 
    // todo : check if needed... 
    // unsubscribe from all games
    // for(auto game : myGames){ //האם צריך גם לעשות מכל משחק?
    //     myGames[game.first].unsubscribeUser();
    // }
    
    myGames.clear(); 
    receiptsHandlers.clear();
    isConnected = false;
}

vector<string> user::getReceiptsHandlers(int receiptId){
    return receiptsHandlers[receiptId];
}

void user::deleteReceipt(int receiptId){
    receiptsHandlers.erase(receiptId);
}

void user::send(string framesMsg, char seperator){
    if(!handler.sendFrameAscii(framesMsg,seperator)){
        std::cout << "error";
    }
    int x=9;
}

bool user::getIsConnected(){
    return isConnected;
}

bool user::read(string& ans){
    char sep = '\0';
    return handler.getFrameAscii(ans,sep);
}