#include "../include/StompFrame.h"
#include <iostream>
using std::cout;

//server send to client
MessageFrame::MessageFrame(): destination(""), subscription(-1), messageId(-1),body(""){
    command = MESSAGE;
}

void MessageFrame::excute(user* curUser){
    // parseToEvent
    Event newEventReceived = body; // todo : create a parser for events from server
    // save in user
    curUser->receiveEvent(destination,newEventReceived);
}
MessageFrame::~MessageFrame(){}

void MessageFrame::create(map<string, string> headers,string body){
    if(headers.find("destination") == headers.end()){
        cout << "error"; // todo: remove
        return;
    };
    if(headers.find("subscription") == headers.end()){
        cout << "error"; // todo: remove
        return;
    };
    if(headers.find("message-id") == headers.end()){
        cout << "error"; // todo: remove
        return;
    };

    destination = headers["destination"];
    subscription = stoi(headers["subscription"]);
    messageId = stoi(headers["message-id"]);
}