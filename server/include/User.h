#pragma once
#include <sys/types.h>
#include <cstdio>
#include <unistd.h>
#include <string>
#include "StdMsg.h"
#include "patterns/Observer.h"

class ChatServer;

using namespace std;
class User : public Observer<Msg> {
    
    int    user_fd;
    int    user_id;
    string user_login;
    pthread_mutex_t lock;

    public:

    User() {};
    User(int user_fd);
    User(int user_fd, int user_id, string login);
    ~User();

    void       notify(Msg& m) { sendMsg(m); }
    bool       sendMsg(Msg& m);
    StdMsg     getMsg(void);

    int        getId(void) const { return user_id; }
    void       setId(int id) { this->user_id=id; }
    
    void       setLogin(string login) { this->user_login=login; }
    string     getLogin(void) const { return this->user_login; }
    
    int        getFd(void) const { return user_fd; }


};

