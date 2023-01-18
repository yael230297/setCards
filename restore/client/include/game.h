#include <string>
#include <map>
#include <vector>
#include "event.h"

using std::string;
using std::map;
using std::vector;

class game
{
private:
    string name;
    // username to events
    map<string,vector<Event>> eventsPerUser;
    int subscriptionId;

public:
    game();
    game(string _name, map<string,vector<Event>> events, int subId);
    ~game();

    int getSubscriptionId();
    vector<Event> getEventsPerUser(string username);
    void subscribeUser(int id);
    void unsubscribeUser();
    void addEvent(string userename, Event newEvent);
};