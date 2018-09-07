from constants import *
board = []
currentTurn = ""
players = {"X": 1, "O": 2}
game_started = False
victory = False

def start_game():
    global board, currentTurn, game_started, victory
    board = [0, 0, 0, 0, 0, 0, 0, 0, 0]
    currentTurn = "X"
    game_started = True
    victory = False


def check_victories():
    global victory, currentTurn
    checked = [[board[0], board[1], board[2]], [board[3], board[4], board[5]], [board[6], board[7], board[8]]]
    for i in range(3):
        if checked[i][0] == checked[i][1] and checked[i][1] == checked[i][2] and checked[i][1] != 0:
            victory = True
            return
        if checked[0][i] == checked[1][i] and checked[1][i] == checked[2][i] and checked[1][i] != 0:
            victory = True
            return

    if checked[1][1] != 0:
        if checked[0][0] == checked[1][1] and checked[1][1] == checked[2][2]:
            victory = True
            return
        if checked[2][0] == checked[1][1] and checked[1][1] == checked[0][2]:
            victory = True
            return
    draw = True
    for i in board:
        draw = draw and i != 0
    if draw:
        victory = True
        currentTurn = "draw"


def process_input(num, player):
    if player != currentTurn:
        return

    board[int(num)] = player_to_num(player)
    check_victories()
    if victory:
        game_started = False
        return
    switch_turn()


def switch_turn():
    global currentTurn
    currentTurn = "X" if currentTurn == "O" else "O"


def player_to_num(player):
    return players[player]


def board_to_send():
    global board

    return SPLITTER.join(str(x) for x in board)  # board format: 0|1|2|3...

