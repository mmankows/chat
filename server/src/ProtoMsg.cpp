#include "../include/ProtoMsg.h"
#include <cstdio>
#include <iostream>

ProtoMsg::ProtoMsg(string action) : Msg(){
    this->type   = 2;
    this->action = action;
}
ProtoMsg::~ProtoMsg() {}

void ProtoMsg::addUserInfo(const User* u) { 
    id_nick_pairs.push_back( pair<int,string>( u->getId(), u->getLogin()) );
}

bool ProtoMsg::serialize(int sockfd) {

    FILE* fp = fdopen(sockfd,"w");
    FileStream s(fp);

    PrettyWriter<FileStream> writer(s);

    writer.StartObject();
    
    writer.String("type");
    writer.Int(type);
        
    writer.String("action");
    writer.String(action.c_str());

    writer.String("nick_uid");
    writer.StartArray();

    for (vector<pair<int,string> >::iterator it=id_nick_pairs.begin(); it != id_nick_pairs.end(); ++it) {
        writer.StartObject();
        writer.String("nick");
        writer.String((*it).second.c_str() );
        writer.String("uid");
        writer.Int((*it).first);    
        writer.EndObject();
    }
    writer.EndArray();


    writer.EndObject();

    printf("Finished writing.\n");

}
