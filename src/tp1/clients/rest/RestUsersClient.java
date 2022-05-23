package tp1.clients.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import tp1.api.User;
import tp1.api.service.rest.RestUsers;
import tp1.api.service.util.Result;
import tp1.api.service.util.Users;

import java.net.URI;
import java.util.List;

public class RestUsersClient extends RestClient implements Users {

	final WebTarget target;

	
	public RestUsersClient( URI serverURI ) {
		super( serverURI );
		target = client.target( serverURI ).path( RestUsers.PATH );
	}
	
	@Override
	public Result<String> createUser(User user) {
		return super.reTry( () -> clt_createUser( user ));
	}

	@Override
	public Result<User> getUser(String userId, String password) {
		// TODO Auto-generated method stub
		return reTry( () -> clt_getUser(userId, password));
	}



	@Override
	public Result<User> updateUser(String userId, String password, User user) {
		// TODO Auto-generated method stub
		return reTry(() ->  clt_updateUser(userId, password,user));
	}



	@Override
	public Result<User> deleteUser(String userId, String password) {
		// TODO Auto-generated method stub
		return reTry(() ->  clt_deleteUser(userId, password));
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		return super.reTry( () -> clt_searchUsers( pattern ));
	}


	public Result<User> userExists(String userId) {
		return super.reTry( () -> clt_userExists( userId ));
	}

	private Result<User> clt_userExists(String userId) {
		Response r = target.path(userId).path("exists")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			return Result.ok(r.readEntity(User.class));
		} else
			return Result.error(statusToErrorCode(r.getStatusInfo().toEnum()));
	}

	private Result<User> clt_getUser(String userId, String password) {

		Response r = target.path( userId )
				.queryParam(RestUsers.PASSWORD, password)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			return Result.ok(r.readEntity(User.class));
		} else
			return Result.error(statusToErrorCode(r.getStatusInfo().toEnum()));
	}

	private Result<String> clt_createUser( User user) {
		
		Response r = target.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) //SUCESS
			return Result.ok(r.readEntity(String.class));
		else
			return Result.error(statusToErrorCode(r.getStatusInfo().toEnum()));
	}
	private Result<User> clt_updateUser(String userId, String password, User user) {
		Response r = target.path( userId )
				.queryParam(RestUsers.PASSWORD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(user,MediaType.APPLICATION_JSON));

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
			return Result.ok(r.readEntity(User.class));
		}else
			return Result.error(statusToErrorCode(r.getStatusInfo().toEnum()));
	}
	private Result<User> clt_deleteUser(String userId, String password){
		Response r = target.path(userId)
				.queryParam(RestUsers.PASSWORD, password).request()
				.accept(MediaType.APPLICATION_JSON)
				.delete();

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() ) {
				return Result.ok(r.readEntity(User.class));
		} else
			return Result.error(statusToErrorCode(r.getStatusInfo().toEnum()));
	}

	private Result<List<User>> clt_searchUsers(String pattern) {
		Response r = target
				.queryParam(RestUsers.QUERY, pattern)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get();

		if( r.getStatus() == Status.OK.getStatusCode() && r.hasEntity() )
			return Result.ok(r.readEntity(new GenericType<List<User>>(){}));
		else 
			return Result.error(statusToErrorCode(r.getStatusInfo().toEnum()));

	}
}
