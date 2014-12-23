#include "../include/StdMsg.h"
#include <cstdio>

StdMsg::StdMsg(const char* content) : Msg(){
    type        = -1;
    id_to       = -1;
    content_len = strlen(content);
    bcopy(content, this->content,content_len+1);

}
StdMsg::~StdMsg() {}

void StdMsg::serialize(int sockfd) {

    FILE* fp = fdopen(1,"w");
    FileStream s(fp);

    PrettyWriter<FileStream> writer(s);

    writer.StartObject();
        
    writer.String("school");
    writer.String("GPA");

    writer.EndObject();

    printf("Finished writing.\n");

}
