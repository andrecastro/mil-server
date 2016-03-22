package br.edu.ifce.ppd.tria.server.rmi.model;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.service.ChatServiceNotifier;
import br.edu.ifce.ppd.tria.core.service.GameServiceNotifier;

/**
 * Created by andrecoelho on 3/15/16.
 */
public class RmiClient extends Client {

    private GameServiceNotifier gameServiceNotifier;
    private ChatServiceNotifier chatServiceNotifier;

    public RmiClient(Client client, GameServiceNotifier gameServiceNotifier, ChatServiceNotifier chatServiceNotifier) {
        super(client.getId());
        this.gameServiceNotifier = gameServiceNotifier;
        this.chatServiceNotifier = chatServiceNotifier;
    }

    public ChatServiceNotifier chatServiceNotifier() {
        return chatServiceNotifier;
    }

    public GameServiceNotifier gameServiceNotifier() {
        return gameServiceNotifier;
    }
}
