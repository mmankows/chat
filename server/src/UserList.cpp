#include "../include/UserList.h"
#include "../include/ProtoMsg.h"
#include  <iostream>

using namespace std;
void UserList::registerObserver(User* u) { 
    cout<<"UserList::registerObserver()\n";
    users[ u->getId() ] = u; 
};

void UserList::unregisterObserver(int user_id) { 
    cout<<"UserList::unregisterObserver()\n";
    users.erase(user_id);
};

void UserList::notifyObservers(Msg& m) {
    cout<<"UserList::notifyObservers()\n";
    //Proto m;
    
    for(map<int,User*>::iterator it=users.begin(); it != users.end(); it++) {
        cout<<"Notifying user\n";
        (*it).second->sendMsg(m);
    }

};

void UserList::addUser(User* u) { 
    cout<<"UserList::addUser()\n";
    ProtoMsg m("add");
    m.addUserInfo(u);
    
    registerObserver(u); 
    notifyObservers(m);
};

void UserList::delUser(int user_id) { 
    cout<<"UserList::delUser()\n";
    ProtoMsg m("del");
    m.addUserInfo(users[user_id]);
    
    unregisterObserver(user_id);
    notifyObservers(m);    
};
