package tp1.api.service;

import tp1.api.FileInfo;
import tp1.api.User;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.clients.FilesClientFactory;
import tp1.clients.UsersClientFactory;


import java.net.URI;
import java.util.*;
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
        String url = null;

        if (f == null) {
            f = new FileInfo();
            f.setFilename(filename);
            f.setOwner(userId);

            url = FilesClientFactory.getNewClient().toString();
            f.setFileURL(String.format(url + RestFiles.PATH + "/%s", path));
            Set<String> share = f.getSharedWith();
            if (share == null)
                share = new HashSet<>();
            f.setSharedWith(share);
            this.files.put(path, f);
        } else {
            url = f.getFileURL().split(RestFiles.PATH + "/" + path)[0];
        }
        FilesClientFactory.writeFile(URI.create(url), path,data,"");
        redistribute();
        return Result.ok(f);
    }

    @Override
    public synchronized Result<Void> deleteFile(String filename, String userId, String password) {
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
        FilesClientFactory.deleteFile(url,path,"");
        redistribute();
        return Result.ok();
    }

    @Override
    public synchronized Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
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
    public synchronized Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
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
        return FilesClientFactory.getFile(uri,false);
    }

    private boolean hasAccess(FileInfo f, String accUserId) {
        if (f.getOwner().equalsIgnoreCase(accUserId))
            return true;
        return f.getSharedWith().contains(accUserId);
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
                FilesClientFactory.deleteFile(url,path,"");
            }
        }
        redistribute();
        return Result.ok();
    }

    private void redistribute() {
        Map<URI, Integer> mp = FilesClientFactory.getServerFiles();
        Map.Entry<URI, Integer> min = null;
        Map.Entry<URI, Integer> max = null;
        for (Map.Entry<URI, Integer> e : mp.entrySet()) {
            if (min == null)
                min = e;
            if (max == null)
                max = e;
            if (e.getValue() < min.getValue())
                min = e;
            if (e.getValue() > max.getValue())
                max = e;
        }
        if (max != null && min != null) {
            int diffFiles = max.getValue() - min.getValue();
            int counter = 0;
            int maxFilesDistribute = diffFiles / 2;
            if (diffFiles > 2) {
                for (Map.Entry<String, FileInfo> f : files.entrySet()) {
                    if (counter < maxFilesDistribute) {
                        FileInfo file = f.getValue();
                        String fileId = f.getKey();
                        String fileURL = file.getFileURL();
                        if (fileURL.contains(max.getKey().toString())) {
                            Result<byte[]> result = FilesClientFactory.getFile(fileURL,true);
                            if (result.isOK()) {
                                String url = fileURL.split(RestFiles.PATH + "/" + fileId)[0];
                                FilesClientFactory.deleteFile(url, fileId, " ");
                                FilesClientFactory.writeFile(URI.create(url), fileId, result.value(), " ");
                                file.setFileURL(String.format( "%s%s/%s", url , RestFiles.PATH , fileId));
                                counter++;
                            }
                        }

                    }
                }
            }
        }
    }
}
