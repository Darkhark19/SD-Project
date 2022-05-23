package tp1.clients;

import tp1.api.User;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;
import tp1.clients.rest.RestUsersClient;
import tp1.clients.soap.SoapUsersClient;
import tp1.discovery.Discovery;
import tp1.server.rest.UsersServer;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class UsersClientFactory {

    private static final ConcurrentMap<URI,Users> users = new ConcurrentHashMap<>();
    private static Users getClient() {
        var serverURI = Discovery.getInstance().knownUrisOf(UsersServer.SERVICE, 1);
        if(serverURI[0] != null) {
           Users u = users.get(serverURI[0]);
           if (u == null) {
               if (serverURI[0].toString().endsWith("rest"))
                   u = new RestUsersClient(serverURI[0]);
               else
                   u = new SoapUsersClient(serverURI[0]);
               users.put(serverURI[0], u);
           }
           return u;
       }
        return null;
    }

    public static Result<User> getUser(String userId, String password) {
        return getClient().getUser(userId, password);
    }

    public static Result<User> userExists(String userId) {
        return getClient().userExists(userId);
    }

}
