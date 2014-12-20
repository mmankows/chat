#pragma once

#include <vector>
#include "Msg.h"
#include "User.h"

using namespace std;
class Chat {
    int id_owner;            //id of user who's owner of the chat
    
    vector<User*> members;   //array of user ids involved in this conversations

    public:
    Chat(vector<User*> members);
    ~Chat(void);
    
    /* get size */
    int getSize(void);
    /* add new user to current conversations */
    bool addUser(User* usr_ptr);
    /* delete (block) user from current conversetion */
    bool delUser(User* usr_ptr);
    /* broadcast msg over all members in current conversation */
    void broadcastMsg(Msg& m);
    
};

