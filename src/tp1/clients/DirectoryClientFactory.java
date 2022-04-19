package tp1.clients;

import tp1.api.User;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;
import tp1.clients.rest.RestDirectoryClient;
import tp1.clients.rest.RestUsersClient;
import tp1.clients.soap.SoapDirectoryClient;
import tp1.clients.soap.SoapUsersClient;
import tp1.discovery.Discovery;
import tp1.server.rest.DirectoryServer;
import tp1.server.rest.UsersServer;

public class DirectoryClientFactory {
    public static Directory getClient() {
        var serverURI = Discovery.getInstance().knownUrisOf(DirectoryServer.SERVICE,1);// use discovery to find a uri of the Users service;
        if( serverURI[0].toString().endsWith("rest"))
            return new RestDirectoryClient( serverURI[0] );
        else
            return new SoapDirectoryClient( serverURI[0] );
    }

    public static Result<Void> deleteUserFiles(String userId){
        return getClient().deleteClientFiles(userId);
    }
}
