#include "../include/StompFrame.h"
#include <iostream>
using std::cout;

SubscribeFrame::SubscribeFrame(string gameName,int subscriptionId):mDestination(gameName),mReceipt(-1) ,mSubscriptionId(subscriptionId)
{
    command = SUBSCRIBE;
    headers.emplace("destination",mDestination);
    headers.emplace("id",std::to_string(mSubscriptionId));
}

SubscribeFrame::~SubscribeFrame(){}

void SubscribeFrame::excute(user* curUser){
    mReceipt = curUser->creatReceiptId();
    vector<string> details = vector<string>();
    details.push_back(std::to_string(mSubscriptionId));
    details.push_back(mDestination);
    curUser->addReceipt(mReceipt,stompCommand::SUBSCRIBE,details);
    mSubscriptionId = curUser->creatSubscribetId();

    headers.emplace("receipt",std::to_string(mReceipt));
    string subscribeString = this->toString();
    curUser->send(subscribeString,'\0');
}

void SubscribeFrame::create(map<string, string> headers,string body){
    if(headers.find("destination") == headers.end()){
        cout << "error"; // todo: remove
        return;
    }
    else if(headers.find("id") == headers.end()){
        cout << "error"; // todo: remove
        return;
    }
    else{
        mDestination = headers["destination"];
        mSubscriptionId = stoi(headers["id"]);
    };
}