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
    delete users[user_id];   // delete object
    users.erase(user_id);   // remove pointer
};

void UserList::notifyObservers(Msg& m) {
    cout<<"UserList::notifyObservers()\n";
    
    for(map<int,User*>::iterator it=users.begin(); it != users.end(); it++) {
        cout<<"Notifying user\n";
        (*it).second->sendMsg(m);
    }

};

void UserList::addUser(User* u) { 
    cout<<"UserList::addUser()\n";

    ProtoMsg m_others("add");
    m_others.addUserInfo(u);
    
    notifyObservers(m_others);
    registerObserver(u); 
     
    //send list of all connected users to the new user
    ProtoMsg m_list("add");
    for(map<int,User*>::iterator it=users.begin(); it != users.end(); it++) {
        m_list.addUserInfo( (*it).second );
    }
    u->sendMsg(m_list); 
};

void UserList::delUser(int user_id) { 
    cout<<"UserList::delUser()\n";
    ProtoMsg m("del");
    m.addUserInfo(users[user_id]);
    
    notifyObservers(m);    
    unregisterObserver(user_id);
};
