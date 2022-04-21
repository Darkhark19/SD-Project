package tp1.clients.soap;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import tp1.api.User;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

public class SoapUsersClient extends SoapClient implements Users {
    private static final String WSDL = "/users/?wsdl";
    private final SoapUsers users;

    public SoapUsersClient(URI u){
        super();
        String serverUrl = u.toString() + WSDL;
        QName qname = new QName(SoapUsers.NAMESPACE, SoapUsers.NAME);
        Service service = null;
        try {
            service = Service.create( URI.create(u + "?wsdl").toURL(), qname);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        users = service.getPort(SoapUsers.class);
        setClientTimeouts( (BindingProvider)users);
    }
    @Override
    public Result<String> createUser(User user) {
        return super.reTry( () -> clt_createUser(user));
    }

    private Result<String> clt_createUser(User user)  {
        String r = null;
        try {
            r = users.createUser(user);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }



    }

    @Override
    public Result<User> getUser(String userId, String password) {
        return super.reTry( () -> clt_getUser(userId,password));
    }
    private Result<User> clt_getUser(String userId, String password) {
        User r = null;
        try {
            r = users.getUser(userId, password);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }

    }

    @Override
    public Result<User> updateUser(String userId, String password, User user) {
        return super.reTry( () -> clt_updateUser( userId,  password,  user));
    }

    private Result<User> clt_updateUser(String userId, String password, User user) {
        try {
            User r = users.updateUser(userId, password,user);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

    @Override
    public Result<User> deleteUser(String userId, String password) {
        return super.reTry( () -> clt_deleteUser(userId,password));
    }

    private Result<User> clt_deleteUser(String userId, String password) {
        try {
            User r = users.deleteUser(userId, password);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        return super.reTry( () -> clt_searchUsers(pattern));
    }

    private Result<List<User>> clt_searchUsers(String pattern) {
        try {
            List<User> r = users.searchUsers(pattern);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

    @Override
    public Result<User> userExists(String userId) {
        return super.reTry( () -> clt_userExists( userId ));
    }

    private Result<User> clt_userExists(String userId) {
        try {
            User r = users.userExists(userId);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }
    }
}
