package br.edu.ifce.ppd.tria.server.rmi.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrecoelho on 3/17/16.
 */
public class Logger {

    public static void  log(String message, Object... params) {
        Map<String, Object> map = new HashMap<>();

        for (int i = 0; i < params.length; i++) {
            map.put("Parameter[" + i + "]", params[i]);
        }

        System.out.println(message + " " + map);
    }
}
