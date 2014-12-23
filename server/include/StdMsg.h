#pragma once
#include "Msg.h"
#define STDMSG_MAXLEN sizeof(int)*4

class StdMsg : public Msg {
    private:

    char content[STDMSG_MAXLEN];
    
    
    void serialize(int sockfd);

    public:

    StdMsg(const char* content);
    ~StdMsg();


};
