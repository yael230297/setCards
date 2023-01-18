#include <stdlib.h>
#include <string>
#include <vector>
#include <iostream>
#include "StompFrame.h"

using std::string;
using std::vector;

class keyboardParsing
{
private:
   utils utilsClass;
   string curKeyboardInput;
public:
    bool shouldUserTerminate;
    stompCommand curStompCommand;
    user* curUser;
    string hostAndPort;
    string username;
    string password;

    keyboardParsing();
    keyboardParsing(utils _utils, string _curKeyboardInput, bool _shouldUserTerminate,stompCommand _curStompCommand,user* _curUser, string _hostAndPort,string _username,string _password);
    ~keyboardParsing();

    void readFromKeyboard();
    void excuteConnect(user* userCreated, string host, int port);
    void parseLoginFields(string command);
    void parseJoinFields(string command);
    void parseExitFields(string command);
    void parseReportFields(string command);
    void parseSummaryFields(string command);
    void parseLogoutFields(string command);
};

