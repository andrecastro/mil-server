package br.edu.ifce.ppd.tria.server.repositroy;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.model.Game;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

/**
 * Created by andrecoelho on 2/16/16.
 */
public class GameRepository {

    private Map<String, Game> games;

    public GameRepository() {
        games = new HashMap<String, Game>();
    }

    public List<Game> findIdleGames() {
        return games
                .values()
                .parallelStream()
                .filter(Game::isIdle)
                .collect(toCollection(ArrayList::new));
    }

    public Game create(Game game) {
        String randomID = UUID.randomUUID().toString();
        game.setId(randomID);
        games.put(randomID, game);
        return game;
    }

    public Game findById(String id) {
        return games.get(id);
    }

    public Game remove(String id) {
        return games.remove(id);
    }

    public Game findBy(Client client) {
        Predicate<Game> cond = g -> g.isFirstPlayer(client) || g.isSecondPlayer(client);
        return games.values().stream().filter(cond).findFirst().orElse(null);
    }

    public Game update(Game game) {
        games.put(game.getId(), game);
        return game;
    }
}
