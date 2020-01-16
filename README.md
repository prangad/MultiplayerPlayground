# MultiplayerPlayground
My first attempt at a multiplayer game using sockets, written entirely using the Java graphics object.

=========================
CONFIGURING A SESSION:
=========================
1. Open the application.
2. Select a username, and press OK. (Using the username "Death" will grant developer access.)
3. If you are the first client to connect, select "Yes" to host the server. Otherwise, select "No". (See "HOSTING A SERVER" to run a game outside of LAN.)
4. Press OK to use the default port, or provide a port you would like to use to host on or connect to the server. (Remember this selection if you decide to use your own.)

Note. In order for the game to operate properly, one server must be running before any non-server clients attempt to connect.

=========================
CONTROLS:
=========================
~                 - Change your player color.
W, A, S, D        - Basic movement.
LMB               - Fire towards your mouse cursor.
1, 2, 3, 4, 5, 6  - Weapon selection.

F1                - Starts the game. (Server host only.)

=========================
HOSTING A SERVER
=========================
In order to host a server, the server host must open a port on their router, and use that port when initially configuring the session. This is only required if you are not playing on a LAN connection.
Clients must connect to the host using the port they provided, as well as the host's public IP address. (Ex. 127.0.0.1:1331)
