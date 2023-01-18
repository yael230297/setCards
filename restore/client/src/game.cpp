#include "../include/game.h"

game::game():name(""),eventsPerUser(map<string,vector<Event>>()), subscriptionId(-1)
{}

game::game(string _name, map<string,vector<Event>> events, int subId): name(_name),eventsPerUser(events), subscriptionId(subId)
{
}

game::~game()
{
}

vector<Event> game::getEventsPerUser(string username){
    return eventsPerUser[username];
}


int game::getSubscriptionId(){
    return subscriptionId;
}

void game::subscribeUser(int id){
    subscriptionId = id;
}

void game::unsubscribeUser(){
    subscriptionId = -1;
    eventsPerUser.clear();
}

void game::addEvent(string userename, Event newEvent){
    if(eventsPerUser.find(userename) != eventsPerUser.end()){
        auto x = vector<Event>();
        eventsPerUser.emplace(userename,x);
    }
    eventsPerUser[userename].push_back(newEvent);

}