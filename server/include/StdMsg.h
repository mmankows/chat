#pragma once
#include "Msg.h"
#define MAX_MSG_SIZE 2048



class StdMsg : public Msg {
    private:

    string content;
    bool serialize(int sockfd);
    bool deserialize(int sockfd);
   
    

    public:
    StdMsg();
    StdMsg(string content);
    StdMsg(vector<int> to_id, string content);
    ~StdMsg();


   
    protected:
    friend struct MyHandler;
    struct MyHandler {
        StdMsg*  msg_ptr;
        bool in_array;
        bool in_to_id;
        bool in_content;
        
        bool Uint(unsigned u) { return Int(u); }
        bool Bool(bool b) { cout << "Bool(" << b << ")" << endl; return true; }
        bool Int64(int64_t i) { return Int(i); }
        bool Uint64(uint64_t u) { return Int(u); }
        bool Double(double d) { return false; }
        bool Key(const char* str, SizeType length, bool copy) {
            if( !strncmp(str,"to_id",length) ) {
                in_to_id = true;
                in_content = false;
            }
            else if( !strncmp(str,"content",length) ) {
                in_content = true;
                in_to_id   = false;
            }
            else {
                in_content = in_to_id = false; 
            }
            return true;
        }
        
        bool Null() { return true; }
        bool Int(int i) { 
            if(in_to_id && in_array) 
                msg_ptr->to_id.push_back(i);
            return true;
        }
        bool String(const char* str, SizeType length, bool copy) { 
            if( in_content ) {
                msg_ptr->content = string(str);
            }
            return true;
        }
        bool StartObject() { return true; }
        bool EndObject(SizeType memberCount) { return true; }
        bool StartArray() { in_array = true; return true; }
        bool EndArray(SizeType elementCount) { in_array = false; return true; }
    }  parser;
};
