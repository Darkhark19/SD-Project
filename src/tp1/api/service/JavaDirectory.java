package tp1.api.service;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tp1.api.FileInfo;
import tp1.api.User;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.clients.FilesClientFactory;
import tp1.clients.UsersClientFactory;
import tp1.discovery.Discovery;
import tp1.server.rest.FilesServer;

import java.net.URI;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class JavaDirectory implements Directory {

    private static Logger Log = Logger.getLogger(JavaDirectory.class.getName());
    private final ConcurrentMap<String, FileInfo> files = new ConcurrentHashMap<>();
    private static final String DELIMITER = "___";

    public JavaDirectory() {
    }

    @Override
    public synchronized Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
        Result<User> user = UsersClientFactory.getUser(userId, password);
        if (!user.isOK()) {
            return Result.error(user.error());
        }
        if (filename == null || userId == null || password == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        String path = userId + DELIMITER + filename;
        FileInfo f = files.get(path);
        URI[] files = Discovery.getInstance().knownUrisOf(FilesServer.SERVICE, 1);
        String url = null;
        if (f == null) {
            f = new FileInfo();
            f.setFilename(filename);
            f.setOwner(userId);
            int position = (int) Math.round(Math.random() * (files.length - 1));
            //Log.info(String.valueOf(files.length));
            url = files[position].toString();
            f.setFileURL(String.format(url + RestFiles.PATH + "/%s", path));
            Set<String> share = f.getSharedWith();
            if (share == null)
                share = new HashSet<>();
            f.setSharedWith(share);
            this.files.put(path, f);

        } else {
            url = f.getFileURL().split(RestFiles.PATH + "/" + path)[0];
        }
         FilesClientFactory.getClient(URI.create(url)).writeFile(path, data, "");
        return Result.ok(f);
    }

    @Override
    public Result<Void> deleteFile(String filename, String userId, String password) {
        Result<User> user = UsersClientFactory.getUser(userId, password);
        String path = userId + DELIMITER + filename;
        if (!user.isOK()) {
            return Result.error(user.error());
        }
        FileInfo f = this.files.remove(path);
        if (f == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);
        if (filename == null || userId == null || password == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        String url = f.getFileURL().split(RestFiles.PATH + "/" + path)[0];
        FilesClientFactory.getClient(URI.create(url)).deleteFile(path, "");
        return Result.ok();
    }

    @Override
    public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
        String path = userId + DELIMITER + filename;
        FileInfo f = this.files.get(path);
        Result<User> exists = UsersClientFactory.userExists(userIdShare);
        if (!exists.isOK() || f == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);
        Result<User> user = UsersClientFactory.getUser(userId, password);
        if (!user.isOK()) {
            return Result.error(user.error());
        }
        if (filename == null || userId == null || password == null || userIdShare == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        Set<String> share = f.getSharedWith();
        share.add(userIdShare);
        f.setSharedWith(share);
        return Result.ok();
    }

    @Override
    public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
        String path = userId + DELIMITER + filename;
        FileInfo f = this.files.get(path);
        Result<User> exists = UsersClientFactory.userExists(userIdShare);
        if (!exists.isOK() || f == null)
            return Result.error(Result.ErrorCode.NOT_FOUND);

        Result<User> user = UsersClientFactory.getUser(userId, password);
        if (!user.isOK()) {
            return Result.error(user.error());
        }
        if (filename == null || userId == null || password == null || userIdShare == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        Set<String> share = f.getSharedWith();
        share.remove(userIdShare);
        f.setSharedWith(share);
        return Result.ok();
    }

    @Override
    public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
        Log.info("getFile:" + filename + "owner:" + userId + "user:" + accUserId);
        String path = userId + DELIMITER + filename;
        Result<User> user = UsersClientFactory.getUser(accUserId, password);
        FileInfo f = files.get(path);
        if (!user.isOK()) {
            return Result.error(user.error());
        }
        Result<User> exists = UsersClientFactory.userExists(userId);
        if (!exists.isOK() || f == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        if (!hasAccess(f, accUserId)) {
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }
        if (filename == null || userId == null || accUserId == null || password == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        String uri = f.getFileURL();
        throw new WebApplicationException(Response.temporaryRedirect(URI.create(uri)).build());
    }

    private boolean hasAccess(FileInfo f, String accUserId) {
        if (f.getOwner().equalsIgnoreCase(accUserId))
            return true;
        Set<String> user = f.getSharedWith();
        if (user != null) {
            for (String u : user)
                if (u.equalsIgnoreCase(accUserId))
                    return true;
            return false;
        }
        return false;
    }


    @Override
    public Result<List<FileInfo>> lsFile(String userId, String password) {
        List<FileInfo> result = new LinkedList<>();
        Result<User> user = UsersClientFactory.getUser(userId, password);
        if (!user.isOK()) {
            return Result.error(user.error());
        }
        if (userId == null || password == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        for (FileInfo f : files.values()) {
            if (f.getOwner().equalsIgnoreCase(userId))
                result.add(f);
            else if (f.getSharedWith().contains(userId))
                result.add(f);

        }
        return Result.ok(result);
    }

    public Result<Void> deleteClientFiles(String userId){
        for(FileInfo f : files.values()){
            if (f.getOwner().equalsIgnoreCase(userId)){
                String path = userId + DELIMITER + f.getFilename();
                this.files.remove(path);
                String url = f.getFileURL().split(RestFiles.PATH + "/" + path)[0];
                FilesClientFactory.getClient(URI.create(url)).deleteFile(path, "");
            }
        }
        return Result.ok();
    }
}
