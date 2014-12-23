#pragma once
#include <cstring>
#include "../rapidjson/include/rapidjson/document.h"
#include "../rapidjson/include/rapidjson/filestream.h"
#include "../rapidjson/include/rapidjson/prettywriter.h"

using namespace rapidjson;

class Msg {

    protected:
    int type;
    int id_to;
    int content_len;
    
    virtual void serialize(int sockfd) =0;
    
    public:
    Msg();
    ~Msg();
    void send(int sockfd) { serialize(sockfd); };
    

};

