#include "../include/StompFrame.h"
#include <iostream>
using std::cout;

//we send to the server
ConnectFrame::ConnectFrame(): mVersion("1.2"), mHost(""), mLogin(""), mPasscode(""){
    command = CONNECT;
}
ConnectFrame::ConnectFrame(string host, int port, string login, string passcode): mVersion("1.2"), mHost(host), mLogin(login),mPasscode(passcode){
    command = CONNECT;
    host_ = host;
    port_ = port;
    headers.emplace("login", login);
    headers.emplace("passcode", passcode);
    headers.emplace("host", host);
    headers.emplace("accept-version", "1.2");
}

ConnectFrame::~ConnectFrame(){}


void ConnectFrame::excute(user* curUser){
    // convert the frame to string  
    string sendFrame = toString();
    //sends the string to the server 
    curUser->send(sendFrame, '\0');
}

void ConnectFrame::create(map<string,string> headers, string body ){
    if (headers.find("host") == headers.end()) {
        cout << "error - host not found"; // todo: remove
    } else if(headers.find("login") == headers.end()){
        cout << "error - login not found"; // todo: remove
    } else if(headers.find("passcode") == headers.end()){
        cout << "error - passcode not found"; // todo: remove)
    } else {
        mHost = headers["host"];
        mLogin = headers["login"];
        mPasscode = headers["passcode"];
    }
}