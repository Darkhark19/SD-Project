package tp1.clients.soap;

import jakarta.xml.ws.Service;
import tp1.api.User;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;

import javax.xml.namespace.QName;
import java.net.URI;
import java.util.List;

public class SoapUsersClient extends SoapClient implements Users {
    private static final String WSDL = "/users/?wsdl";
    public  SoapUsersClient(URI u){


    }
    @Override
    public Result<String> createUser(User user) {
        QName qname = new QName(SoapUsers.NAMESPACE, SoapUsers.NAME);
        Service service = Service.create( URI.create(serverUrl + "?wsdl").toURL(), qname);
        SoapUsers users = service.getPort(sd2122.aula5.api.service.soap.SoapUsers.class);
    }

    @Override
    public Result<User> getUser(String userId, String password) {
        return null;
    }

    @Override
    public Result<User> updateUser(String userId, String password, User user) {
        return null;
    }

    @Override
    public Result<User> deleteUser(String userId, String password) {
        return null;
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        return null;
    }

    @Override
    public Result<User> userExists(String userId) {
        return null;
    }
}
