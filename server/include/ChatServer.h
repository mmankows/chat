#pragma once

#include <pthread.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <cstdlib>
#include <openssl/md5.h>
#include <cstring>
#include <iostream>
#include <string>
#include <map>


#include "User.h"
#include "UserList.h"

using namespace std;

class ChatServer {
    
    private:
    int     port;
    int     listenfd;

    int     c_user;
    int     c_chat;

    UserList connectedUsers;


    void  init();
    static string get_auth_token(int sockfd);
    static int    auth(string auth_token);
    static int    auth_file(string login, string hashword);
    friend void*  serve_user(void*);
    
    public:
    ChatServer(int port=258412);
    ChatServer(const ChatServer& );
    ~ChatServer();

    void startListening();

}; 

