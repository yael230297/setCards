CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: StompWCIClient

EchoClient: bin/ConnectionHandler.o bin/echoClient.o
	g++ -o bin/EchoClient bin/ConnectionHandler.o bin/echoClient.o $(LDFLAGS)

StompWCIClient: bin/utils.o bin/event.o bin/game.o bin/ConnectionHandler.o bin/user.o bin/keyboardParsing.o bin/StompClient.o bin/StompFrame.o bin/ConnectedFrame.o bin/ConnectFrame.o bin/DisconnectFrame.o bin/ErrorFrame.o bin/MessageFrame.o bin/ReceiptFrame.o bin/SendFrame.o bin/SubscribeFrame.o bin/UnsubscribeFrame.o bin/SummaryFrame.o
	g++ -o bin/StompWCIClient bin/utils.o bin/event.o bin/game.o bin/ConnectionHandler.o bin/user.o bin/keyboardParsing.o bin/StompClient.o bin/StompFrame.o bin/ConnectedFrame.o bin/ConnectFrame.o bin/DisconnectFrame.o bin/ErrorFrame.o bin/MessageFrame.o bin/ReceiptFrame.o bin/SendFrame.o bin/SubscribeFrame.o bin/UnsubscribeFrame.o bin/SummaryFrame.o $(LDFLAGS)

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/event.o: src/event.cpp
	g++ $(CFLAGS) -o bin/event.o src/event.cpp

bin/StompClient.o: src/StompClient.cpp
	g++ $(CFLAGS) -o bin/StompClient.o src/StompClient.cpp
#
bin/utils.o: src/utils.cpp
	g++ $(CFLAGS) -o bin/utils.o src/utils.cpp

bin/user.o: src/user.cpp
	g++ $(CFLAGS) -o bin/user.o src/user.cpp

bin/keyboardParsing.o: src/keyboardParsing.cpp
	g++ $(CFLAGS) -o bin/keyboardParsing.o src/keyboardParsing.cpp

bin/game.o: src/game.cpp
	g++ $(CFLAGS) -o bin/game.o src/game.cpp

#frames
bin/ConnectedFrame.o: src/Frames/ConnectedFrame.cpp
	g++ $(CFLAGS) -o bin/ConnectedFrame.o src/Frames/ConnectedFrame.cpp

bin/ConnectFrame.o: src/Frames/ConnectFrame.cpp
	g++ $(CFLAGS) -o bin/ConnectFrame.o src/Frames/ConnectFrame.cpp

bin/DisconnectFrame.o: src/Frames/DisconnectFrame.cpp
	g++ $(CFLAGS) -o bin/DisconnectFrame.o src/Frames/DisconnectFrame.cpp

bin/ErrorFrame.o: src/Frames/ErrorFrame.cpp
	g++ $(CFLAGS) -o bin/ErrorFrame.o src/Frames/ErrorFrame.cpp

bin/MessageFrame.o: src/Frames/MessageFrame.cpp
	g++ $(CFLAGS) -o bin/MessageFrame.o src/Frames/MessageFrame.cpp

bin/ReceiptFrame.o: src/Frames/ReceiptFrame.cpp
	g++ $(CFLAGS) -o bin/ReceiptFrame.o src/Frames/ReceiptFrame.cpp

bin/SendFrame.o: src/Frames/SendFrame.cpp
	g++ $(CFLAGS) -o bin/SendFrame.o src/Frames/SendFrame.cpp

bin/StompFrame.o: src/Frames/StompFrame.cpp
	g++ $(CFLAGS) -o bin/StompFrame.o src/Frames/StompFrame.cpp

bin/SubscribeFrame.o: src/Frames/SubscribeFrame.cpp
	g++ $(CFLAGS) -o bin/SubscribeFrame.o src/Frames/SubscribeFrame.cpp

bin/UnsubscribeFrame.o: src/Frames/UnsubscribeFrame.cpp
	g++ $(CFLAGS) -o bin/UnsubscribeFrame.o src/Frames/UnsubscribeFrame.cpp

bin/SummaryFrame.o: src/Frames/SummaryFrame.cpp
	g++ $(CFLAGS) -o bin/SummaryFrame.o src/Frames/SummaryFrame.cpp

.PHONY: clean
clean:
	rm -f bin/*
	