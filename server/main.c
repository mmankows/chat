#include <iostream>
#include <cstdio>
#include "include/headers.h"

int main() {
    ChatServer myServer("127.0.1.2", 1234);
    vector<User*> v;
    User u1(1);
    User u2(21);
    User u3(134);
    Chat* c = new Chat(v);
    StdMsg m;


    c->addUser(&u1);
    c->addUser(&u2);
    c->addUser(&u3);
/*    c->addUser(2);
    c->addUser(3);
    c->addUser(4);
    c->delUser(2);
    c->delUser(333332);
    
    c->broadcastMsg(m);
*/  
    printf("Chat sieze: %d\n", c->getSize());
   

    c->broadcastMsg(m);

    return 0;
}
