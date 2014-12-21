#pragma once
#include "Msg.h"
#define STDMSG_MAXLEN sizeof(int)*4

class StdMsg : public Msg {
    
    char content[STDMSG_MAXLEN];


    public:

    StdMsg(const char* content);
    ~StdMsg();

    char* serialize();

};
