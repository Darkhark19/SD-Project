package tp1.server.rest.resources;

import jakarta.inject.Singleton;
import tp1.api.FileInfo;
import tp1.api.service.JavaDirectory;
import tp1.api.service.rest.RestDirectory;

import java.util.logging.Logger;
import java.util.List;

@Singleton
public class DirectoryResource extends Resource implements RestDirectory {
    private static Logger Log = Logger.getLogger(DirectoryResource.class.getName());

    private final JavaDirectory directory = new JavaDirectory();
    public DirectoryResource(){
    }
    @Override
    public synchronized FileInfo writeFile(String filename, byte[] data, String userId, String password) {
       var result =  directory.writeFile(filename,data,userId,password);
       return super.response(result);
    }

    @Override
    public synchronized void deleteFile(String filename, String userId, String password) {
        var result = directory.deleteFile(filename,userId, password);
        super.response(result);
    }

    @Override
    public synchronized void shareFile(String filename, String userId, String userIdShare, String password) {
        var result = directory.shareFile(filename,userId,userIdShare, password);
        super.response(result);
    }

    @Override
    public synchronized void unshareFile(String filename, String userId, String userIdShare, String password) {
        var result = directory.unshareFile(filename,userId,userIdShare, password);
        super.response(result);
    }

    @Override
    public synchronized byte[] getFile(String filename, String userId, String accUserId, String password) {
        var result = directory.getFile(filename,userId,accUserId, password);
        return super.response(result);
    }

    @Override
    public synchronized List<FileInfo> lsFile(String userId, String password) {
        var result = directory.lsFile(userId,password);
        return super.response(result);
    }

    @Override
    public synchronized void deleteClientFiles(String userId) {
        var result = directory.deleteClientFiles(userId);
        super.response(result);
    }


} 
