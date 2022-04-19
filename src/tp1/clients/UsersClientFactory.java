package tp1.clients;

import tp1.api.User;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;
import tp1.clients.rest.RestUsersClient;
import tp1.clients.soap.SoapUsersClient;
import tp1.discovery.Discovery;
import tp1.server.rest.UsersServer;


public class UsersClientFactory{

    private static Users getClient() {
        var serverURI = Discovery.getInstance().knownUrisOf(UsersServer.SERVICE,1);
        if( serverURI[0].toString().endsWith("rest"))
            return new RestUsersClient( serverURI[0] );
        else
            return new SoapUsersClient( serverURI[0] );
    }

    public static Result<User> getUser(String userId, String password){
        return getClient().getUser(userId,password);
    }

    public static Result<User> userExists(String userId){
        return getClient().userExists(userId);
    }

}
