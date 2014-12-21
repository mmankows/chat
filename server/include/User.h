#pragma once
#include <sys/types.h>
#include <cstdio>
#include <unistd.h>
#include "Msg.h"


class User {
    int user_id;
    int user_fd;
    pthread_t user_thread;

    public:

    User(int user_fd);
    ~User();

    pthread_t* getThread(void) { return &user_thread; }
    int        getId(void) { return user_id; }
    int        getFd(void) { return user_fd; }
    bool       sendMsg(Msg& m);

};

