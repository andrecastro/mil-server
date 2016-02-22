package br.edu.ifce.ppd.tria.server.business;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;
import br.edu.ifce.ppd.tria.server.repositroy.ClientRepository;
import br.edu.ifce.ppd.tria.server.repositroy.GameRepository;


/**
 * Created by andrecoelho on 2/19/16.
 */
public class RegistrationBusiness {

    private ClientRepository clients;
    private GameRepository games;

    public RegistrationBusiness(ClientRepository clients, GameRepository games) {
        this.clients = clients;
        this.games = games;
    }

    public Client register(Client client) {
        return clients.save(client);
    }

    public Game deregister(Client client) {
        Game game = games.findBy(client);

        // there is no game associated to this client
        // just remove the client
        if (game == null) {
            clients.remove(client);
            return null;
        }

        if (game.isFirstPlayer(client)) {
            clients.remove(client);
        } else {
            clients.remove(game.getSecondPlayer().getClient());
        }

        return games.remove(game.getId());
    }


}
