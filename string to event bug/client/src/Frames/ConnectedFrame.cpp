#include "../include/StompFrame.h"
#include <iostream>
using std::cout;

//we get it from the server
ConnectedFrame::ConnectedFrame(): version(""){
    command = CONNECTED;
}

ConnectedFrame::ConnectedFrame(string _version): version(_version){
    command = CONNECTED;
}

ConnectedFrame::~ConnectedFrame(){}

void ConnectedFrame::excute(user* curUser){
    curUser->setIsConnected();
}

void ConnectedFrame::create(map<string,string> headers, string body){
    if (headers.find("version") == headers.end()) {
        cout << "error - version not found"; // todo: remove
    }
    version = headers["version"];
}
