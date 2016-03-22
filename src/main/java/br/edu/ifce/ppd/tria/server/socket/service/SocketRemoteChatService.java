package br.edu.ifce.ppd.tria.server.socket.service;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.core.protocol.Action;
import br.edu.ifce.ppd.tria.core.protocol.helper.ActionBuilder;
import br.edu.ifce.ppd.tria.core.service.ChatService;
import br.edu.ifce.ppd.tria.server.business.GameBusiness;
import br.edu.ifce.ppd.tria.server.socket.model.SocketClient;

import static br.edu.ifce.ppd.tria.core.protocol.helper.ActionBuilder.anAction;

/**
 * Created by andrecoelho on 2/16/16.
 */
public class SocketRemoteChatService implements ChatService {

    private GameBusiness gameBusiness;

    public SocketRemoteChatService(GameBusiness gameBusiness) {
        this.gameBusiness = gameBusiness;
    }

    @Override
    public Action sendMessage(Client client, String message) {
        Game game = gameBusiness.getGameOf(client);

        if (game == null) {
            return null; // there is nothing to do
        }

        SocketClient socketClient;


        ActionBuilder notifySendMessage = anAction().to("chat-service/notify-send-message")
                .withParamValue("message", message);

        if (game.isFirstPlayer(client)) {
            notifySendMessage.withParamValue("player-name", game.getFirstPlayer().getName());
            socketClient = (SocketClient) gameBusiness.getFromRepository(game.getSecondPlayer().getClient());
        } else {
            notifySendMessage.withParamValue("player-name", game.getSecondPlayer().getName());
            socketClient = (SocketClient) gameBusiness.getFromRepository(game.getFirstPlayer().getClient());
        }

        socketClient.send(notifySendMessage.build());

        return anAction()
                .to("chat-service/send-message")
                .withParamValue("message", message)
                .build();
    }
}