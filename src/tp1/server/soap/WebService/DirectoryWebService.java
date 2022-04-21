package tp1.server.soap.WebService;

import tp1.api.FileInfo;
import tp1.api.service.JavaDirectory;
import tp1.api.service.soap.DirectoryException;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapDirectory;
import tp1.api.service.util.Result;

import java.util.List;

public class DirectoryWebService implements SoapDirectory {

    private final JavaDirectory directory = new JavaDirectory();
    public DirectoryWebService(){

    }
    private static <T> T result(Result<T> result) throws DirectoryException {
        if (result.isOK())
            return result.value();
        else
            throw new DirectoryException(result.error().name());
    }
    @Override
    public FileInfo writeFile(String filename, byte[] data, String userId, String password) throws DirectoryException {
        var response = directory.writeFile(filename,data, userId,password);
        return result(response);
    }

    @Override
    public void deleteFile(String filename, String userId, String password) throws DirectoryException {
        var response = directory.deleteFile(filename, userId,password);
        result(response);
    }

    @Override
    public void shareFile(String filename, String userId, String userIdShare, String password) throws DirectoryException {
        var response = directory.shareFile(filename,userId, userIdShare,password);
        result(response);
    }

    @Override
    public void unshareFile(String filename, String userId, String userIdShare, String password) throws DirectoryException {
        var response = directory.unshareFile(filename,userId, userIdShare,password);
        result(response);
    }

    @Override
    public byte[] getFile(String filename, String userId, String accUserId, String password) throws DirectoryException {
        var response = directory.getFile(filename,userId, accUserId,password);
        return result(response);
    }

    @Override
    public List<FileInfo> lsFile(String userId, String password) throws DirectoryException {
        var response = directory.lsFile(userId ,password);
        return result(response);
    }
}
