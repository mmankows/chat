#pragma once

#include <pthread.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <cstdlib>
#include <openssl/md5.h>
#include <cstring>
#include <iostream>
#include <map>

#include "User.h"
#include "Chat.h"


using namespace std;

class ChatServer {
    
    private:
    int     port;

    int     listenfd;

    int     c_user;
    int     c_chat;
    map<int,User> connectedUsers;
    map<int,Chat> activeChats;

    void  _init();
    int   _auth(string auth_token);
    int   _auth_file(string login, string hashword);
    friend void* serve_user(void*);
    
    public:
    ChatServer(int port=258412);
    ChatServer(const ChatServer& );
    ~ChatServer();

    void startListening();

}; 

