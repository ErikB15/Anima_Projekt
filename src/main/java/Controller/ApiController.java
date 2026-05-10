package Controller;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworkingCallback;
import com.codedisaster.steamworks.SteamResult;

/**
 * @author Daniel
 * Klass för att hantera nätverk och matchmaking mellan klient och Steamworks
 */
public class ApiController {
    public static boolean isSteamInitialized;

    private static SteamMatchmaking matchmaking;
    private SteamFriends friends;

    private Thread callbackThread;
    private volatile boolean running;

    private static SteamNetworking networking;
    private static SteamID opponentSteamID = null;
    private static boolean isHost = false;

    public ApiController() {
        // Empty constructor 
    }

    // Attempts to connect to Steam
    public boolean initSteam() {
        try {
            SteamAPI.loadLibraries();
            if (getIsSteamInitialized() == false) {

                try{
                    SteamAPI.init();
                } catch (Exception e) {
                    System.out.println("SteamAPI failed to initialize. Make sure Steam is running and is signed in to an account.");
                    return false; 
                }           
                

                System.out.println("Success! SteamAPI initialized.");
                isSteamInitialized = true;


                    // Initialize Steam Interfaces
                    matchmaking = new SteamMatchmaking(matchmakingCallback);
                    friends = new SteamFriends(friendsCallback);

                    networking = new SteamNetworking(networkingCallback);
                   
                    // Start the background thread to listen for Steam events
                    startCallbackThread();  // Starts the background thread 
                    String selfName = friends.getPersonaName();  // Gets the name of the user who is currently logged in
                    System.out.println("Signed in as " + selfName);

                
                return true;
            } else {
                System.out.println("Failed to initialize SteamAPI.");
                return false;
            }
        } catch (SteamException e) {
            System.out.println("A Steam error occurred during init!");
            e.printStackTrace();
            return false;
        }
    }

    // Background loop to process Steam events 60 times a second
    private void startCallbackThread() {
        running = true;
        callbackThread = new Thread(() -> {
            // Loop until the game closes
            while (running && SteamAPI.isSteamRunning()) {
                SteamAPI.runCallbacks();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        callbackThread.setDaemon(true); // Ensures thread closes when game closes
        callbackThread.start();
    }

    public static void hostLobby() {
        if (isSteamInitialized && matchmaking != null) {
            System.out.println("Creating Steam Lobby...");
            isHost = true;
            matchmaking.createLobby(SteamMatchmaking.LobbyType.FriendsOnly, 2);
        }
    }

    // STEAM MATCHMAKING CALLBACKS 

    // Mestadels meningslöst bullshit
    private SteamMatchmakingCallback matchmakingCallback = new SteamMatchmakingCallback() {
        @Override
        public void onLobbyCreated(SteamResult result, SteamID steamIDLobby) {
            if (result == SteamResult.OK) {
                System.out.println("Lobby created successfully! Lobby ID: " + steamIDLobby.getAccountID());
                
                // Pop open the native Steam Overlay to invite friends
                if (friends != null) {
                    friends.activateGameOverlayInviteDialog(steamIDLobby);
                }
            } else {
                System.out.println("Failed to create lobby. Result: " + result);
            }
        }
        

        @Override
        public void onLobbyEnter(SteamID steamIDLobby, int chatPermissions, boolean blocked, SteamMatchmaking.ChatRoomEnterResponse response) {
            if (response == SteamMatchmaking.ChatRoomEnterResponse.Success) {
                System.out.println("Successfully entered lobby: " + steamIDLobby.getAccountID());
                 //TODO Transition to game lobby screen in GUI

                 if (!isHost) {
                    //TODO Notify player that they have joined the game
                    opponentSteamID = opponentSteamID = matchmaking.getLobbyOwner(steamIDLobby);
                    System.out.println("Joined host's lobby. Found opponent: " + opponentSteamID.getAccountID());;
                    System.out.println("Friend on ID "+ steamIDLobby.getAccountID() + " joined");
                }

            }
        }

        public void onFavoritesListChanged(int ip, int queryPort, int connPort, int appID, int flags, boolean add, int accountID) {}
        public void onLobbyInvite(SteamID steamIDUser, SteamID steamIDLobby, long gameID) {
            System.out.println("Invite received from " + steamIDUser.getAccountID() + " to join lobby");
        }
        public void onLobbyDataUpdate(SteamID steamIDLobby, SteamID steamIDMember, boolean success) {}
        @Override
        public void onLobbyChatUpdate(SteamID steamIDLobby, SteamID steamIDUserChanged, SteamID steamIDMakingChange, SteamMatchmaking.ChatMemberStateChange stateChange) {
            // FIX FOR HOST: 
            // This triggers specifically when a new member enters the room.
            if (isHost && stateChange == SteamMatchmaking.ChatMemberStateChange.Entered) {
                // The 'steamIDUserChanged' parameter is provided by this method automatically
                opponentSteamID = steamIDUserChanged;
                System.out.println("Friend entered the lobby! Found opponent: " + opponentSteamID.getAccountID());
                
                // Trigger the transition to the next screen
                onConnectionHandshakeComplete();
            }
        }
        public void onLobbyChatMessage(SteamID steamIDLobby, SteamID steamIDUser, SteamMatchmaking.ChatEntryType entryType, int chatID) {}
        public void onLobbyGameCreated(SteamID steamIDLobby, SteamID steamIDGameServer, int ip, short port) {
            System.out.println("Game created in lobby");
        }
        public void onLobbyMatchList(int lobbiesMatching) {}


        public void onLobbyKicked(SteamID steamIDUser, SteamID steamIDHoster, boolean blocked) { 
    System.out.println("Player was kicked from lobby"); 
}



public void onFavoritesListAccountsUpdated(SteamResult result) {
    // Can handle if needed
}

    };


public void onLobbyKicked(SteamID steamIDUser, SteamID steamIDHoster, boolean blocked) {
    System.out.println("Player was kicked from lobby");
}


public void onFavoritesListAccountsUpdated(SteamResult result) {
    // Can handle if needed
}





    //  STEAM FRIENDS CALLBACKS 
    private SteamFriendsCallback friendsCallback = new SteamFriendsCallback() {
        @Override
        public void onGameLobbyJoinRequested(SteamID steamIDLobby, SteamID steamIDFriend) {
            System.out.println("Friend accepted the invite! Joining lobby...");
            isHost = false;
   
            if (matchmaking != null) {
                matchmaking.joinLobby(steamIDLobby);
            }
        }

        public void onSetPersonaNameResponse(boolean success, boolean localSuccess, SteamResult result) {}
        public void onPersonaStateChange(SteamID steamID, SteamFriends.PersonaChange change) {}
        public void onGameOverlayActivated(boolean active) {}
        public void onGameServerChangeRequested(String server, String password) {}
        public void onAvatarImageLoaded(SteamID steamID, int image, int width, int height) {}
        public void onFriendRichPresenceUpdate(SteamID steamIDFriend, int appID) {}
        public void onGameRichPresenceJoinRequested(SteamID steamIDFriend, String connect) {}
        public void onGameServerJoinRequested(SteamID steamIDLobby, SteamID steamIDFriend) {}
    };
    


    private void onConnectionHandshakeComplete() {
        System.out.println("HANDSHAKE COMPLETE");
  
    }


    // STEAM NETWORKING CALLBACKS 
    private SteamNetworkingCallback networkingCallback = new SteamNetworkingCallback() {
        @Override
        public void onP2PSessionRequest(SteamID steamIDRemote) {
            System.out.println("Incoming network request from: " + steamIDRemote.getAccountID());
            
            // If the request comes from the opponent we met in the lobby, let them in!
            if (opponentSteamID != null && steamIDRemote.equals(opponentSteamID)) {
                networking.acceptP2PSessionWithUser(steamIDRemote);
                System.out.println("Accepted P2P pipe connection with opponent!");
            }
        }

        @Override
        public void onP2PSessionConnectFail(SteamID steamIDRemote, SteamNetworking.P2PSessionError sessionError) {
            System.out.println("Failed to connect pipe to: " + steamIDRemote.getAccountID() + ". Error: " + sessionError);
        }
    };

    // Safely disconnects from Steam when the game closes
    public void shutdownSteam() {
        if (SteamAPI.isSteamRunning()) {
            SteamAPI.shutdown();
            running = false;
            System.out.println("SteamAPI shut down cleanly.");
        }
    }

    public static boolean getIsSteamInitialized() {
        return isSteamInitialized;

    }
}