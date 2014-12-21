#include "../include/ChatServer.h"


//constructor
ChatServer::ChatServer(int port) {
    this->port   = port;
    this->c_user = 0;

    _init();
}

//close socket, notify clients
ChatServer::~ChatServer() {
    cout<<"Closing server, cleaning resources"<<endl;
    close(this->listenfd);
}

int ChatServer::_auth_file(string login, string password) {
    string filename = "user_db.txt";
    
    return ++c_user;
}


//based on authentication token sent from client verify if login/password match. If yes, return clientID from database
int ChatServer::_auth(string auth_token) {
    //extract login, password from authentication token
    int delim_pos = auth_token.find('\036');
    string login  = auth_token.substr(0,delim_pos);
    string pass   = auth_token.substr(delim_pos+1);

    //comput md5hashsum of password
    unsigned char result[MD5_DIGEST_LENGTH+1];
    MD5((const unsigned char*) pass.c_str(), (unsigned long)pass.length(), result);
    result[MD5_DIGEST_LENGTH] = '\0';
    string hash = (const char*)result;
    
    cout<<"LOGIN:"<<login<<"|"<<endl;
    cout<<"PASSW:"<<pass<<"|"<<endl;
    cout<<"DELIM:"<<'\036'<<"|"<<endl;
    cout<<"LEN:"<<MD5_DIGEST_LENGTH<<"|"<<endl;
    cout<<"MD5:"<<hash<<"|"<<endl;

    return _auth_file(login, hash);

}

//establish server conection on given addres and port
void ChatServer::_init() {
    cout<<"Initializing server connection (port:"<<port<<")... "<<endl;
    struct sockaddr_in srv_addr;
    memset((void*) &srv_addr, 0, sizeof(srv_addr));

    //create listening socket
    listenfd = socket(AF_INET, SOCK_STREAM, 0);
    
    if(listenfd < 0) {
        cerr << "Cannot open socket" << endl;
        exit(-1);
        //
    }

    srv_addr.sin_family      = AF_INET;
    srv_addr.sin_addr.s_addr = INADDR_ANY;
    srv_addr.sin_port        = htons(this->port);
    
    if( bind(listenfd, (struct sockaddr*)&srv_addr, sizeof(srv_addr) ) < 0 ) {
        cerr << "Cannot bind" << endl;
        exit(-1);
        //
    }
    cout<<"Server properly initialized"<<endl;

}

void* serve_user(void* ptr) {
    User* user_ptr = static_cast<User*>(ptr);
    int sockfd     = user_ptr->getFd();
    int red        = -1;
    
    while( 1 ) {
    char buf[256];
        red = read(sockfd, buf, sizeof(buf));
        cout<<"RED:"<<red<<endl;
        if (red <=0) break;
        buf[red] = '\0';
        write(sockfd,"you wrote:",10);
        write(sockfd,buf,strlen(buf));
    }
    
}

void ChatServer::startListening(void) {
    cout<<"Listening..."<<endl;
    struct sockaddr_in cli_addr;
    int    size      = sizeof(cli_addr);
    int    clientfd  = 0;
    User* tmp_user_ptr;

    listen(this->listenfd, 30);

    while(1) {
        cout<<"Listening for connection!\n";
        clientfd = accept( listenfd, (struct sockaddr*) &cli_addr, (socklen_t*)&size);
        cout<<"Incomming connection!\n";
        tmp_user_ptr = new User(clientfd);
        pthread_create(tmp_user_ptr->getThread(), NULL, serve_user, (void*)tmp_user_ptr);
        cout<<"Created thread for new connection!\n";
    }

}




