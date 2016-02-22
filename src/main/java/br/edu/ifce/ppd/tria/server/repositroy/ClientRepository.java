package br.edu.ifce.ppd.tria.server.repositroy;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.protocol.Connection;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andrecoelho on 2/18/16.
 */
public class ClientRepository {

    private ConcurrentHashMap<String, Client> clients;

    public ClientRepository() {
        clients = new ConcurrentHashMap<>();
    }

    public Client save(Client client) {
        clients.put(client.getId(), client);
        return client;
    }

    public Client remove(Client client) {
        return clients.remove(client.getId());
    }

    public Client findById(String clientId) {
        return clients.get(clientId);
    }
}
