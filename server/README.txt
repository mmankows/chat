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


Client - Server Flow:

1. Client logs into the server by sending authentication token.
"login\036password"
2. Server Responds with user list if login exist in db and match password.
Otherwise it close connection.
3. User list sent to authenticated client is ProtoMsg type.
4. From this moment client got subscribed to the user list, becomes visible to
other users and their notifications (about joining/leaving server, similar to
ProtoMsg, but containing one record. If leave notification comes with
action:del, that means client should remove user from his user list. Joining
notification come with action:add, means new user should be added)
5. Server is waiting for message Json type StdMsg 
where type always should be set to 1, to_id should contain integer list of
userid that shoul get a message. Content is just a message body


