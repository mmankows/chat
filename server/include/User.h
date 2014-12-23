#pragma once
#include <sys/types.h>
#include <cstdio>
#include <unistd.h>
#include <string>
#include "Msg.h"
#include "patterns/Observer.h"

class ChatServer;

using namespace std;
class User : public Observer<Msg> {
    
    int user_fd;
    int    user_id;
    string user_login;
    

    pthread_t user_thread;

    public:

    User() {};
    User(int user_fd);
    User(int user_fd, int user_id, string login);
    ~User();

    void       notify(Msg& m) { sendMsg(m); }
    pthread_t* getThread(void) { return &user_thread; }
    int        getId(void) const { return user_id; }
    void       setId(int id) { this->user_id=id; }
    void       setLogin(string login) { this->user_login=login; }
    string     getLogin(void) const { return this->user_login; }
    int        getFd(void) const { return user_fd; }
    bool       sendMsg(Msg& m);


};

