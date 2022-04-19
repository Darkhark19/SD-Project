package tp1.server.soap.WebService;


import jakarta.jws.WebService;
import tp1.api.User;
import tp1.api.service.JavaUsers;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;
import java.util.List;
import java.util.logging.Logger;

@WebService(serviceName=SoapUsers.NAME, targetNamespace=SoapUsers.NAMESPACE, endpointInterface=SoapUsers.INTERFACE)
public class UsersWebService implements SoapUsers {

	private static final String CONFLICT = "Conflict";
	private static final String BAD_REQUEST = "Bad Request";

	static Logger Log = Logger.getLogger(UsersWebService.class.getName());
	private final Users impl = new JavaUsers();
	public UsersWebService() {
	}
	public static <T> T result(Result<T> result) throws UsersException {
		if (result.isOK())
			return result.value();
		else
			throw new UsersException(result.error().name());
	}
	@Override
	public String createUser(User user) throws UsersException {
		Log.info(String.format("SOAP createUser: user = %s\n", user));
		var result = impl.createUser(user);
		return result(result);
	}

	@Override
	public User getUser(String userId, String password) throws UsersException {
		var result = impl.getUser(userId,password);
		return result(result);
	}

	@Override
	public User updateUser(String userId, String password, User user) throws UsersException {
		var result = impl.updateUser(userId,password,user);
		return result(result);
	}

	@Override
	public User deleteUser(String userId, String password) throws UsersException {
		var result = impl.deleteUser(userId,password);
		return result(result);

	}

	@Override
	public List<User> searchUsers(String pattern) throws UsersException {
		var result = impl.searchUsers(pattern);
		return result(result);

	}

	@Override
	public User userExists(String userId) throws UsersException {
		var result = impl.userExists(userId);
		return result(result);
	}


	private boolean badUserData(User user) {
		//TODO check user data...
		return false;
	}
	
}
