#include "../include/StompFrame.h"
#include <iostream>
using std::cout;

// UnsubscribeFrame::UnsubscribeFrame(){
//     command = UNSUBSCRIBE;

// }

UnsubscribeFrame::UnsubscribeFrame(int subscriptionId, int receipt):mSubscriptionId(subscriptionId), mReceipt(receipt)
{
    command = UNSUBSCRIBE;
    headers.emplace("id",std::to_string(mSubscriptionId));
}

UnsubscribeFrame::~UnsubscribeFrame(){}

void UnsubscribeFrame::excute(user* curUser){
    vector<string> details = {std::to_string(mSubscriptionId)};
    curUser->addReceipt(mReceipt,stompCommand::UNSUBSCRIBE,details);
    
    headers.emplace("receipt",std::to_string(mReceipt));
    string unsubscribeString = this->toString();
    curUser->send(unsubscribeString,'\0');
}

void UnsubscribeFrame::create(map<string, string> headers,string body){
    if(headers.find("id") == headers.end()){
        cout << "error"; // todo: remove
    }else{
        mSubscriptionId = stoi(headers["id"]);
    }
}
