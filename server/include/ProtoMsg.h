#pragma once
#include <iostream>
#include <string>
#include <utility>
#include <vector>

#include "Msg.h"
#include "User.h"

using namespace std;

class ProtoMsg : public Msg {
    string                     action;            // what happened
    vector<pair<int,string> >  id_nick_pairs;     // id-value pairs

    bool serialize(int sockfd); 
    bool deserialize(int sockfd) {}
    
    public:
    ProtoMsg() {};
    ProtoMsg(string action);
    ~ProtoMsg();
    
    void addUserInfo(const User* u);
    

};

