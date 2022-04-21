package tp1.clients;


import tp1.api.service.util.Files;
import tp1.clients.rest.RestFilesClient;
import tp1.clients.soap.SoapFilesClient;
import tp1.discovery.Discovery;
import tp1.server.rest.FilesServer;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FilesClientFactory {

    private static final int MAX_ENTRIES = 5;
    private static final Map<URI,Integer> serverFiles = new ConcurrentHashMap<>();

    public static Files getClient(URI u) {
        if( u.toString().endsWith("rest"))
            return new RestFilesClient( u );
        else
            return new SoapFilesClient( u );
    }

    public static synchronized URI getNewClient(){
        URI[] files = Discovery.getInstance().knownUrisOf(FilesServer.SERVICE,1);
        if (serverFiles.isEmpty())
            for (int i = 0; i < files.length && i < MAX_ENTRIES; i++) {
                serverFiles.putIfAbsent(files[i],0);
            }
        Map.Entry<URI,Integer> result = null;
        for(Map.Entry<URI,Integer> e: serverFiles.entrySet()){
            if(result == null) {
                result = e;
            }else if(e.getValue() < result.getValue()){
                result = e;
            }
        }
        serverFiles.put(result.getKey(), result.getValue() + 1);
        return result.getKey();
    }

    public static synchronized void deleteFile(String url, String path, String token){
        getClient(URI.create(url)).deleteFile(path, token);
        decreaseFiles(URI.create(url));
    }
    public static synchronized Map<URI,Integer> getServerFiles(){
        return serverFiles;
    }
    public static synchronized void decreaseFiles(URI uri){
        int numFiles = serverFiles.get(uri);
        numFiles--;
        serverFiles.put(uri,numFiles);

    }
}
