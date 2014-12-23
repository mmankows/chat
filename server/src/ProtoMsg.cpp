#include "../include/ProtoMsg.h"
#include <cstdio>
#include <iostream>

ProtoMsg::ProtoMsg(string action) : Msg(){
    this->action = action;
}
ProtoMsg::~ProtoMsg() {}

void ProtoMsg::addUserInfo(const User* u) { 
    id_nick_pairs.push_back( pair<int,string>( u->getId(), u->getLogin()) );
}

void ProtoMsg::serialize(int sockfd) {

    FILE* fp = fdopen(sockfd,"w");
    FileStream s(fp);

    PrettyWriter<FileStream> writer(s);

    writer.StartObject();
        
    writer.String("action");
    writer.String(action.c_str());

    writer.String("nick_uid");
    writer.StartArray();

    for (vector<pair<int,string> >::iterator it=id_nick_pairs.begin(); it != id_nick_pairs.end(); ++it) {
        writer.StartObject();
        writer.String((*it).second.c_str() );
        writer.Int((*it).first);    
        writer.EndObject();
    }
    writer.EndArray();


    writer.EndObject();

    printf("Finished writing.\n");

}
