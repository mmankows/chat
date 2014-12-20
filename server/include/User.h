#pragma once
#include "Msg.h"


class User {
    int id_user;

    public:

    User(int id_user);
    ~User();

    int get_id(void) { return id_user; }
    bool sendMsg(Msg& m);

};

