#include <iostream>
#include <cstdio>
#include <exception>
#include "include/headers.h"


int main(int argc, char* argv[]) {
    
    ChatServer myServer;
    //User u(0);


    //StdMsg m = u.getMsg();
    
   // StdMsg m(vector<int>(10), string("To jest tresc wiadomosci kuhhhwo"));
   //m.send(1);
   
   
    myServer.startListening();

    return 0;
}
