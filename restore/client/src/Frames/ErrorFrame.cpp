#include "../include/StompFrame.h"
#include <iostream>
using std::cout;

ErrorFrame::ErrorFrame(): message(""), receiptId(-1){
    command = ERROR;
}

void ErrorFrame::excute(user* curUser){
// print msg to screen base on msg header
    cout << message;
// disconnect
    curUser->disconnect();
}

ErrorFrame::~ErrorFrame(){}

void ErrorFrame::create(map<string,string> headers, string body){
    if (headers.find("message") == headers.end()) {
        cout << "error - message not found"; // todo: remove
    } else if(headers.find("receipt id") == headers.end()){
        cout << "error - receipt id not found"; //todo: remove
    } else{
        receiptId = stoi(headers["receipt id"]);
        message = headers["message"];
    }
}