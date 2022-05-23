package tp1.clients.soap;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapFiles;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.util.Files;
import tp1.api.service.util.Result;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class SoapFilesClient extends SoapClient implements Files{
    private static final String WSDL = "/files/?wsdl";
    private SoapFiles files;
    public SoapFilesClient(URI u) {
        super(u);
    }

    private synchronized SoapFiles create(){
        QName qname = new QName(SoapFiles.NAMESPACE, SoapFiles.NAME);
        try {
            URL url = URI.create(u + WSDL).toURL();
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(CONNECT_TIMEOUT);
            conn.connect();
            if (files == null) {
                Service service = Service.create(url, qname);
                files = service.getPort(SoapFiles.class);
                setClientTimeouts((BindingProvider) files);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.files;
    }
    @Override
    public Result<Void> writeFile(String fileId, byte[] data, String token) {
        return super.reTry(() -> clt_writeFile(fileId,data,token));
    }


    @Override
    public Result<Void> deleteFile(String fileId, String token) {
        return super.reTry(() -> clt_deleteFile(fileId,token));
    }

    @Override
    public Result<byte[]> getFile(String fileId, String token) {
       return super.reTry(() -> clt_getFile(fileId,token));
    }
    private Result<Void> clt_writeFile(String fileId, byte[] data, String token) {
        try{
            create().writeFile(fileId,data,token);
            return Result.ok();
        } catch (FilesException e) {
           return Result.error(statusToErrorCode(e));
        }
    }

    private Result<Void> clt_deleteFile(String fileId, String token) {
        try{
            create().deleteFile(fileId,token);
            return Result.ok();
        } catch (FilesException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

    private Result<byte[]> clt_getFile(String fileId, String token) {
        try{
            byte[] content = create().getFile(fileId,token);
            return Result.ok(content);
        } catch (FilesException e) {
            return Result.error(statusToErrorCode(e));
        }
    }
}
