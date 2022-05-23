package tp1.clients;


import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;
import tp1.clients.rest.RestFilesClient;
import tp1.clients.soap.SoapFilesClient;
import tp1.discovery.Discovery;
import tp1.server.rest.DirectoryServer;
import tp1.server.rest.FilesServer;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FilesClientFactory {

    private static final int MAX_ENTRIES = 5;
    private static final Map<URI, Integer> serverFiles = new ConcurrentHashMap<>();

    public static Files getClient(URI u) {
        if (u.toString().contains("rest")) {
            return new RestFilesClient(u);
        } else
            return new SoapFilesClient(u);
    }

    public static URI getNewClient() {
        URI[] files = Discovery.getInstance().knownUrisOf(FilesServer.SERVICE, 1);
        if (serverFiles.isEmpty())
            for (int i = 0; i < files.length && i < MAX_ENTRIES; i++) {
                serverFiles.putIfAbsent(files[i], 0);
            }
        Map.Entry<URI, Integer> result = null;
        for (Map.Entry<URI, Integer> e : serverFiles.entrySet()) {
            if (result == null) {
                result = e;
            } else if (e.getValue() < result.getValue()) {
                result = e;
            }
        }
        return result.getKey();
    }

    public static Result<byte[]> getFile(String url, boolean delete) {
        var serverURI = Discovery.getInstance().knownUrisOf(DirectoryServer.SERVICE, 1);
        String[] urlPath = url.split(RestFiles.PATH + "/");
        if (url.contains("rest")) {
            if (serverURI[0].toString().contains("rest") && !delete) {
                throw new WebApplicationException(Response.temporaryRedirect(URI.create(url)).build());
            } else {
                return new RestFilesClient(URI.create(urlPath[0])).getFile(urlPath[1], "");
            }
        }
        return new SoapFilesClient(URI.create(urlPath[0])).getFile(urlPath[1], "");
    }


    public static synchronized void deleteFile(String url, String path, String token) {

        getClient(URI.create(url)).deleteFile(path, token);
        decreaseFiles(URI.create(url));
    }

    public static Map<URI, Integer> getServerFiles() {
        return serverFiles;
    }

    private static void decreaseFiles(URI uri) {
        int numFiles = serverFiles.get(uri);
        numFiles--;
        serverFiles.put(uri, numFiles);

    }

    public static synchronized void writeFile(URI u, String fileID, byte[] content, String token) {
        getClient(u).writeFile(fileID, content, token);
        int result = serverFiles.get(u);
        serverFiles.put(u, result + 1);

    }
}
