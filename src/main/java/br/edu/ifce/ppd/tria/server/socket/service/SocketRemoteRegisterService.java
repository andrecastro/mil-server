package br.edu.ifce.ppd.tria.server.socket.service;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.core.protocol.helper.ActionBuilder;
import br.edu.ifce.ppd.tria.core.service.RegisterService;
import br.edu.ifce.ppd.tria.server.business.RegistrationBusiness;
import br.edu.ifce.ppd.tria.server.socket.SocketConnection;
import br.edu.ifce.ppd.tria.server.socket.model.SocketClient;

import java.rmi.RemoteException;
import java.util.UUID;

import static br.edu.ifce.ppd.tria.core.protocol.helper.ActionBuilder.anAction;

/**
 * Created by andrecoelho on 2/19/16.
 */
public class SocketRemoteRegisterService implements RegisterService{

    private RegistrationBusiness registrationBusiness;

    public SocketRemoteRegisterService(RegistrationBusiness registrationBusiness) {
        this.registrationBusiness = registrationBusiness;
    }

    @Override
    public Client createClient() {
        return new Client(UUID.randomUUID().toString());
    }

    @Override
    public Client register(Client client) {
        return registrationBusiness.register(client);
    }

    @Override
    public void deregister(Client client) {
        Game closedGame = registrationBusiness.deregister(client);

        if (closedGame == null)
            return; // do nothing

        if (closedGame.isFirstPlayer(client) && closedGame.getSecondPlayer() == null)
            return; // do nothing

        SocketClient opponentClient;

        if (closedGame.isFirstPlayer(client)) {
            Client secondPlayerClient = closedGame.getSecondPlayer().getClient();
            opponentClient = (SocketClient) registrationBusiness.getClientFromRepository(secondPlayerClient);
        } else {
            Client firstPlayerClient = closedGame.getFirstPlayer().getClient();
            opponentClient = (SocketClient) registrationBusiness.getClientFromRepository(firstPlayerClient);
        }

        Action notifyClose = anAction()
                .to("register-service/notify-close-game").build();

        opponentClient.send(notifyClose);
    }

}
