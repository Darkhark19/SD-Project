package tp1.clients;

import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.clients.rest.RestDirectoryClient;
import tp1.clients.soap.SoapDirectoryClient;
import tp1.discovery.Discovery;
import tp1.server.rest.DirectoryServer;

public class DirectoryClientFactory {


    public static Directory getClient() {
        var serverURI = Discovery.getInstance().knownUrisOf(DirectoryServer.SERVICE, 1);// use discovery to find a uri of the Users service;
        if (serverURI[0].toString().endsWith("rest"))
            return new RestDirectoryClient(serverURI[0]);
        else
            return new SoapDirectoryClient(serverURI[0]);
    }

    public static Result<Void> deleteUserFiles(String userId) {
        var serverURI = Discovery.getInstance().knownUrisOf(DirectoryServer.SERVICE);
        if (serverURI != null) {
            if (serverURI[0].toString().endsWith("rest"))
                return new RestDirectoryClient(serverURI[0]).deleteClientFiles(userId);
            else
                return new SoapDirectoryClient(serverURI[0]).deleteClientFiles(userId);
        }
        return Result.ok();
    }
}
