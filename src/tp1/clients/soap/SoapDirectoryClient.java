package tp1.clients.soap;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import tp1.api.FileInfo;
import tp1.api.service.soap.DirectoryException;
import tp1.api.service.soap.SoapDirectory;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class SoapDirectoryClient extends SoapClient implements Directory {

    private static final String WSDL = "/directory/?wsdl";
    private SoapDirectory directory ;
    public SoapDirectoryClient(URI uri) {
        super(uri);

    }

    private synchronized SoapDirectory create() {
        QName qname = new QName(SoapDirectory.NAMESPACE, SoapDirectory.NAME);
        Service service = null;
        try {
            URL url = URI.create(u + WSDL).toURL();
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(CONNECT_TIMEOUT);
            conn.connect();
            if (directory == null) {
                service = Service.create(url, qname);
                directory = service.getPort(SoapDirectory.class);
                setClientTimeouts((BindingProvider) directory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this.directory;
    }
    @Override
    public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
        return super.reTry( () -> clt_writeFile(filename,data,userId,password));
    }

    @Override
    public Result<Void> deleteFile(String filename, String userId, String password) {
        return super.reTry( () -> clt_deleteFile(filename,userId,password));
    }



    @Override
    public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
        return super.reTry( () -> clt_shareFile(filename,userId,userIdShare,password));
    }

    @Override
    public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
        return super.reTry( () -> clt_unshareFile(filename,userId,userIdShare,password));
    }



    @Override
    public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
        return super.reTry( () -> clt_getFile(filename,userId,accUserId,password));
    }



    @Override
    public Result<List<FileInfo>> lsFile(String userId, String password) {
        return super.reTry( () -> clt_lsFile(userId,password));
    }



    @Override
    public Result<Void> deleteClientFiles(String userId) {
        return super.reTry( () -> clt_deleteClientFiles(userId));
    }

    private Result<Void>  clt_deleteClientFiles(String userId) {
        try{
            create().deleteClientFiles(userId);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

    private Result<FileInfo> clt_writeFile(String filename, byte[] data, String userId, String password) {
        try {
            FileInfo f = create().writeFile(filename,data,userId,password);
            return Result.ok(f);
        } catch (DirectoryException e) {
            return Result.error(statusToErrorCode(e));
        }
    }
    private Result<Void> clt_deleteFile(String filename, String userId, String password) {
        try {
            create().deleteFile(filename,userId,password);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

    private Result<Void> clt_shareFile(String filename, String userId, String userIdShare, String password) {
        try {
            create().shareFile(filename,userId,userIdShare,password);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(statusToErrorCode(e));
        }
    }
    private Result<Void> clt_unshareFile(String filename, String userId, String userIdShare, String password) {
        try {
            create().unshareFile(filename,userId,userIdShare,password);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(statusToErrorCode(e));
        }
    }
    private Result<byte[]> clt_getFile(String filename, String userId, String accUserId, String password) {
        try {
            byte[] content = create().getFile(filename,userId,accUserId,password);
            return Result.ok(content);
        } catch (DirectoryException e) {
            return Result.error(statusToErrorCode(e));
        }
    }
    private Result<List<FileInfo>> clt_lsFile(String userId, String password) {
        try {
            List<FileInfo> d = create().lsFile(userId,password);
            return Result.ok(d);
        } catch (DirectoryException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

}
