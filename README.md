# Tri-Tap-Toe
 3d Tic-Tac-Toe for Android, includes online and local multiplayer with 3 types of bots!
 ---
 
 ## Summary
 Connect 3 Squares of the same size or ascending/descending size in your colour to win. Play in up to 4 players games and stop your friends winning first with both local and online play, with customisable player names and 3 types of bots to fill your games or practise against.
 
 Designed to further learn & practise Kotlin & XML for mobile development. I seem more motivated to make games so this is inspired by the physical board game Otrio. Players have unlimited turns (until the board is full or a winner is declared) to connect 3 squares in a row or even in the same space.
 This project was useful to practise XML again and cement my Kotlin fundamentals, while also providing learning opportunites and challenges - such a bot AI, notifications and Firebase for online multiplayer.
 
| **Lobby** | **Game** | **Restart** |
| ---| ---| ---|
| <img src="https://github.com/Kovah101/Kovah101/blob/main/T-T-T%20Screenshots/Lobby%20Cutdown.png" width="260"> | <img src="https://github.com/Kovah101/Kovah101/blob/main/T-T-T%20Screenshots/Game%20Cutdown.png" width="260"> | <img src="https://github.com/Kovah101/Kovah101/blob/main/T-T-T%20Screenshots/Restart%20Cutdown.png" width="260"> |

---
 
 ## Features

 * Firebase Login - Fiebase integration for online multiplayer and user stats
 
 * Local Lobby - picks max player number, player names and human or bot (with 3 different difficulties)


 *  Online Lobby sends requests and accepts via Firebase Realtime Database, when host confirms then all players can start. Notifications are sent to guests that will take them to the lobby. 


 * Main Game - unique colours for each player, turn indicator, reset button, winning moves flash on screen, starting player increments and bots wait before moving to simulate real players


 * Bot AI -Easy, Medium and Hard bots:
   - Easy simply picks a random free square. 
   - Medium trys to win if 2/3 moves correct, otherwise stops other players from winning, otherwise random moves. 
   - Hard looks at all moves, adds all possible win conditions of each move adjusted for both own moves and other players and picks the highest priority move if it cant outright win or stop the next player from winning.

 * Notifications - Sent with online game request, informs guest player of hosts name, which player they are and takes them to populated Online Lobby 

| Firebase Login/Sign-up screen | Menu | Local/Online Lobby | Main Game | Notificaton |
 | --- | --- | --- | --- | --- |
 |<img src="https://github.com/Kovah101/Kovah101/blob/main/T-T-T%20Screenshots/T-T-T%20Login%201.jpeg" width="250"> | <img src="https://github.com/Kovah101/Kovah101/blob/main/T-T-T%20Screenshots/T-T-T%20Menu.jpeg" width="250"> | <img src="https://github.com/Kovah101/Kovah101/blob/main/T-T-T%20Screenshots/T-T-T%20%20Lobby%201.jpeg" width="250"> | <img src="https://github.com/Kovah101/Kovah101/blob/main/T-T-T%20Screenshots/T-T-T%20Start%20Game.jpeg" width="250"> | <img src="https://github.com/Kovah101/Kovah101/blob/main/T-T-T%20Screenshots/T-T-T%20Notification.jpeg" width="250">
 
 ---
 
 ## Workflow
 1. Build the Main Game, with 4 players, different colours, settings to reset and adjust max players, AI to check for wins and flash winning moves - test for bugs
 2. Create Menu & Firebase Login Activites to start implementing online multiplayer & display gamemodes to player - test for bugs
 3. Create Online Lobby using database listeners on specific branches and sending data between players to initiate online game.
 4. Adjust Main Game to change modes depending on online or offline intent, using listeners again to update players on each move, winner and reset.
 5. Create Offline Lobby using online as template and refactor code to be more modular
 6. Design Bots - used pen and paper then implement bot heirarchy of decisions, then implement those decisions based on the confirmedMoves array. Near wins used to make bots win themselves or stop others. Hard bot move priority starts off simple but when adjusted for own and others moves horizontally, vertically, diagonally, spot and ascending/descending/same size gets very complicated!
 7. Test offline bot functionality, then implement online bots and test them, took a while to figure out how to delay bot turns
 8. Design notification & implement TaskStackBuilder to jump straight to lobby, clean up code and design icons and symbols
 9. Write README

---
 
 ## Future Tasks
 * Refine Bot AI, make Easy bot slightly more complex, relook at priority system for Hard bots
 * Try using FireBase Cloud Messaging to send notifications when app is off
 * Go through design choices (colours, sizes, padding, shapes) 

---
 
 ## Helpful Links
 * [Otrio Boardgame](https://boardgamegeek.com/boardgame/188465/otrio)
