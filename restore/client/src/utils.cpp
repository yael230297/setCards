#include "../include/utils.h"
#include <string.h>

using std::string;
using std::vector;

utils::utils(){

}

utils::~utils(){
    
}

vector<string> utils::splitByChar(std::string stringToSplit, char* seperator){
    char *ptr; // declare a ptr pointer
    char* charsToSplit = &stringToSplit[0];
    ptr = strtok(charsToSplit, seperator); // use strtok() function to separate string using comma (,) delimiter.  
    std::vector<std::string> parts;
    while (ptr != NULL)  
    {  
        parts.push_back(ptr);
        ptr = strtok (NULL, seperator);  
    }
    return parts;
}

string utils::commandToString(stompCommand _command){
switch(_command)
    {
        case CONNECTED: return "CONNECTED";
        case MESSAGE: return "MESSAGE";
        case RECEIPT: return "RECEIPT";
        case ERROR: return "ERROR";
        case CONNECT: return "CONNECT";
        case SEND: return "SEND";
        case SUBSCRIBE: return "SUBSCRIBE";
        case UNSUBSCRIBE: return "UNSUBSCRIBE";
        case DISCONNECT: return "DISCONNECT";
        default:
        return "INVALID";
    }
}

stompCommand utils::stringToCommand(std::string _command){
    if(_command == "CONNECTED"){
        return CONNECTED;
    }else if(_command == "MESSAGE"){
        return MESSAGE;
    }else if(_command == "RECEIPT"){
        return RECEIPT;
    }else if(_command == "ERROR"){
        return ERROR;
    }else if(_command == "CONNECT"){
        return CONNECT;
    }else if(_command == "SEND"){
        return SEND;
    }else if(_command == "SUBSCRIBE"){
        return SUBSCRIBE;
    }else if(_command == "UNSUBSCRIBE"){
        return UNSUBSCRIBE;
    }else if(_command == "DISCONNECT"){
        return DISCONNECT;
    }else{ //(_command == "INVALID")
        return INVALID;
    }
//todo: if the command is'nt valid - return an error???
}
