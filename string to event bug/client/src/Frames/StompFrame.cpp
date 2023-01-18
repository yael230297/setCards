#include "../include/StompFrame.h"
#include <string>
#include <vector>
#include <stdio.h>
#include <string.h>


StompFrame::StompFrame(): command(INVALID),headers(map<string, string>()),body(""),
utilsClass(utils()),port_(-1), host_(""){}    

//for messages(string) that we get from the server 
StompFrame::StompFrame(std::string frameStr):command(INVALID),headers(map<string, string>()),
body(""),utilsClass(utils()),port_(-1), host_(""){
    char sep ='\n';
    std::vector<std::string> parts = utilsClass.splitByChar(frameStr,&sep);
    command = utilsClass.stringToCommand(parts[0]);
    int i = 1; 
    while(i < parts.size() && parts[i].length() != 0){
        char sep = ':';
        std::vector<std::string> header = utilsClass.splitByChar(parts[i],&sep);
        headers.emplace(header[0],header[1]);
        i++;
    }
    i++;
    for(std::string::size_type j = i; j < parts.size(); j++){
        body += parts[j];
    }
}

StompFrame::StompFrame(stompCommand _command, std::map<std::string, std::string> _headers, std::string _body):
    command(_command),headers(_headers),body(_body),utilsClass(utils()),port_(-1), host_("")
{
}


StompFrame::~StompFrame()
{
}

std::string StompFrame::toString(){
    int x=4;
    std::string frameString = utilsClass.commandToString(command) +"\n";
    for(auto &pair : headers){
        frameString += pair.first + ": "+ pair.second + "\n";
    }
    if(body!="")
    {
    frameString += body;
    }
    return frameString;
}

stompCommand StompFrame::getCommand(){
    return command;
}

void StompFrame::excute(user* curUser){
    switch (command)
    {
    case CONNECTED:
    {
        ConnectedFrame connectedFrame = ConnectedFrame();
        connectedFrame.create(headers,body);
        connectedFrame.excute(curUser);
        break;
    }
    case RECEIPT:
    {
        RecieptFrame receiptFrame = RecieptFrame();
        receiptFrame.create(headers,body);
        receiptFrame.excute(curUser);
        break;
    }
    case MESSAGE:
    {
        MessageFrame messageFrame = MessageFrame();
        messageFrame.create(headers,body);
        messageFrame.excute(curUser);
        break;
    }
    case ERROR:
    {
        ErrorFrame errorFrame = ErrorFrame();
        errorFrame.create(headers,body);
        errorFrame.excute(curUser);
        break;
    }
    default:
        std::cout << "unknow frame received";
        break;
    }
}

void StompFrame::create(map<string,string> headers, string body){

}