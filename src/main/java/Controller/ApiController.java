package Controller;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamException;


public class ApiController {
    public static boolean isSteamInitialized;

    public ApiController() {
        // Empty constructor 
    }

    // Attempts to connect to Steam
    public boolean initSteam() {
        try {
            SteamAPI.loadLibraries();
            if (getIsSteamInitialized() == false || !SteamAPI.init()) {
                SteamAPI.init();
                if (SteamAPI.init()){
                System.out.println("Success! SteamAPI initialized.");
            } else {
                System.out.println("Failed to initialize Steamworks. Open Steam.");
                return false;
            }
                isSteamInitialized = true;
                return true;
            } else {
                System.out.println("Failed to initialize SteamAPI.");
                return false;
            }
        } catch (SteamException e) {
            System.out.println("A Steam error occurred during init");
            e.printStackTrace();
            return false;
        }
    }

    // Safely disconnects from Steam when the game closes
    public void shutdownSteam() {
        if (SteamAPI.isSteamRunning()) {
            SteamAPI.shutdown();
            System.out.println("SteamAPI shut down cleanly.");
        }
    }

    public static boolean getIsSteamInitialized() {
        return isSteamInitialized;
    }
}