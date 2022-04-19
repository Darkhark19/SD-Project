package tp1.server.rest.resources;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import tp1.api.service.util.Result;


public abstract class Resource {

    protected <T> T response(Result<T> result){
        Response.Status s = getErrorCode(result);
        if(result.isOK() )
            return  result.value();
        else
            throw new WebApplicationException(s);
    }

    private <T> Response.Status getErrorCode(Result<T> result) {
        return switch (result.error()) {
            case BAD_REQUEST -> Response.Status.BAD_REQUEST;
            case CONFLICT -> Response.Status.CONFLICT;
            case NOT_FOUND -> Response.Status.NOT_FOUND;
            case FORBIDDEN -> Response.Status.FORBIDDEN;
            case NOT_IMPLEMENTED -> Response.Status.NOT_IMPLEMENTED;
            case OK ->  Response.Status.OK; //result.value() == null ? Response.Status.NO_CONTENT :
            default -> Response.Status.INTERNAL_SERVER_ERROR;
        };
    }

}
