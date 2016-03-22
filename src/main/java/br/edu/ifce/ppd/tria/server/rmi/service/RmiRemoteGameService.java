package br.edu.ifce.ppd.tria.server.rmi.service;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.core.service.GameService;
import br.edu.ifce.ppd.tria.server.business.GameBusiness;
import br.edu.ifce.ppd.tria.server.rmi.helper.Logger;
import br.edu.ifce.ppd.tria.server.rmi.model.RmiClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


/**
 * Created by andrecoelho on 3/15/16.
 */
public class RmiRemoteGameService extends UnicastRemoteObject implements GameService {

    private GameBusiness gameBusiness;

    public RmiRemoteGameService(GameBusiness gameBusiness) throws RemoteException {
        this.gameBusiness = gameBusiness;
    }

    @Override
    public ArrayList<Game> retrieveIdleGames() throws RemoteException {
        Logger.log("request - retrieveIdleGames");

        return new ArrayList<>(gameBusiness.idleGames());
    }

    @Override
    public Game createGame(Client client, String alias, String firstPlayerName) throws RemoteException {
        Logger.log("request - createGame", client, alias, firstPlayerName);

        return gameBusiness.createGame(client, alias, firstPlayerName);
    }

    @Override
    public Game enterGame(Client client, String gameId, String secondPlayerName) throws RemoteException {
        Logger.log("request - enterGame", client, gameId, secondPlayerName);

        Game game = gameBusiness.enterGame(client, gameId, secondPlayerName);

        RmiClient opponentClient = (RmiClient) gameBusiness.getClientOf(game.getFirstPlayer());
        notify(() -> {
            try {
                opponentClient.gameServiceNotifier().notifyEnterGame(game);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        return game;
    }

    @Override
    public Game putPieceInSpot(Client client, String gameId, Integer selectedSpotId) throws RemoteException {
        Logger.log("request - putPieceInSpot", client, gameId, selectedSpotId);

        Game game = gameBusiness.putPieceInSpot(client, gameId, selectedSpotId);

        Client opponentClient = game.getOpponentClientOf(client);
        RmiClient rmiOpponentClient = (RmiClient) gameBusiness.getFromRepository(opponentClient);
        notify(() -> {
            try {
                rmiOpponentClient.gameServiceNotifier().notifyPutPiece(game);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        return game;
    }

    @Override
    public Game removePiece(Client client, String gameId, Integer selectedSpotId) throws RemoteException {
        Logger.log("request - removePiece", client, gameId, selectedSpotId);

        Game game = gameBusiness.removePiece(client, gameId, selectedSpotId);

        if (gameBusiness.isGameOver(client, game)) {
            gameBusiness.finishGame(game);
            game.setGameOver(true);
        }

        Client opponentClient = game.getOpponentClientOf(client);
        RmiClient rmiOpponentClient = (RmiClient) gameBusiness.getFromRepository(opponentClient);
        notify(() -> {
            try {
                rmiOpponentClient.gameServiceNotifier().notifyRemovePiece(game);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        return game;
    }

    @Override
    public Game movePiece(Client client, String gameId, Integer fromSpotId, Integer toSpotId) throws RemoteException {
        Logger.log("request - movePiece", client, gameId, fromSpotId, toSpotId);

        Game game = gameBusiness.movePiece(gameId, client, fromSpotId, toSpotId);

        Client opponentClient = game.getOpponentClientOf(client);
        RmiClient rmiOpponentClient = (RmiClient) gameBusiness.getFromRepository(opponentClient);
        notify(() -> {
            try {
                rmiOpponentClient.gameServiceNotifier().notifyMovePiece(game);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        return game;
    }

    @Override
    public Game askToRestartGame(Client client, String gameId) throws RemoteException {
        Logger.log("request - askToRestartGame", client, gameId);

        Game game = gameBusiness.getGame(gameId);

        Client opponentClient = game.getOpponentClientOf(client);
        RmiClient rmiOpponentClient = (RmiClient) gameBusiness.getFromRepository(opponentClient);
        notify(() -> {
            try {
                rmiOpponentClient.gameServiceNotifier().notifyAskToRestart();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        return game;
    }

    @Override
    public Game restartGame(Client client, String gameId) throws RemoteException {
        Logger.log("request - askToRestartGame", client, gameId);

        Game game = gameBusiness.restartGame(gameId);

        RmiClient firsPlayerClient = (RmiClient) gameBusiness.getClientOf(game.getFirstPlayer());
        RmiClient secondPlayerClient = (RmiClient) gameBusiness.getClientOf(game.getSecondPlayer());
        notify(() -> {
            try {
                firsPlayerClient.gameServiceNotifier().notifyRestartGame(game);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        notify(() -> {
            try {
                secondPlayerClient.gameServiceNotifier().notifyRestartGame(game);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        return game;
    }

    @Override
    public Game giveUp(Client client) throws RemoteException {
        Logger.log("request - askToRestartGame", client);

        Game game = gameBusiness.giveUp(client);

        if (game.isSecondPlayer(client)) {
            RmiClient rmiClient = (RmiClient) gameBusiness.getClientOf(game.getFirstPlayer());
            notify(() -> {
                try {
                    rmiClient.gameServiceNotifier().notifyGiveUp();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });

        } else if (game.getSecondPlayer() != null) {
            RmiClient rmiClient = (RmiClient) gameBusiness.getClientOf(game.getSecondPlayer());
            notify(() -> {
                try {
                    rmiClient.gameServiceNotifier().notifyGiveUp();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }

        return game;
    }

    private void notify(Runnable runnable) {
        new Thread(runnable).start();
    }
}
