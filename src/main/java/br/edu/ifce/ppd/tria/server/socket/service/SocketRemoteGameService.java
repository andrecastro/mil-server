package br.edu.ifce.ppd.tria.server.socket.service;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.core.service.GameService;
import br.edu.ifce.ppd.tria.server.business.GameBusiness;
import br.edu.ifce.ppd.tria.server.socket.model.SocketClient;

import java.util.ArrayList;
import static br.edu.ifce.ppd.tria.core.protocol.helper.ActionBuilder.anAction;

/**
 * Created by andrecoelho on 2/16/16.
 */
public class SocketRemoteGameService implements GameService {

    private GameBusiness gameBusiness;

    public SocketRemoteGameService(GameBusiness gameBusiness) {
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
        socketClient.send(notifyEnterGame);

        return anAction()
                .to("game-service/enter-game")
                .withParamValue("game", game)
                .build();
    }

    @Override
    public Action putPieceInSpot(Client currentClient, String gameId, Integer selectedSpotId) {
        Game game = gameBusiness.putPieceInSpot(currentClient, gameId, selectedSpotId);

        Action notifyPutPiece = anAction()
                .to("game-service/notify-put-piece")
                .withParamValue("game", game)
                .build();

        Client opponentClient = game.getOpponentClientOf(currentClient);
        SocketClient socketOpponentClient = (SocketClient) gameBusiness.getFromRepository(opponentClient);
        socketOpponentClient.send(notifyPutPiece);

        return anAction()
                .to("game-service/put-piece")
                .withParamValue("game", game)
                .build();
    }

    @Override
    public Action removePiece(Client client, String gameId, Integer selectedSpotId) {
        Game game = gameBusiness.removePiece(client, gameId, selectedSpotId);

        if (gameBusiness.isGameOver(client, game)) {
            gameBusiness.finishGame(game);
            game.setGameOver(true);
        }

        Action notifyRemovePiece = anAction()
                .to("game-service/notify-remove-piece")
                .withParamValue("game", game)
                .build();

        Client opponentClient = game.getOpponentClientOf(client);
        SocketClient socketOpponentClient = (SocketClient) gameBusiness.getFromRepository(opponentClient);
        socketOpponentClient.send(notifyRemovePiece);

        return anAction()
                .to("game-service/remove-piece")
                .withParamValue("game", game)
                .build();
    }

    @Override
    public Action movePiece(Client client, String gameId, Integer fromSpotId, Integer toSpotId) {
        Game game = gameBusiness.movePiece(gameId, client, fromSpotId, toSpotId);

        Action notifyPutPiece = anAction()
                .to("game-service/notify-move-piece")
                .withParamValue("game", game)
                .build();

        Client opponentClient = game.getOpponentClientOf(client);
        SocketClient socketOpponentClient = (SocketClient) gameBusiness.getFromRepository(opponentClient);
        socketOpponentClient.send(notifyPutPiece);

        return anAction()
                .to("game-service/move-piece")
                .withParamValue("game", game)
                .build();
    }

    @Override
    public Action giveUp(Client client) {
        Game game = gameBusiness.giveUp(client);

        Action notifyGiveUp = anAction()
                .to("game-service/notify-give-up")
                .build();

        if (game.isSecondPlayer(client)) {
            SocketClient socketClient = (SocketClient) gameBusiness.getClientOf(game.getFirstPlayer());
            socketClient.send(notifyGiveUp);
        } else if (game.getSecondPlayer() != null) {
            SocketClient socketClient = (SocketClient) gameBusiness.getClientOf(game.getSecondPlayer());
            socketClient.send(notifyGiveUp);
        }

        return anAction().to("game-service/give-up").build();
    }

    @Override
    public Action askToRestartGame(Client client, String gameId) {
        Game game = gameBusiness.getGame(gameId);

        Action notifyAskToRestart = anAction()
                .to("game-service/notify-ask-to-restart")
                .build();

        Client opponentClient = game.getOpponentClientOf(client);
        SocketClient socketOpponentClient = (SocketClient) gameBusiness.getFromRepository(opponentClient);
        socketOpponentClient.send(notifyAskToRestart);

        return anAction().to("game-service/ask-to-restart").build();
    }

    @Override
    public Action restartGame(Client client, String gameId) {
        Game game = gameBusiness.restartGame(gameId);

        Action notifyRestartGame = anAction()
                .to("game-service/notify-restart-game")
                .withParamValue("game", game)
                .build();

        Client opponentClient = game.getOpponentClientOf(client);
        SocketClient socketOpponentClient = (SocketClient) gameBusiness.getFromRepository(opponentClient);
        socketOpponentClient.send(notifyRestartGame);

        return notifyRestartGame;
    }
}
