#pragma once
#include <cstring>

class Msg {
    protected:
    int type;
    int id_to;
    int content_len;
    
    public:
    Msg();
    ~Msg();

    virtual char* serialize(void) =0;

};

