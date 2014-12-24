Supported Msg JSONs :



ProtoMsg (Sent by server, contains user list)

{
    "type":2,
    "action":"(add|del)",
    "nick_uid": [
        "nickname1" : userid(int),
        "nickname2" : userid(int)
    
            ....
    ]
}


StdMsg (Sent by server from user or from user to another user);

{
    "type":1,
    "to_id":[
        id1(int),
        id2(int)

        ....
    ],
    "content" : "Somemsgbody"

}
