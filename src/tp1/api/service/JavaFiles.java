package tp1.api.service;

import jakarta.ws.rs.WebApplicationException;
import tp1.api.service.rest.RestFiles;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;

//import java.io.File;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

public class JavaFiles implements Files {
    private static final String DIR_PATH = "data";
    private Path dirPath ;
    public JavaFiles(){
        try {
            Path newPath = Paths.get(DIR_PATH);
            dirPath = java.nio.file.Files.createDirectory(newPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result<Void> writeFile(String fileId, byte[] data, String token) {
        if (token == null) {
            return Result.error(Result.ErrorCode.FORBIDDEN);

        }
        if (fileId == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        try {
            Path file = dirPath.resolve(fileId);
            String path = file.toString();
            java.nio.file.Files.write(Paths.get(path), data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Result.ok();
    }

    @Override
    public Result<Void> deleteFile(String fileId, String token) {

        if (token == null) {

            return Result.error(Result.ErrorCode.FORBIDDEN);
        }
        if (fileId == null){
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }


        try {
            Path file = dirPath.resolve(fileId);
            String path = file.toString();
            boolean exist = java.nio.file.Files.deleteIfExists(Paths.get(path));
            if(!exist){
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }
            return Result.ok();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Result<byte[]> getFile(String fileId, String token) {

        if (token == null) {
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }
        if (fileId == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        Path file = dirPath.resolve(fileId);
        String path = file.toString();
        if(java.nio.file.Files.notExists(Paths.get(path))){
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        try {
            byte[] data = java.nio.file.Files.readAllBytes(Paths.get(path));
            return Result.ok(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

}
