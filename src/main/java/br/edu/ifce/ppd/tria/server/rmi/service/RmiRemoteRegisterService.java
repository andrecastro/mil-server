package br.edu.ifce.ppd.tria.server.rmi.service;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.core.service.ChatServiceNotifier;
import br.edu.ifce.ppd.tria.core.service.GameServiceNotifier;
import br.edu.ifce.ppd.tria.core.service.RegisterService;
import br.edu.ifce.ppd.tria.server.business.RegistrationBusiness;
import br.edu.ifce.ppd.tria.server.rmi.helper.Logger;
import br.edu.ifce.ppd.tria.server.rmi.model.RmiClient;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

/**
 * Created by andrecoelho on 3/15/16.
 */
public class RmiRemoteRegisterService extends UnicastRemoteObject implements RegisterService {

    private RegistrationBusiness registrationBusiness;

    public RmiRemoteRegisterService(RegistrationBusiness registrationBusiness) throws RemoteException {
        this.registrationBusiness = registrationBusiness;
    }

    @Override
    public Client createClient() throws RemoteException {
        Logger.log("request - createClient");

        return new Client(UUID.randomUUID().toString());
    }

    @Override
    public Client register(Client client) throws RemoteException {
        Logger.log("request - register", client);

        try {
            lookupClientForClientNotifiers(client);
        } catch (NotBoundException | MalformedURLException | RemoteException | ServerNotActiveException e) {
            throw new RemoteException("Error registering notifier", e);
        }

        return client;
    }

    @Override
    public void deregister(Client client) throws RemoteException {
        Logger.log("request - deregister", client);

        Game closedGame = registrationBusiness.deregister(client);

        if (closedGame == null)
            return; // do nothing

        if (closedGame.isFirstPlayer(client) && closedGame.getSecondPlayer() == null)
            return; // do nothing

        RmiClient opponentClient;

        if (closedGame.isFirstPlayer(client)) {
            Client secondPlayerClient = closedGame.getSecondPlayer().getClient();
            opponentClient = (RmiClient) registrationBusiness.getClientFromRepository(secondPlayerClient);
        } else {
            Client firstPlayerClient = closedGame.getFirstPlayer().getClient();
            opponentClient = (RmiClient) registrationBusiness.getClientFromRepository(firstPlayerClient);
        }

        new Thread(() -> {
            try {
                opponentClient.gameServiceNotifier().notifyCloseGame();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void lookupClientForClientNotifiers(Client client)
            throws NotBoundException, MalformedURLException, RemoteException, ServerNotActiveException {

        String notifyGameServiceName = "GameServiceNotifier" + client.getId();
        String notifyChatServiceName = "ChatServiceNotifier" + client.getId();

        Registry clientRegistry = LocateRegistry.getRegistry(client.getHost(), client.getPort());

        GameServiceNotifier gameServiceNotifier = (GameServiceNotifier) clientRegistry.lookup(notifyGameServiceName);
        ChatServiceNotifier chatServiceNotifier = (ChatServiceNotifier) clientRegistry.lookup(notifyChatServiceName);

        registrationBusiness.register(new RmiClient(client, gameServiceNotifier, chatServiceNotifier));
    }
}
