import threading
import socket
import tictactoe_logic as engine
from constants import *

ip = "10.27.208.107"
port = 3020

server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.bind((ip, port))
server.listen(5)

print "Server started up on {}:{}".format(ip, port)

sockets = []
assignedX = assignedO = False


def handle_connection(client, id):
    global assignedX, assignedO
    sockets.append(client)
    print "Got Socket With Id", id
    if not assignedX:
        send(client, "X", PLAYER_SET)
        assignedX = True
        player = "X"
    elif not assignedO:
        send(client, "O", PLAYER_SET)
        assignedO = True
        player = "O"
    else:
        send(client, "full")
        return
    print assignedO, assignedX, engine.game_started
    if assignedX and assignedO and not engine.game_started:
        print "Starting Game"
        engine.start_game()
    try:
        while True:
            try:
                data = client.recv(1024)
                if not engine.game_started:
                    continue
                if data == "end":

                    break
            except:
                break
            # print "Got {} from player {}".format(data, player)

            if not assignedX or not assignedO or not engine.game_started:
                continue

            if data.startswith(BUTTON_PRESS):
                data = data[len(BUTTON_PRESS)::]
                engine.process_input(data, player)
            # print "Board is", engine.board_to_send()
            broadcast(engine.board_to_send(), BOARD_SEND)
            if engine.victory:
                broadcast(engine.currentTurn, WINNER)
                engine.start_game()
    finally:
        client.close()
        sockets.remove(client)

        if player == "X":
            assignedX = False
            print "Disconnected X"
        elif player == "O":
            assignedO = False
            print "Disconnected O"

        print "Closed Connection!"


def broadcast(msg, tag=""):
    for s in sockets:
        send(s, msg, tag)


def send(client, msg, tag=""):
    client.send(tag + msg+"\n")


try:
    while True:
        conn, address = server.accept()
        print "Got connection from {}:{}".format(address[0], address[1])
        handler = threading.Thread(target=handle_connection, args=(conn, len(sockets)))
        handler.__args = (conn, handler)
        handler.start()

finally:
    server.close()

