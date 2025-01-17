package app.security.controllers;

import app.security.dtos.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.util.Set;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public interface ISecurityController {
    Handler login(); // to get a token
    Handler register(); // to get a user

    void getAllUsers(Context ctx); // to get all users

    Handler authenticate(); // to verify roles inside token
    boolean authorize(UserDTO userDTO, Set<RouteRole> allowedRoles); // to verify user roles
    String createToken(UserDTO user) throws Exception;
    UserDTO verifyToken(String token) throws Exception;
}
