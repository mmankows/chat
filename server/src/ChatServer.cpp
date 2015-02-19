#include "../include/ChatServer.h"
#include <fstream>
#include <map>
#include <unistd.h>
#include <pthread.h>
#include <openssl/md5.h>

//constructor
ChatServer::ChatServer(int port) {
    this->port = port;

    init();
}

//close socket, notify clients
ChatServer::~ChatServer() {
    cout<<"Closing server, cleaning resources"<<endl;
    close(this->listenfd);
}

int ChatServer::auth_file(string login, string password) {
    const char* filename  = "user_db.txt";
    string line;
    bool auth_status = false;
    
    ifstream dbfile;
    dbfile.open(filename);
    if( !dbfile.is_open() ) {
        cerr<<"Error opening db file";
        exit(-1);
    }
    
    string curr_login, curr_pass;
    int id = -1;
    while( std::getline(dbfile,line) ) {
        int sep_pos = line.find(':');
        curr_login  = line.substr(0,sep_pos);
    
        if( !curr_login.compare(login) ) {
            line        = line.substr(sep_pos+1);
            sep_pos     = line.find(':');
            curr_pass   = line.substr(0,sep_pos);
            if( !curr_pass.compare(password) ) {
                id = atoi( line.substr(sep_pos+1).c_str() );
            } 
            break;
        }
    }

    dbfile.close();
    return id;
}


//based on authentication token sent from client verify if login/password match. If yes, return clientID from database
int ChatServer::auth(string auth_token) {
    
    //extract login, password from authentication token
    int delim_pos = auth_token.find(' '); //\036
    string login  = auth_token.substr(0,delim_pos);
    string pass   = auth_token.substr(delim_pos+1);

    //compute md5hashsum of password
    unsigned char result[MD5_DIGEST_LENGTH+1];
    MD5((const unsigned char*) pass.c_str(), (unsigned long)pass.length(), result);
    result[MD5_DIGEST_LENGTH] = '\0';
    string hash = (const char*)result;

    return auth_file(login, pass);

}

string ChatServer::get_auth_token(int sockfd) {
    string token;
    char buf[256];
        
    buf[ read(sockfd, buf, sizeof(buf)) ] = '\0';

    token = buf;
    return token; 
}

//establish server conection on given addres and port
void ChatServer::init() {
    cout<<"Initializing server connection (host:localhost, port:"<<port<<")... "<<endl;
    struct sockaddr_in srv_addr;
    memset((void*) &srv_addr, 0, sizeof(srv_addr));

    //create listening socket
    listenfd = socket(AF_INET, SOCK_STREAM, 0);
    
    if(listenfd < 0) {
        cerr << "Cannot open socket" << endl;
        exit(-1);
    }

    srv_addr.sin_family      = AF_INET;
    srv_addr.sin_addr.s_addr = INADDR_ANY;
    srv_addr.sin_port        = htons(this->port);
    
    if( bind(listenfd, (struct sockaddr*)&srv_addr, sizeof(srv_addr) ) < 0 ) {
        cerr << "Cannot bind" << endl;
        exit(-1);
    }
    cout<<"Server properly initialized"<<endl;

}

void bad_auth(int sockfd) {
    char msg[] = "{\"status\":\"authentication_problem\"}";
    write(sockfd,msg,strlen(msg) );
}

//Main subroutine for serving specific User
void* serve_user(void* ptr) {
    std::pair<ChatServer*,int>* arg_ptr  = static_cast<std::pair<ChatServer*,int>*>(ptr);
    ChatServer* srv_ptr = arg_ptr->first;
    int sockfd          = arg_ptr->second;
    int user_id         = sockfd*2-1; //tmp
    User* user_ptr      = NULL;
    int red             = -1;
    string login;
    string token;

    delete (std::pair<ChatServer*,int>*) ptr;   //delete structure used for passing args

    token    = ChatServer::get_auth_token(sockfd);
    if ( ( user_id  = srv_ptr->auth( token ) ) <= 0 ) {
        bad_auth(sockfd);
        close(sockfd);
        return NULL;
    };
    login    = token.substr(0,token.find(' ') );
    user_ptr = new User(sockfd, user_id, login );
    srv_ptr->connectedUsers.addUser(user_ptr);

    while( 1 ) {
        
        StdMsg m = user_ptr->getMsg();                          //blocking reading from socket, deserialize, pack to class
        m.setSender( user_ptr->getId() );
                                                                //TODO mutex lock inside of User.sendMsg();
        if( !m.getStatus() ) {
            cout<<"Problem while reading msg from user\n";
            //something wrong with reading from user
            break;
        }

        for( vector<int>::iterator it = m.getTargetIds()->begin(); it != m.getTargetIds()->end(); it++ ) {
            srv_ptr->connectedUsers[*it]->sendMsg(m);    //write message to target
        }

    }
    srv_ptr->connectedUsers.delUser(user_id);
    
}

void ChatServer::startListening(void) {
    struct sockaddr_in cli_addr;
    int    size      = sizeof(cli_addr);
    pthread_t tid    = 0;
    int    clientfd  = 0;
    
    listen(this->listenfd, 30);

    while(1) {
        clientfd = accept( listenfd, (struct sockaddr*) &cli_addr, (socklen_t*)&size);
        pthread_create(&tid, NULL, serve_user, (void*) new std::pair<ChatServer*,int>(this,clientfd) );
    }

}




