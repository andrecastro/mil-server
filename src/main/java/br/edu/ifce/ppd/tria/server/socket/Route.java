package br.edu.ifce.ppd.tria.server.socket;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.server.socket.config.RouteExecutor;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRemoteChatService;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRemoteGameService;
import br.edu.ifce.ppd.tria.server.socket.model.SocketClient;
import br.edu.ifce.ppd.tria.server.socket.service.SocketRemoteRegisterService;

import java.util.HashMap;

/**
 * Created by andrecoelho on 2/17/16.
 */
public class Route {

    private SocketRemoteChatService chatService;
    private SocketRemoteGameService gameService;
    private SocketRemoteRegisterService registerService;

    private HashMap<String, RouteExecutor> configuredRoutes;

    public Route(SocketRemoteRegisterService registerService,
                 SocketRemoteGameService gameService, SocketRemoteChatService chatService) {
        this.registerService = registerService;
        this.gameService = gameService;
        this.chatService = chatService;
        this.configuredRoutes = new HashMap<>();
        this.configure();
    }

    public Action to(Action action, SocketClient client) {
        RouteExecutor executor = configuredRoutes.get(action.getPath());
        return executor.execute(client, action.getBody());
    }

    public void toDeregister(Client client) {
        registerService.deregister(client);
    }

    public SocketClient toRegistration(SocketConnection connection) {
        return (SocketClient) registerService.register(new SocketClient(connection, registerService.createClient()));
    }

    private void configure() {
        addRoute("game-service/idle-games", (client, body) ->  {
            return gameService.retrieveIdleGames();
        });

        addRoute("game-service/create-game", (client, body) ->  {
            return gameService.createGame(client, (String) body.get("game-alias"), (String) body.get("player-name"));
        });

        addRoute("game-service/enter-game", (client, body)-> {
            return  gameService.enterGame(client, (String) body.get("game-id"), (String) body.get("player-name"));
        });

        addRoute("game-service/put-piece-in-spot", (client, body)-> {
            String gameId = (String) body.get("game-id");
            Integer selectedSpotId = (Integer) body.get("selected-spot-id");
            return  gameService.putPieceInSpot(client, gameId, selectedSpotId);
        });

        addRoute("game-service/remove-piece", (client, body)-> {
            String gameId = (String) body.get("game-id");
            Integer selectedSpotId = (Integer) body.get("selected-spot-id");
            return  gameService.removePiece(client, gameId, selectedSpotId);
        });

        addRoute("game-service/move-piece", (client, body)-> {
            String gameId = (String) body.get("game-id");
            Integer fromSpotId = (Integer) body.get("from-spot-id");
            Integer toSpotId = (Integer) body.get("to-spot-id");
            return  gameService.movePiece(client, gameId, fromSpotId, toSpotId);
        });

        addRoute("game-service/give-up", (client, body)-> {
            return  gameService.giveUp(client);
        });

        addRoute("game-service/ask-to-restart", (client, body)-> {
            return  gameService.askToRestartGame(client, (String) body.get("game-id"));
        });

        addRoute("game-service/restart-game", (client, body)-> {
            return  gameService.restartGame(client, (String) body.get("game-id"));
        });

        addRoute("chat-service/send-message", (client, body) -> {
            return chatService.sendMessage(client, (String) body.get("message"));
        });
    }

    private void addRoute(String path, RouteExecutor executor) {
        this.configuredRoutes.put(path, executor);
    }
}
