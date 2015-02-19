#include "../include/StdMsg.h"
#include <cstdio>
#include <unistd.h>


StdMsg::StdMsg() : Msg(1) {
    parser.msg_ptr = this;
    cout<<"StdMsg()\n";
}

StdMsg::StdMsg(string content) : Msg(1) {
    parser.msg_ptr = this;
    this->content = content;
}

StdMsg::StdMsg(vector<int> to_id, string content) : Msg(1) {
    parser.msg_ptr = this;
    this->to_id   = to_id;
    this->content = content;
}

StdMsg::~StdMsg() {}

bool StdMsg::serialize(int sockfd) {
    cout<<"StdMsg::serialize()\n";

    FILE* fp = fdopen(sockfd,"w");
    FileStream s(fp);

    Writer<FileStream> writer(s);

    writer.StartObject();
        
    writer.String("type");
    writer.Int(type);
    
    writer.String("from_id");
    writer.Int(from_id);

    writer.String("to_id");
    writer.StartArray();

    for (vector<int>::iterator it=to_id.begin(); it != to_id.end(); ++it) {
        writer.Int((*it));    
    }
    writer.EndArray();
    
    writer.String("content");
    writer.String(content.c_str());
    
    writer.EndObject();

}

bool StdMsg::deserialize(int sockfd) {
    char json[MAX_MSG_SIZE];
    int red = 0;
    
    red = read(sockfd, json,MAX_MSG_SIZE-1);
    if( red <= 0 ) {
        return false;
        //error
    } 
    json[red] = '\0';

    Reader reader;
    StringStream ss(json);
    reader.Parse(ss, parser);

    return true;

}
