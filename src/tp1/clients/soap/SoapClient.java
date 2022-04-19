package tp1.clients.soap;

import jakarta.ws.rs.core.Response;
import tp1.api.service.util.Result;

public class SoapClient {


    protected Result.ErrorCode statusToErrorCode(Response.Status status) {
        return switch (status){
            case OK, NO_CONTENT -> Result.ErrorCode.OK;
            case CONFLICT -> Result.ErrorCode.CONFLICT;
            case FORBIDDEN -> Result.ErrorCode.FORBIDDEN;
            case NOT_FOUND -> Result.ErrorCode.NOT_FOUND;
            case BAD_REQUEST -> Result.ErrorCode.BAD_REQUEST;
            case NOT_IMPLEMENTED -> Result.ErrorCode.NOT_IMPLEMENTED;
            default -> Result.ErrorCode.INTERNAL_ERROR;
        };
    }
}
