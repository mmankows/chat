#pragma once
#include <cstring>
#include <vector>
#include <iostream>
#include "../rapidjson/include/rapidjson/document.h"
#include "../rapidjson/include/rapidjson/filestream.h"
#include "../rapidjson/include/rapidjson/prettywriter.h"
#include "../rapidjson/include/rapidjson/reader.h"

using namespace rapidjson;
using namespace std;

class Msg {
    
    protected:
    bool status;

    int type;
    vector<int> to_id;
    
    virtual bool serialize(int sockfd)     = 0;
    virtual bool deserialize(int sockfd)   = 0;
    
    public:
    Msg() { type = -1; status=0; }
    Msg(int t) { type = t; status=0; }
    ~Msg() {}
    bool getStatus() { return status; }
    bool send(int sockfd) { status = serialize(sockfd);   return getStatus(); }
    bool get(int sockfd)  { status = deserialize(sockfd); return getStatus(); }
    vector<int>* getTargetIds(void) { return &to_id; }

 

};

