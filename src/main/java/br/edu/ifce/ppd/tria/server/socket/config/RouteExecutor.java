package br.edu.ifce.ppd.tria.server.socket.config;

import br.edu.ifce.ppd.tria.core.model.Client;
import br.edu.ifce.ppd.tria.core.protocol.Action;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by andrecoelho on 2/26/16.
 */
public interface RouteExecutor {

    Action execute(Client client, HashMap<String, Serializable> body);

}
