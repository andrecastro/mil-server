package br.edu.ifce.ppd.tria.server.business;

import br.edu.ifce.ppd.tria.core.model.*;
import br.edu.ifce.ppd.tria.core.service.RemoteGameService;
import br.edu.ifce.ppd.tria.server.repositroy.ClientRepository;
import br.edu.ifce.ppd.tria.server.repositroy.GameRepository;

import java.util.List;
import java.util.UUID;

import static br.edu.ifce.ppd.tria.core.model.PlayerSelection.FIRST_PLAYER;
import static br.edu.ifce.ppd.tria.core.model.PlayerSelection.SECOND_PLAYER;


/**
 * Created by andrecoelho on 2/16/16.
 */
public class GameBusiness implements RemoteGameService {

    private GameRepository games;
    private ClientRepository clients;

    public GameBusiness(GameRepository games, ClientRepository clients) {
        this.games = games;
        this.clients = clients;
    }

    public List<Game> idleGames() {
        return games.findIdleGames();
    }

    public Game createGame(Client client, String alias, String firstPlayerName) {
        Game game = new Game(alias, createPlayer(firstPlayerName, FIRST_PLAYER, client));
        return games.create(game);
    }

    public Game enterGame(Client client, String gameId, String secondPlayer) {
        Game game = games.findById(gameId);

        if (game == null) {
            return null; // do something
        }

        game.setSecondPlayer(createPlayer(secondPlayer, SECOND_PLAYER, client));
        game.setStatus(GameStatus.PLAYING);

        return games.update(game);
    }

    private Player createPlayer(String name, PlayerSelection selection, Client client) {
        return new Player(new Client(client.getId()), name, selection);
    }

    public Client getClientOf(Player player) {
        return clients.findById(player.getClient().getId());
    }

}
