package br.edu.ifce.ppd.tria.server.socket;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.server.socket.service.SocketChatService;
import br.edu.ifce.ppd.tria.server.socket.service.SocketGameService;
import br.edu.ifce.ppd.tria.server.socket.model.SocketClient;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRegisterService;

import java.util.HashMap;

/**
 * Created by andrecoelho on 2/17/16.
 */
public class Route {

    private SocketChatService chatService;
    private SocketGameService gameService;
    private SocketRegisterService registerService;

    public Route(SocketRegisterService registerService,
                 SocketGameService gameService, SocketChatService chatService) {
        this.registerService = registerService;
        this.gameService = gameService;
        this.chatService = chatService;
    }

    public Action to(Action action, SocketClient client) {
        String[] paths = action.getPath().split("/");
        HashMap body = action.getBody();

        switch (paths[0]) {
            case "game-service":
                return handleGameService(paths[1], body, client);
            case "chat-service":
                return handleChatService(paths[1], body, client);
            default:
                return new Action(null, null); //TODO create better error
        }
    }

    public void toDeregister(Client client) {
        registerService.deregister(client);
    }

    public SocketClient toRegistration(SocketConnection connection) {
        return registerService.register(connection);
    }

    private Action handleChatService(String path, HashMap body, SocketClient client) {
        switch (path) {
            case "send-message":
                return chatService.sendMessage(client, (String) body.get("message"));
            default:
                return new Action(null, null); // TODO create better error
        }
    }

    private Action handleGameService(String path, HashMap body, SocketClient client) {
        switch (path) {
            case "idle-games":
                return gameService.retrieveIdleGames();
            case "create-game":
                return gameService.createGame(client,(String) body.get("game-alias") ,(String) body.get("player-name"));
            case "enter-game":
                return gameService.enterGame(client, (String) body.get("game-id"), (String) body.get("player-name"));
            default:
                return new Action(null, null); // TODO create better error
        }
    }
}
