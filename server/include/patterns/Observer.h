#pragma once


//Po klasie obserwator dziedziczy klasa użytkownika serwera (User)
template <typename MSG>
class Observer {
    
    public:
    virtual void notify(MSG&)=0;

};

//Po tej klasie dziedziczy klasa User list przechowywujaca liste uzytkownikow (UserList)
//przy zmianie stanu listy nalezy powiadomic uzytkownikow.
template <typename OBS,typename MSG>
class Subject {
    
    public:
    virtual void registerObserver(OBS*) = 0;
    virtual void unregisterObserver(int user_id) = 0;
    virtual void notifyObservers(MSG&) = 0;


};

