package tp1.clients.soap;

import com.sun.xml.ws.client.BindingProviderProperties;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Result;

import java.util.function.Supplier;

public class SoapClient {

    protected static final int READ_TIMEOUT = 10000;
    protected static final int CONNECT_TIMEOUT = 10000;

    protected static final int RETRY_SLEEP = 1000;
    protected static final int MAX_RETRIES = 3;
    public SoapClient(){

    }
    static void setClientTimeouts(BindingProvider port ) {
        port.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
        port.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, READ_TIMEOUT);
    }

    protected <T> T reTry(Supplier<T> func) {
        for (int i = 0; i < MAX_RETRIES; i++)
            try {
                return func.get();
            } catch (WebServiceException x) {
                sleep(RETRY_SLEEP);
            } catch (Exception x) {
                x.printStackTrace();
                break;
            }
        return null;
    }


    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException x) { // nothing to do...
        }
    }
    protected Result.ErrorCode statusToErrorCode(Exception status) {
        return switch (status.getMessage()){
            case "OK", "NO_CONTENT" -> Result.ErrorCode.OK;
            case "CONFLICT" -> Result.ErrorCode.CONFLICT;
            case "FORBIDDEN" -> Result.ErrorCode.FORBIDDEN;
            case "NOT_FOUND" -> Result.ErrorCode.NOT_FOUND;
            case "BAD_REQUEST" -> Result.ErrorCode.BAD_REQUEST;
            case "NOT_IMPLEMENTED" -> Result.ErrorCode.NOT_IMPLEMENTED;
            default -> Result.ErrorCode.INTERNAL_ERROR;
        };
    }

}
