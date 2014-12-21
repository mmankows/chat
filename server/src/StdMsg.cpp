#include "../include/StdMsg.h"
#include <cstdio>

StdMsg::StdMsg(const char* content) : Msg(){
    type        = -1;
    id_to       = -1;
    content_len = strlen(content);
    bcopy(content, this->content,content_len+1);

}
StdMsg::~StdMsg() {}
char* StdMsg::serialize() {
    int header[4] = {type, id_to, -1, content_len};
    char* smsg    = new char[sizeof(header) + 255];
    
    memcpy((void*)smsg,                  (void*)header,         sizeof(header));
    memcpy((void*)(smsg+sizeof(header)), (void*)this->content,  strlen(this->content));
    for(int i=0; i<255+sizeof(header); i++) {
        printf("%c",smsg[i]);
    }
    
    return smsg;
}
