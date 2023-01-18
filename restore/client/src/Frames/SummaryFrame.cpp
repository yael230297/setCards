#include "../include/stompFrame.h"
#include <iostream>
#include <iostream>
#include <fstream>
using std::cout;

// not a real frame
// SummaryFrame::SummaryFrame(){    
// }

SummaryFrame::SummaryFrame(string gamename,string username, string file): mGamename(gamename),mUsername(username),
    mFile(file)
{
}

SummaryFrame::~SummaryFrame(){}

void SummaryFrame::excute(user* curUser){
    game chosenGame = curUser->getGame(mGamename);
    vector<Event> allEvents = chosenGame.getEventsPerUser(mUsername);
    allEvents = orderByTime(allEvents);
    // write to file - todo - not working fix it
    std::ofstream summaryFile(mFile, std::ios_base::app | std::ios_base::out);
    summaryFile << allEvents[0].get_team_a_name() + " vs " + allEvents[0].get_team_b_name();
    for(Event event : allEvents){
        summaryFile << event.toString(mUsername); // todo : need to check if the parse is good
    }
}

void SummaryFrame::create(map<string,string> headers, string body){
    
}


vector<Event> SummaryFrame::orderByTime(vector<Event> original){
    std::sort(original.begin(), original.end(),
            [](const Event &l, const Event &r){
                return l.get_time() < r.get_time();
            });
    return original;
}