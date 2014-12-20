#include "../include/Chat.h"

Chat::Chat(vector<User*> members) {
    this->members  = members;
    this->id_owner = members.size() ? members[0]->get_id() : -1;
}

Chat::~Chat() {
    members.clear();
}

bool Chat::addUser(User* usr_ptr) {
    this->members.push_back( usr_ptr );
}

bool Chat::delUser(User* usr_ptr) {
    vector<User*>::iterator it = members.begin();
    do {
        if ( (*it)->get_id() == usr_ptr->get_id() ) {
            members.erase(it);
            return true;
        }
    } while( it++ != members.end() );
    
    return false;
}

int Chat::getSize(void) {
    return members.size();
}

void Chat::broadcastMsg(Msg& m) {
    vector<User*>::iterator it = members.begin();
    bool status = true;

    while( it != members.end() ) {
        status = (*it)->sendMsg(m);
        it++;
    }

}
