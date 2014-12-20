#pragma once

#include <iostream>
#include <map>

#include "User.h"
#include "Chat.h"

using namespace std;

class ChatServer {
    
    private:
        int     port;
        string  addr;
    
        int     c_user;
        int     c_chat;
        map<int,User> connectedUsers;
        map<int,Chat> activeChats;
    
        void _init();
    
    public:
        ChatServer(string addr="127.0.0.1", int port=258412);
        ChatServer(const ChatServer& );
        ~ChatServer();
   
        int startListening();

}; 

