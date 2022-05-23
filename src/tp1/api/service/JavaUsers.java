package tp1.api.service;

import tp1.api.FileInfo;
import tp1.api.User;
import tp1.api.service.rest.RestDirectory;
import tp1.api.service.util.Directory;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;
import tp1.clients.DirectoryClientFactory;
import tp1.clients.rest.RestDirectoryClient;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

public class JavaUsers implements Users {

    private static Logger Log = Logger.getLogger(JavaUsers.class.getName());
    protected final ConcurrentMap<String, User> users = new ConcurrentHashMap<>();


    public JavaUsers() {
    }

    @Override
    public synchronized Result<String> createUser(User user) {
        // Check if user data is valid
        if (user.getUserId() == null || user.getPassword() == null || user.getFullName() == null ||
                user.getEmail() == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }
        //Add the user to the map of users
        User u = users.putIfAbsent(user.getUserId(), user);
        if (u != null)
            return Result.error(Result.ErrorCode.CONFLICT);
        else
            return Result.ok(user.getUserId());
    }

    @Override
    public Result<User> getUser(String userId, String password) {
        User user = users.get(userId);

        // Check if user exists
        if (user == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        //Check if the password is correct
        if (!user.getPassword().equals(password)) {
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }
        return Result.ok(user);
    }

    @Override
    public Result<User> updateUser(String userId, String password, User user) {
        // TODO Complete method
        User u = null;
        synchronized (users) {
            Result<User> result = this.getUser(userId, password);
            if (!result.isOK())
                return result;
            u = result.value();
            // Check if user is valid
            if (userId == null || password == null) {
                return Result.error(Result.ErrorCode.BAD_REQUEST);
            }
            u.setEmail(user.getEmail() != null ? user.getEmail() : u.getEmail());
            u.setFullName(user.getFullName() != null ? user.getFullName() : u.getFullName());
            u.setPassword(user.getPassword() != null ? user.getPassword() : u.getPassword());
        }
        return Result.ok(u);
    }

    @Override
    public Result<User> deleteUser(String userId, String password) {
        // TODO Complete method
        User user = null;
        synchronized (users) {
            user = users.get(userId);
            // Check if user exists
            if (user == null) {
                return Result.error(Result.ErrorCode.NOT_FOUND);
            }
            //Check if the password is correct
            if (!user.getPassword().equals(password)) {
                return Result.error(Result.ErrorCode.FORBIDDEN);
            }

            DirectoryClientFactory.deleteUserFiles(userId);
            user = users.remove(userId);
        }
        return Result.ok(user);
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        // TODO Complete method
        if (users.isEmpty())
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        List<User> result = new LinkedList<>();
        for (Map.Entry<String, User> e : users.entrySet()) {
            if (e.getValue().getFullName().toLowerCase().contains(pattern.toLowerCase())) {
                User user = e.getValue();
                User u = new User(user.getUserId(), user.getFullName(), user.getEmail(), "");
                result.add(u);
            }
        }
        return Result.ok(result);
    }

    public Result<User> userExists(String userId) {
        if (users.isEmpty())
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        User u = users.get(userId);
        if (u != null) {
            u = new User(u.getUserId(), u.getFullName(), u.getEmail(), "");
            return Result.ok(u);
        }
        return Result.error(Result.ErrorCode.NOT_FOUND);
    }
}
