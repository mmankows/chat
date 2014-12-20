#include "../include/ChatServer.h"


//constructor
ChatServer::ChatServer(string addr , int port) {
    this->port = port;
    this->addr = addr;

    _init();
}

//close socket, notify clients
ChatServer::~ChatServer() {
    cout<<"Closing server, cleaning resources"<<endl;

}


//establish server conection on given addres and port
void ChatServer::_init() {
    cout<<"Initializing server connection ("<<addr<<":"<<port<<")... "<<endl;
    

}




