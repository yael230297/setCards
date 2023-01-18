#include "../include/StompFrame.h"


// sending to the server - events
SendFrame::SendFrame():mDestination(""), mBody(""){
    command = SEND;

}

SendFrame::SendFrame(string name, string event): mDestination(name), mBody(event){
    command = SEND;
    headers.emplace("destination",mDestination);
}
SendFrame::~SendFrame(){}

void SendFrame::excute(user* curUser){
    int x=3;
    string sendFrameString = this->toString();
    curUser->send(sendFrameString,'\0');
}

void SendFrame::create(map<string,string> header, string body){

}