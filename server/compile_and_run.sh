g++ -ansi src/* main.c -o chatServer -pthread -lssl -lcrypto
./chatServer 1>&2 2>/dev/null & #run in background
