#pragma once
#include <map>
#include "User.h"
#include "ProtoMsg.h"
#include "patterns/Observer.h"


//Klasa UserList implementuje wzorzec obserwator

using namespace std;

class UserList : public Subject<User,Msg>  {
    std::map<int,User*> users;
    
    public:
    void registerObserver(User* u);
    void unregisterObserver(int user_id);
    void notifyObservers(Msg&);
    
    void addUser(User* u); 
    void delUser(int user_id);



};
