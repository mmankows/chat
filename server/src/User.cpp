#include <cstdio>
#include "../include/User.h"




User::User(int id_user) {
    this->id_user = id_user;
}

User::~User() {

}

bool User::sendMsg(Msg& m) {
    printf("[%d]I've got msg!: %s\n", id_user, m.serialize());
}
