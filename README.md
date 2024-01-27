# Game Rule
In Tic-Tac-Toe, two players take turns marking a spot on a 3x3 grid with 'X' or 'O'. The goal is to
get three of your marks in a row, either horizontally, vertically, or diagonally. If all spots are
filledand no one wins, the game ends in a draw.

# UI/UX
![image](https://github.com/yzj-jzy/Tic-Tac-Toe/assets/80561240/68a55e6e-9fee-4eb9-ada8-9754680efb44)


# Key feature
- [x] Player can enter their name when register new user 
- [x] Player match and chess symbol(X or O) allocated to player
- [x] Turn to turn game and finalised when wining condition meet
- [x] 15 seconds count down for each trun and random chess drawed when time out
- [x] Player rank system
- [x] Fault tolerance when player crash/existing from game

# Setup
1. Clone into own PC and unZip into a folder
2. open the command line by dictionary path
3. Run Server
```
java -jar server.jar ip port
```
Please Place ip and port with real one
etc ip: localhost port 8080</br>
Warning: Do dot close it unless not more game running

4. Run client(New player)
```
java -jar client.jar username server_ip server_port
```
The server_ip and server_port should corresponse to sever one in step3
