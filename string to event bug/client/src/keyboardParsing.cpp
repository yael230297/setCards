#include "../include/keyboardParsing.h"
using std::string;
using std::vector;

keyboardParsing:: keyboardParsing():utilsClass(utils()), curKeyboardInput(""),
    shouldUserTerminate(false), curStompCommand(INVALID), curUser(NULL), hostAndPort(""),
    username(""), password(""){}

keyboardParsing::~keyboardParsing(){}

void keyboardParsing::readFromKeyboard(){
	std::string keyboardInput = "";
    std::getline (std::cin, keyboardInput);

	if(keyboardInput.find("login")!= string::npos){
		parseLoginFields(keyboardInput);
        curKeyboardInput = keyboardInput;
        curStompCommand = CONNECT;
        return;
	}

	if(keyboardInput.find("join")!= string::npos){
		parseJoinFields(keyboardInput);
        return;
	}

	if(keyboardInput.find("exit")!= string::npos){
		parseExitFields(keyboardInput);
        return;
	}

	if(keyboardInput.find("report")!= string::npos){
		parseReportFields(keyboardInput);
        return;
	}

	if(keyboardInput.find("summary")!= string::npos){
		parseSummaryFields(keyboardInput);
        return;
	}

	if(keyboardInput.find("logout")!= string::npos){
		parseLogoutFields(keyboardInput);
        shouldUserTerminate = true;
        return;
	}
}

// login {host:port} {username} {password}
void keyboardParsing::parseLoginFields(std::string command){
    char space = ' ';
    vector<string> parts = utilsClass.splitByChar(command, &space);
    int i=0;
    string stompCommand = parts[i++];
    hostAndPort = parts[i++];
    username = parts[i++];
    password = parts[i++];
}

// join {gamename} 
void keyboardParsing::parseJoinFields(std::string command){
    char space = ' ';
    vector<string> parts = utilsClass.splitByChar(command, &space);
    int i=0;
    string stompCommand = parts[i++];
    string gameName = parts[i++];

    int subscriptionId = curUser->creatSubscribetId();
    SubscribeFrame* subscribeFrame = new SubscribeFrame(gameName,subscriptionId);
    subscribeFrame->excute(curUser);
    delete subscribeFrame;
}

// exit {gamename}
void keyboardParsing::parseExitFields(std::string command){
    char space = ' ';
    vector<string> parts = utilsClass.splitByChar(command, &space);
    int i=0;
    string stompCommand = parts[i++];
    string gameName = parts[i++];
    
    int subscriptionId = curUser->getSubscriptionId(gameName);
    int receiptId = curUser->creatReceiptId();
    UnsubscribeFrame* unsubscribeFrame = new UnsubscribeFrame(subscriptionId, receiptId);
    unsubscribeFrame->excute(curUser);
    delete unsubscribeFrame;
}

// report {file}
void keyboardParsing::parseReportFields(std::string command){
    char space = ' ';
    vector<string> parts = utilsClass.splitByChar(command,  &space);
    int i=0;
    string stompCommand = parts[i++];
    string file = parts[i];
    //getAllEvents
    names_and_events allGames = parseEventsFile(file);
    string gameName = allGames.team_a_name + "_" + allGames.team_b_name;
    vector<Event> allEvents = allGames.events;
    
    for(Event curEvent : allEvents){  
        string eventStr = curEvent.toString(curUser->getName()); 
        SendFrame* sendFrame = new SendFrame(gameName, eventStr);
        sendFrame->excute(curUser);
        delete sendFrame;
    }
}

// summary {gamename} {user} {file}
void keyboardParsing::parseSummaryFields(std::string command){
    char space = ' ';
    vector<string> parts = utilsClass.splitByChar(command,  &space);
    int i=0;
    string stompCommand = parts[i++];
    string gameName = parts[i++];
    string username = parts[i++];
    string file = parts[i++];
    SummaryFrame* summaryFrame = new SummaryFrame(gameName, username,file); 
    summaryFrame->excute(curUser);
    delete summaryFrame;
}

// logout
void keyboardParsing::parseLogoutFields(std::string command){
    int receiptId = curUser-> creatReceiptId();
    DisconnectFrame* disconnectFrame = new DisconnectFrame(receiptId);
    disconnectFrame->excute(curUser);
    delete disconnectFrame;
}

void keyboardParsing::excuteConnect(user* userCreated, string host, int port){
    curUser = userCreated;  
    ConnectFrame* connectFrame = new ConnectFrame(host, port,username,password);
    connectFrame->excute(curUser);
    delete connectFrame;
}
