#include "../include/User.h"




User::User(int user_fd) {
    this->user_fd = user_fd;
    this->user_id = -1;

}

User::~User() {
    close(this->user_fd);
}

bool User::sendMsg(Msg& m) {
    printf("[%d]I've got msg!: %s\n", user_id, m.serialize());
}
