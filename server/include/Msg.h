#pragma once

class Msg {
    int type;
    int id_receiver;
    
    public:
    Msg();
    ~Msg();

    virtual char* serialize(void) =0;

};

