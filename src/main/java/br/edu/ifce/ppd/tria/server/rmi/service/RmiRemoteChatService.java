package br.edu.ifce.ppd.tria.server.rmi.service;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.core.service.ChatService;
import br.edu.ifce.ppd.tria.server.business.GameBusiness;
import br.edu.ifce.ppd.tria.server.rmi.helper.Logger;
import br.edu.ifce.ppd.tria.server.rmi.model.RmiClient;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by andrecoelho on 3/15/16.
 */
public class RmiRemoteChatService extends UnicastRemoteObject implements ChatService {

    private GameBusiness gameBusiness;

    public RmiRemoteChatService(GameBusiness gameBusiness) throws RemoteException {
        this.gameBusiness = gameBusiness;
    }

    @Override
    public String sendMessage(Client client, String message) throws RemoteException {
        Logger.log("request - sendMessage:", client, message);

        Game game = gameBusiness.getGameOf(client);

        if (game == null) {
            return null; // there is nothing to do
        }

        if (game.isFirstPlayer(client)) {
            RmiClient rmiClient = (RmiClient) gameBusiness.getFromRepository(game.getSecondPlayer().getClient());
            sendMessage(rmiClient, game.getFirstPlayer().getName(), message);
        } else {
            RmiClient rmiClient = (RmiClient) gameBusiness.getFromRepository(game.getFirstPlayer().getClient());
            sendMessage(rmiClient, game.getSecondPlayer().getName(), message);
        }

        return message;
    }

    private void sendMessage(RmiClient rmiClient, String playerName, String message) {
        new Thread(() -> {
            try {
                rmiClient.chatServiceNotifier().notifySendMessage(playerName, message);
                Logger.log("response - sendMessage:", playerName, message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
