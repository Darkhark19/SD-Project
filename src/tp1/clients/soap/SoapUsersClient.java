package tp1.clients.soap;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import tp1.api.User;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class SoapUsersClient extends SoapClient implements Users {
    private static final String WSDL = "/users/?wsdl";
    private SoapUsers users;

    public SoapUsersClient(URI u) {
        super(u);
    }

    private synchronized SoapUsers create() {
        try {
            QName qname = new QName(SoapUsers.NAMESPACE, SoapUsers.NAME);
            URL url = URI.create(u + WSDL).toURL();
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(CONNECT_TIMEOUT);
            conn.connect();
            if (users == null) {
                Service service = Service.create(url, qname);
                users = service.getPort(SoapUsers.class);
                setClientTimeouts((BindingProvider) users);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.users;
    }
    @Override
    public Result<String> createUser(User user) {
        return super.reTry(() -> clt_createUser(user));
    }


    @Override
    public Result<User> getUser(String userId, String password) {
        return super.reTry(() -> clt_getUser(userId, password));
    }

    @Override
    public Result<User> updateUser(String userId, String password, User user) {
        return super.reTry(() -> clt_updateUser(userId, password, user));
    }

    @Override
    public Result<User> deleteUser(String userId, String password) {
        return super.reTry(() -> clt_deleteUser(userId, password));
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        return super.reTry(() -> clt_searchUsers(pattern));
    }

    private Result<List<User>> clt_searchUsers(String pattern) {
        try {
            List<User> r = create().searchUsers(pattern);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

    @Override
    public Result<User> userExists(String userId) {
        return super.reTry(() -> clt_userExists(userId));
    }

    private Result<User> clt_userExists(String userId) {
        try {
            User r = create().userExists(userId);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

    private Result<User> clt_deleteUser(String userId, String password) {
        try {
            User r = create().deleteUser(userId, password);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

    private Result<User> clt_updateUser(String userId, String password, User user) {
        try {
            User r = create().updateUser(userId, password, user);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }
    }
    private Result<User> clt_getUser(String userId, String password) {
        try {
            User r = create().getUser(userId, password);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }

    }
    private Result<String> clt_createUser(User user) {

        try {
            String r = create().createUser(user);
            return Result.ok(r);
        } catch (UsersException e) {
            return Result.error(statusToErrorCode(e));
        }
    }

}
