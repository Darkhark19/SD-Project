package tp1.server.soap.WebService;

import tp1.api.service.JavaFiles;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapFiles;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;

public class FilesWebService implements SoapFiles {

    final Files files = new JavaFiles();
    public FilesWebService(){
    }
    private static <T> T result(Result<T> result) throws FilesException {
        if (result.isOK())
            return result.value();
        else
            throw new FilesException(result.error().name());
    }
    @Override
    public byte[] getFile(String fileId, String token) throws FilesException {
        var response = files.getFile(fileId, token);
        return result(response);
    }

    @Override
    public void deleteFile(String fileId, String token) throws FilesException {
        var response = files.deleteFile(fileId, token);
        result(response);
    }

    @Override
    public void writeFile(String fileId, byte[] data, String token) throws FilesException {
        var response = files.writeFile(fileId,data, token);
        result(response);
    }
}
