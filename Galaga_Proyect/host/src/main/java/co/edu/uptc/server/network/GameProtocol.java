package co.edu.uptc.server.network;

public class GameProtocol {

    private static final String CLIENT_ACTION_PREFIX = "CLIENT_ACTION:";
    public static final String GAME_STATE_UPDATE_PREFIX = "GAME_STATE:";

    public static ClientAction interpretClientAction(String input) {
        if (input.startsWith(CLIENT_ACTION_PREFIX)) {
            String action = input.substring(CLIENT_ACTION_PREFIX.length());
            switch (action) {
                case "MOVE_LEFT":
                    return ClientAction.MOVE_LEFT;
                case "MOVE_RIGHT":
                    return ClientAction.MOVE_RIGHT;
                case "SHOOT":
                    return ClientAction.SHOOT;
                case "START":
                    return ClientAction.START_GAME;
                case "PAUSE":
                    return ClientAction.PAUSE_GAME;
                case "RESUME": 
                return ClientAction.RESUME_GAME;
                 case "RESTART": 
                return ClientAction.RESTART_GAME;
                    default:
                    return ClientAction.UNKNOWN;
            }
        }
        return ClientAction.UNKNOWN;
    }

    public static String createGameStateUpdate(String gameStateJson) {
        return GAME_STATE_UPDATE_PREFIX + gameStateJson;
    }

    public static String extractGameState(String message) {
        if (message.startsWith(GAME_STATE_UPDATE_PREFIX)) {
            return message.substring(GAME_STATE_UPDATE_PREFIX.length());
        }
        return null;
    }

    public static String formatClientAction(ClientAction action) {
        return CLIENT_ACTION_PREFIX + action.toString();
    }
}
