package tp1.server.rest.resources;

import jakarta.inject.Singleton;
import tp1.api.User;
import tp1.api.service.JavaUsers;
import tp1.api.service.rest.RestUsers;
import tp1.api.service.util.Users;

import java.util.List;

import java.util.logging.Logger;

@Singleton
public class UsersResource extends Resource implements RestUsers {


    private static Logger Log = Logger.getLogger(UsersResource.class.getName());

    private final Users impl = new JavaUsers();

    public UsersResource() {
    }

    @Override
    public String createUser(User user) {
        Log.info("createUser : " + user);
        var result = impl.createUser(user);
        return super.response(result);

    }


    //@Override
    public User getUser(String userId, String password) {
        Log.info("getUser : user = " + userId + "; pwd = " + password);
        var result = impl.getUser(userId,password);
        return super.response(result);

    }


    @Override
    public User updateUser(String userId, String password, User user) {
        Log.info("updateUser: user = "+ userId+"; pwd = "+password);
        var result = impl.updateUser(userId, password, user);
        return super.response(result);
    }


    @Override
    public User deleteUser(String userId, String password) {
        Log.info("deleteUser");
        var result = impl.deleteUser(userId, password);
        return super.response(result);
    }


    @Override
    public List<User> searchUsers(String pattern) {
        Log.info("searchUsers");
        var result = impl.searchUsers(pattern);
        return super.response(result);
    }

    @Override
    public User userExists(String userId) {
        Log.info("userExists");
        var result = impl.userExists(userId);
        return super.response(result);
    }

}
