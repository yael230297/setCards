#pragma once
#include <stdlib.h>
#include <string>
#include <vector>
#include <iostream>
#include "StompCommand.h"

using std::string;
using std::vector;



class utils
{
private:
public:
    utils();
    ~utils();
    vector<string> splitByChar(std::string stringToSplit, char* seperator);
    string commandToString(stompCommand _command);
    stompCommand stringToCommand(std::string _command);
};