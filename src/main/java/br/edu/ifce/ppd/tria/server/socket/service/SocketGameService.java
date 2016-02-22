package br.edu.ifce.ppd.tria.server.socket.service;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.core.protocol.helper.ActionBuilder;
import br.edu.ifce.ppd.tria.core.service.GameService;
import br.edu.ifce.ppd.tria.server.business.GameBusiness;
import br.edu.ifce.ppd.tria.server.socket.model.SocketClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static br.edu.ifce.ppd.tria.core.protocol.helper.ActionBuilder.anAction;

/**
 * Created by andrecoelho on 2/16/16.
 */
public class SocketGameService implements GameService {

    private GameBusiness gameBusiness;

    public SocketGameService(GameBusiness gameBusiness) {
        this.gameBusiness = gameBusiness;
    }

    @Override
    public Action retrieveIdleGames() {
        return anAction()
                .to("game-service/idle-games")
                .withParamValue("idle-games", new ArrayList<>(gameBusiness.idleGames()))
                .build();
    }

    @Override
    public Action createGame(Client client,String alias, String firstPlayerName) {
        return anAction()
                .to("game-service/create-game")
                .withParamValue("game", gameBusiness.createGame(client, alias, firstPlayerName))
                .build();
    }

    @Override
    public Action enterGame(Client client, String gameId, String secondPlayerName) {
        Game game = gameBusiness.enterGame(client, gameId, secondPlayerName);

        Action notifyEnterGame = anAction()
                .to("game-service/notify-enter-game")
                .withParamValue("game", game)
                .build();

        SocketClient socketClient = (SocketClient) gameBusiness.getClientOf(game.getFirstPlayer());
        socketClient.getConnection().send(notifyEnterGame);

        return anAction()
                .to("game-service/enter-game")
                .withParamValue("game", game)
                .build();
    }
}
