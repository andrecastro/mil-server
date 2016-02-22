package br.edu.ifce.ppd.tria.server.socket.service;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.core.protocol.helper.ActionBuilder;
import br.edu.ifce.ppd.tria.core.service.ChatService;
import br.edu.ifce.ppd.tria.server.repositroy.ClientRepository;
import br.edu.ifce.ppd.tria.server.repositroy.GameRepository;
import br.edu.ifce.ppd.tria.server.socket.model.SocketClient;

import java.io.Serializable;
import java.util.HashMap;

import static br.edu.ifce.ppd.tria.core.protocol.helper.ActionBuilder.anAction;

/**
 * Created by andrecoelho on 2/16/16.
 */
public class SocketChatService implements ChatService {

    private GameRepository games;
    private ClientRepository clients;

    public SocketChatService(GameRepository games, ClientRepository clients) {
        this.games = games;
        this.clients = clients;
    }

    @Override
    public Action sendMessage(Client client, String message) {
        Game game = games.findBy(client);

        if (game == null) {
            // there is nothing to do
            return null;
        }

        SocketClient socketClient;


        ActionBuilder notifySendMessage = anAction().to("chat-service/notify-send-message")
                .withParamValue("message", message);

        if (game.isFirstPlayer(client)) {
            notifySendMessage.withParamValue("player-name", game.getFirstPlayer().getName());
            socketClient = (SocketClient) clients.findById(game.getSecondPlayer().getClient().getId());
        } else {
            notifySendMessage.withParamValue("player-name", game.getSecondPlayer().getName());
            socketClient = (SocketClient) clients.findById(game.getFirstPlayer().getClient().getId());
        }

        socketClient.getConnection().send(notifySendMessage.build());

        return anAction()
                .to("chat-service/send-message")
                .withParamValue("message", message)
                .build();
    }
}