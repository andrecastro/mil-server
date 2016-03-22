package br.edu.ifce.ppd.tria.server.business;

import br.edu.ifce.ppd.tria.core.model.*;
import br.edu.ifce.ppd.tria.server.repositroy.ClientRepository;
import br.edu.ifce.ppd.tria.server.repositroy.GameRepository;

import java.util.List;
import java.util.function.Predicate;

import static br.edu.ifce.ppd.tria.core.model.GameStatus.PLACING_OF_PIECE;
import static br.edu.ifce.ppd.tria.core.model.GameStatus.PLAYING;
import static br.edu.ifce.ppd.tria.core.model.GameStatus.REMOVING_PIECE;
import static br.edu.ifce.ppd.tria.core.model.PlayerSelection.FIRST_PLAYER;
import static br.edu.ifce.ppd.tria.core.model.PlayerSelection.SECOND_PLAYER;
import static br.edu.ifce.ppd.tria.core.model.SpotOccupiedBy.NO_ONE;
import static br.edu.ifce.ppd.tria.core.model.SpotOccupiedBy.PLAYER_ONE;
import static br.edu.ifce.ppd.tria.core.model.SpotOccupiedBy.PLAYER_TWO;
import static br.edu.ifce.ppd.tria.server.business.builder.BoardBuilder.buildNewBoard;


/**
 * Created by andrecoelho on 2/16/16.
 */
public class GameBusiness {

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
        game.setBoard(buildNewBoard());
        return games.create(game);
    }

    public Game enterGame(Client client, String gameId, String secondPlayer) {
        Game game = games.findById(gameId);

        if (game == null) {
            return null; // do something
        }

        game.setSecondPlayer(createPlayer(secondPlayer, SECOND_PLAYER, client));
        game.setStatus(PLACING_OF_PIECE); // first phase of the game is to place the pieces in the board

        return games.update(game);
    }

    public Game putPieceInSpot(Client client, String gameId, Integer selectedSpotId) {
        Game game = games.findById(gameId);
        Spot spot = game.getBoard().get(selectedSpotId);

        if (game.isFirstPlayer(client)) {
            spot.occupiedBy(PLAYER_ONE);
            game.getFirstPlayer().increaseNumberOfPiecesPlaced();
        } else {
            spot.occupiedBy(PLAYER_TWO);
            game.getSecondPlayer().increaseNumberOfPiecesPlaced();
        }

        game.setSwitchTurn(true);
        game.setCanRemovePiece(false);

        if (hasPlacedAllPieces(game)) {
            game.setStatus(PLAYING);
        }

        if (hasCompletedMil(client, game, selectedSpotId)) {
            game.setSwitchTurn(false);
            game.setCanRemovePiece(true);
            game.setStatus(REMOVING_PIECE);
        }

        return games.update(game);
    }

    public Game removePiece(Client client, String gameId, Integer selectedSpotId) {
        Game game = games.findById(gameId);
        Spot spot = game.getBoard().get(selectedSpotId);
        spot.occupiedBy(NO_ONE);

        if (game.isFirstPlayer(client)) {
            game.getSecondPlayer().decreaseNumberOfPieces();
        } else {
            game.getFirstPlayer().decreaseNumberOfPieces();
        }

        game.setStatus(PLAYING);

        if (!hasPlacedAllPieces(game)) {
            game.setStatus(PLACING_OF_PIECE);
        }

        return games.update(game);
    }

    public Game movePiece(String gameId, Client client, Integer fromSpotId, Integer toSpotId) {
        Game game = games.findById(gameId);

        Spot spotFrom = game.getBoard().get(fromSpotId);
        Spot spotTo = game.getBoard().get(toSpotId);

        spotTo.occupiedBy(spotFrom.getOccupiedBy());
        spotFrom.occupiedBy(NO_ONE);

        game.setSwitchTurn(true);
        game.setCanRemovePiece(false);

        if (hasPlacedAllPieces(game)) {
            game.setStatus(PLAYING);
        }

        if (hasCompletedMil(client, game, toSpotId)) {
            game.setSwitchTurn(false);
            game.setCanRemovePiece(true);
            game.setStatus(REMOVING_PIECE);
        }

        return games.update(game);
    }

    public Game giveUp(Client client) {
        Game game = games.findBy(client);
        games.remove(game.getId());
        return game;
    }

    public Game restartGame(String gameId) {
        Game game = games.findById(gameId);

        game.setStatus(PLACING_OF_PIECE);
        game.setBoard(buildNewBoard());
        game.getFirstPlayer().resetPieces();
        game.getSecondPlayer().resetPieces();

        return games.update(game);
    }

    public boolean hasCompletedMil(Client client, Game game, Integer spotId) {
        Spot spot = game.getBoard().get(spotId);

        if (game.isFirstPlayer(client)) {
            return spot.getMilsBelongsTo().stream().anyMatch(m-> m.isCompletelyOccupiedBy(FIRST_PLAYER));
        }

        return spot.getMilsBelongsTo().stream().anyMatch(m -> m.isCompletelyOccupiedBy(SECOND_PLAYER));
    }

    public Client getClientOf(Player player) {
        return clients.findById(player.getClient().getId());
    }

    public Client getFromRepository(Client client) {
        return clients.findById(client.getId());
    }

    public boolean hasPlacedAllPieces(Game game) {
        return game.getFirstPlayer().getNumberOfPiecesPlaced().equals(9)
                && game.getSecondPlayer().getNumberOfPiecesPlaced().equals(9);
    }

    public boolean isGameOver(Client client, Game game) {
        boolean opponentHasTwoPieces;
//        boolean opponentIsStuck = false;
        // TODO implementation to see if the opponent is stuck
        if (game.isFirstPlayer(client)) {
            opponentHasTwoPieces = game.getSecondPlayer().getNumberOfPieces().equals(2);
//            opponentIsStuck = game.getBoard()
//                    .values().stream().filter(s -> s.isOccupiedByPlayerTwo()).allMatch(isStuck);
        } else {
            opponentHasTwoPieces = game.getFirstPlayer().getNumberOfPieces().equals(2);
//            opponentIsStuck = game.getBoard()
//                    .values().stream().filter(s->s.isOccupiedByPlayerOne()).allMatch(isStuck);
        }

        return game.getStatus().equals(PLAYING) && (opponentHasTwoPieces);
    }

    public void finishGame(Game game) {
        games.remove(game.getId());
    }

    public Game getGame(String gameId) {
        return games.findById(gameId);
    }

    public Game getGameOf(Client client) {
        return games.findBy(client);
    }

    private Player createPlayer(String name, PlayerSelection selection, Client client) {
        return new Player(new Client(client.getId()), name, selection);
    }
}
