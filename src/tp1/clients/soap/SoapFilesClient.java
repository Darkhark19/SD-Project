package tp1.clients.soap;

import tp1.api.service.util.Files;
import tp1.api.service.util.Result;

import java.net.URI;

public class SoapFilesClient implements Files{
    private static final String WSDL = "files/?wsdl";
    public SoapFilesClient(URI u) {
    }

    @Override
    public Result<Void> writeFile(String fileId, byte[] data, String token) {
        return null;
    }

    @Override
    public Result<Void> deleteFile(String fileId, String token) {
        return null;
    }

    @Override
    public Result<byte[]> getFile(String fileId, String token) {
        return null;
    }
}
