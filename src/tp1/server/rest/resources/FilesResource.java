package tp1.server.rest.resources;

import jakarta.inject.Singleton;

import tp1.api.service.JavaFiles;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;

import java.util.logging.Logger;

@Singleton
public class FilesResource extends Resource implements RestFiles {

    private static Logger Log = Logger.getLogger(FilesResource.class.getName());
    private final Files impl = new JavaFiles();

    public FilesResource() {
    }


    @Override
    public void writeFile(String fileId, byte[] data, String token) {
        Log.info("writeFile: " + fileId);
        Result<Void> result = null;
        result = impl.writeFile(fileId, data, token);
        super.response(result);

    }

    public void deleteFile(String fileId, String token) {
        Log.info("delete File:" + fileId);
        var result = impl.deleteFile(fileId, token);
        super.response(result);
    }

    @Override
    public byte[] getFile(String fileId, String token) {
        Log.info("Get File:" + fileId);
        var result = impl.getFile(fileId, token);
        return super.response(result);
    }
}
