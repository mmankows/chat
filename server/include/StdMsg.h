#pragma once
#include "Msg.h"

class StdMsg : public Msg {
    char* buffer;

    public:

    StdMsg();
    ~StdMsg();

    char* serialize();
    



};
