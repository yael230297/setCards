#include "../include/StompFrame.h"
#include <iostream>
using std::string;
using std::map;
using std::cout;
using namespace std;


DisconnectFrame::DisconnectFrame(int receipt): mReceipt(receipt){
    command = DISCONNECT;
}

DisconnectFrame::~DisconnectFrame(){}

void DisconnectFrame::excute(user* curUser){
    //insert the request to the receipt map 
    mReceipt = curUser->creatReceiptId();
    curUser->addReceipt(mReceipt,stompCommand::DISCONNECT, vector<string>());
    headers.emplace("receipt",to_string(mReceipt));
    string disconnectStr = toString();
    //sends the string frame to the server
    curUser->send(disconnectStr,'\0');
}

void DisconnectFrame::create(map<string,string> headers, string body){
    if (headers.find("receipt") == headers.end()) {
        cout << "error - receipt id not found"; // todo: remove
    }
    //stoi = a function that convert string to int
    mReceipt = stoi(headers["receipt"]) ;
}