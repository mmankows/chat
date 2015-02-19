#include "../include/User.h"
#include "../include/ChatServer.h"
#include "../include/StdMsg.h"

User::User(int user_fd) {
    this->user_fd = user_fd;
    this->user_id = -1;
}

User::User(int user_fd, int user_id, string login) {
    this->user_fd = user_fd;
    this->user_id = user_id;
    this->user_login = login;
}

User::~User() {
    close(this->user_fd);
}

bool User::sendMsg(Msg& m) {
    m.send( getFd() );
}

StdMsg User::getMsg(void) {
    StdMsg m;
    m.get( getFd() );
    return m;
}
