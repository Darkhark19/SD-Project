package tp1.clients.rest;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import tp1.api.service.util.Result;

import java.net.URI;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class RestClient {
	private static Logger Log = Logger.getLogger(RestClient.class.getName());

	protected static final int READ_TIMEOUT = 10000;
	protected static final int CONNECT_TIMEOUT = 10000;

	protected static final int RETRY_SLEEP = 1000;
	protected static final int MAX_RETRIES = 3;

	protected final URI serverURI;
	protected final Client client;
	protected final ClientConfig config;

	public RestClient(URI serverURI) {
		this.serverURI = serverURI;
		this.config = new ClientConfig();

		config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
		config.property( ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
		
		this.client = ClientBuilder.newClient(config);
	}

	protected <T> T reTry(Supplier<T> func) {
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				return func.get();
			} catch (ProcessingException x) {
				Log.fine("ProcessingException: " + x.getMessage());
				sleep(RETRY_SLEEP);
			} catch (Exception x) {
				Log.fine("Exception: " + x.getMessage());
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
