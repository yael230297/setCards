#include <stdlib.h>
#include <string>
#include <thread>
#include <iostream>
#include "../include/ConnectionHandler.h"
#include "../include/KeyboardParsing.h"

#include "../include/json.hpp"
using json = nlohmann::json;


void foo(user* curUser, keyboardParsing* keyboard){
	while(curUser->getIsConnected()){
		string ans="";
		bool received = curUser->read(ans);
		if(received){
			StompFrame sf = StompFrame(ans);
			sf.excute(curUser);
		}
	}
}


int main(int argc, char *argv[]) {
	
	utils* utilsClass = new utils();
	std::string input = "";
	while(input != "exit"){
		keyboardParsing keyboard = keyboardParsing();
		while(!keyboard.shouldUserTerminate){ // לא סדרנו את התוכנית
			// יוזרים שונים יכולים להתחבר מהקליינט
			int port;
			string host;

			bool connectMsgReceived = false;
			while(!connectMsgReceived){
				keyboard.readFromKeyboard();
				if(keyboard.curStompCommand == CONNECT){
					connectMsgReceived = true;
					char sep = ':';
					auto parts = utilsClass->splitByChar(keyboard.hostAndPort,&sep);
					host = parts[0];
					port = stoi(parts[1]);
				}
			}

			user* curUser = new user(host ,port, keyboard.username, keyboard.password, 0,0, map<string,game>()
				,map<int,vector<string>>(),true, *utilsClass);

			keyboard.excuteConnect(curUser, host, port);
			// test
			std::thread readThread(foo,curUser,&keyboard);

			while(!keyboard.shouldUserTerminate){
				keyboard.readFromKeyboard();
			}

			keyboard.shouldUserTerminate = true;
			readThread.join();
			delete curUser;
		}
		std::cin >> input;
	}

	


	delete utilsClass;
	return 0;
}


// threads creation - yael (ask)
// לעבור על הכל לבדוק אם צריך להוסיף רפרנסים
// בדיקות מקומיות
// בדיקות מול השרת

// memory leak checks - in game for example (dtor ?)


