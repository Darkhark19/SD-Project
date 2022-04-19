package tp1.clients;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tp1.api.service.util.Files;
import tp1.clients.rest.RestFilesClient;
import tp1.clients.soap.SoapFilesClient;

import java.net.URI;

public class FilesClientFactory {

    public static Files getClient(URI u) {
        if( u.toString().endsWith("rest"))
            return new RestFilesClient( u );
        else
            return new SoapFilesClient( u );
    }

}
