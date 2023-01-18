#include "../include/event.h"
#include "../include/json.hpp"
#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <vector>
#include <sstream>
using std::string;
using json = nlohmann::json;

Event::Event(std::string team_a_name, std::string team_b_name, std::string name, int time,
             std::map<std::string, std::string> game_updates, std::map<std::string, std::string> team_a_updates,
             std::map<std::string, std::string> team_b_updates, std::string discription)
    : team_a_name(team_a_name), team_b_name(team_b_name), name(name),
      time(time), game_updates(game_updates), team_a_updates(team_a_updates),
      team_b_updates(team_b_updates), description(discription), utilsClass(utils())
{
}

Event::Event(const std::string &frame_body) : team_a_name(""), team_b_name(""), name(""), time(0), 
game_updates(), team_a_updates(), team_b_updates(), description(""), utilsClass(utils())
{
}

Event::~Event()
{
}

const std::string &Event::get_team_a_name() const
{
    return this->team_a_name;
}

const std::string &Event::get_team_b_name() const
{
    return this->team_b_name;
}

const std::string &Event::get_name() const
{
    return this->name;
}

int Event::get_time() const
{
    return this->time;
}

const std::map<std::string, std::string> &Event::get_game_updates() const
{
    return this->game_updates;
}

const std::map<std::string, std::string> &Event::get_team_a_updates() const
{
    return this->team_a_updates;
}

const std::map<std::string, std::string> &Event::get_team_b_updates() const
{
    return this->team_b_updates;
}

const std::string &Event::get_discription() const
{
    return this->description;
}

names_and_events parseEventsFile(std::string json_path)
{
    std::ifstream f(json_path);
    json data = json::parse(f);

    std::string team_a_name = data["team a"];
    std::string team_b_name = data["team b"];

    // run over all the events and convert them to Event objects
    std::vector<Event> events;
    for (auto &event : data["events"])
    {
        std::string name = event["event name"];
        int time = event["time"];
        std::string description = event["description"];
        std::map<std::string, std::string> game_updates;
        std::map<std::string, std::string> team_a_updates;
        std::map<std::string, std::string> team_b_updates;
        for (auto &update : event["general game updates"].items())
        {
            if (update.value().is_string())
                game_updates[update.key()] = update.value();
            else
                game_updates[update.key()] = update.value().dump();
        }

        for (auto &update : event["team a updates"].items())
        {
            if (update.value().is_string())
                team_a_updates[update.key()] = update.value();
            else
                team_a_updates[update.key()] = update.value().dump();
        }

        for (auto &update : event["team b updates"].items())
        {
            if (update.value().is_string())
                team_b_updates[update.key()] = update.value();
            else
                team_b_updates[update.key()] = update.value().dump();
        }
        
        events.push_back(Event(team_a_name, team_b_name, name, time, game_updates, team_a_updates, team_b_updates, description));
    }
    names_and_events events_and_names{team_a_name, team_b_name, events};

    return events_and_names;
}

/*
* recived from server as a stomp messeage body and need to convert to Event.
*/
void Event::stringToEvent(string event){
    char sepLines ='\n';
    char sepInLines =':';
    vector<string> parts = utilsClass.splitByChar(event,&sepLines);
    int i = 0; 
    string user = utilsClass.splitByChar(parts[i++],&sepInLines)[1];
    team_a_name = utilsClass.splitByChar(parts[i++],&sepInLines)[1];
    team_b_name = utilsClass.splitByChar(parts[i++],&sepInLines)[1];
    name = utilsClass.splitByChar(parts[i++],&sepInLines)[1];
    time = stoi(utilsClass.splitByChar(parts[i++],&sepInLines)[1]);
    // general game updates = todo 
    // team A update = todo
    // team B update = todo
    description = utilsClass.splitByChar(parts[i++],&sepInLines)[1];
}

string Event::toString(string userName){
    string eventStr  = "user: " + userName + '\n';
    eventStr += "team a:" + team_a_name + '\n';
    eventStr += "team b:" + team_b_name + '\n';
    eventStr += "event name:" + name + '\n';
    eventStr += "time:" + std::to_string(time) + '\n';
    eventStr += "general game updates:\n";
    for(auto update : game_updates){
        eventStr += '\t' + update.first + ":" + update.second + '\n'; 
    }
    eventStr += "team a updates:" + '\n'; 
    for(auto update : team_a_updates){
        eventStr += '\t' + update.first + ":" + update.second + '\n'; 
    }
    eventStr += "team b updates:" + '\n'; 
    for(auto update : team_b_updates){
        eventStr += '\t' + update.first + ":" + update.second + '\n'; 
    }
    eventStr += "description:" + '\n' + description + '\n' + '\0';
    std::transform(eventStr.begin(), eventStr.end(), eventStr.begin(), tolower);
    return eventStr;
}