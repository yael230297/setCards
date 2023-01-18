#include "../include/StompFrame.h"

RecieptFrame::RecieptFrame():receiptId(-1){
    command = RECEIPT;
}
RecieptFrame::~RecieptFrame(){}

void RecieptFrame::excute(user* curUser){
    auto commandAndGame = curUser->getReceiptsHandlers(receiptId);
    string commandStr = commandAndGame[0];
    
    switch (utilsClass.stringToCommand(commandStr))
    {
    case SUBSCRIBE:
        curUser->subscribeToGame(commandAndGame[2],stoi(commandAndGame[1]));
        break;
    case UNSUBSCRIBE:
        curUser->unsubscribeFromGame(stoi(commandAndGame[1]));
        break;
    case DISCONNECT:
        curUser->disconnect();
        break;
    default:
        break;
    }
    curUser->deleteReceipt(receiptId); //to do: implement this

}

void RecieptFrame::create(map<string, string> headers,string body){
    if(headers.find("receipt-id")== headers.end()){
        // todo : error 
        std::cout << "no receipt-id frame";
    }
    receiptId = stoi(headers["receipt-id"]);
}