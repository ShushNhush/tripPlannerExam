package app.security.controllers;

import app.security.exceptions.ApiException;
import app.security.tokensecurity.TokenSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.JOSEException;
import app.utils.Utils;
import app.config.HibernateConfig;
import app.security.daos.ISecurityDAO;
import app.security.daos.SecurityDAO;
import app.security.entities.User;
import app.security.exceptions.NotAuthorizedException;
import app.security.exceptions.ValidationException;
import app.security.tokensecurity.ITokenSecurity;
import app.security.dtos.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityController implements ISecurityController {
    ObjectMapper objectMapper = new ObjectMapper();
    ITokenSecurity tokenSecurity = new TokenSecurity();
    private static ISecurityDAO securityDAO;
    private static SecurityController instance;
    private static Logger logger = LoggerFactory.getLogger(SecurityController.class);

    private SecurityController() { }

    public static SecurityController getInstance() { // Singleton because we don't want multiple instances of the same class
        if (instance == null) {
            instance = new SecurityController();
        }
        securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory(false));
        return instance;
    }

    @Override
    public Handler login() {
        return (ctx) -> {
            ObjectNode returnObject = objectMapper.createObjectNode(); // for sending JSON messages back to the client
            try {
                // Get user credentials from the request body
                UserDTO user = ctx.bodyAsClass(UserDTO.class);

                // Verify user credentials
                UserDTO verifiedUser = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());

                // Create a token for the verified user
                String token = createToken(verifiedUser);

                // Save the verified user to the session
                ctx.sessionAttribute("currentUser", verifiedUser);

                // Send back the token and username
                ctx.status(200).json(returnObject
                        .put("token", token)
                        .put("username", verifiedUser.getUsername()));

            } catch (EntityNotFoundException | ValidationException e) {
                ctx.status(401);
                System.out.println(e.getMessage());
                ctx.json(returnObject.put("msg", e.getMessage()));
            }
        };
    }


    @Override
    public Handler register() {
        return (ctx) -> {
            ObjectNode returnObject = objectMapper.createObjectNode();
            try {
                UserDTO userInput = ctx.bodyAsClass(UserDTO.class);
                User created = securityDAO.createUser(userInput.getUsername(), userInput.getPassword());

                String token = createToken(new UserDTO(created.getUsername(), Set.of("USER")));
                ctx.status(HttpStatus.CREATED).json(returnObject
                        .put("token", token)
                        .put("username", created.getUsername()));
            } catch (EntityExistsException e) {
                ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
                ctx.json(returnObject.put("msg", "User already exists"));
            }
        };
    }

    @Override
    public void getAllUsers(Context ctx) {

        List<UserDTO> users = securityDAO.getAllUsers();
        ctx.json(users, UserDTO.class);
    }

    @Override
    public Handler authenticate() throws UnauthorizedResponse {

        ObjectNode returnObject = objectMapper.createObjectNode();
        return (ctx) -> {
            // This is a preflight request => OK
            if (ctx.method().toString().equals("OPTIONS")) {
                ctx.status(200);
                return;
            }
            String header = ctx.header("Authorization");
            if (header == null) {
                throw new UnauthorizedResponse("Authorization header missing");
            }

            String[] headerParts = header.split(" ");
            if (headerParts.length != 2) {
                throw new UnauthorizedResponse("Authorization header malformed");
            }

            String token = headerParts[1];
            UserDTO verifiedTokenUser = verifyToken(token);

            if (verifiedTokenUser == null) {
                throw new UnauthorizedResponse("Invalid User or Token");
            }
            logger.info("User verified: " + verifiedTokenUser);
            ctx.attribute("user", verifiedTokenUser);
        };
    }

    @Override
    // Check if the user's roles contain any of the allowed roles
    public boolean authorize(UserDTO user, Set<RouteRole> allowedRoles) {
        if (user == null) {
            throw new UnauthorizedResponse("You need to log in, dude!");
        }
        Set<String> roleNames = allowedRoles.stream()
                   .map(RouteRole::toString)  // Convert RouteRoles to  Set of Strings
                   .collect(Collectors.toSet());
        return user.getRoles().stream()
                   .map(String::toUpperCase)
                   .anyMatch(roleNames::contains);
        }

    @Override
    public String createToken(UserDTO user) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }
            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(500, "Could not create token");
        }
    }

    @Override
    public UserDTO verifyToken(String token) {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED ? System.getenv("SECRET_KEY") : Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new NotAuthorizedException(403, "Token is not valid");
            }
        } catch (ParseException | JOSEException | NotAuthorizedException e) {
            e.printStackTrace();
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }

    public @NotNull Handler addRole() {
        return (ctx) -> {
            ObjectNode returnObject = objectMapper.createObjectNode();
            try {
                // Get the role from the body
                String newRole = ctx.bodyAsClass(ObjectNode.class).get("role").asText();
                String token = getTokenFromHeader(ctx);
                UserDTO user = verifyToken(token);

                System.out.println("Adding role " + newRole + " to user " + user.getUsername());

                // Add the new role
                User updatedUser = securityDAO.addRole(user, newRole);

                // Verify roles were added
                Set<String> updatedRoles = updatedUser.getRolesAsStrings();
                System.out.println("Updated user roles: " + updatedRoles);

                UserDTO updatedUserDTO = UserDTO.builder()
                        .id(updatedUser.getId())
                        .username(updatedUser.getUsername())
                        .password(updatedUser.getPassword())
                        .roles(updatedRoles)
                        .build();

                // Regenerate the token with updated roles
                String newToken = createToken(updatedUserDTO);

                System.out.println("New token: " + newToken);

                // Update session
                ctx.sessionAttribute("currentUser", updatedUserDTO);

                // Return the new token and success message
                if (updatedRoles != null && newToken != null) {
                    returnObject
                            .put("msg", "Role " + newRole + " added to user")
                            .put("token", newToken)
                            .putPOJO("roles", updatedRoles); // Corrected putPOJO usage
                    System.out.println("Final JSON response: " + returnObject.toString()); // Log final JSON
                    ctx.status(200).json(returnObject);
                } else {
                    throw new Exception("Failed to update roles or generate token");
                }
            } catch (EntityNotFoundException e) {
                ctx.status(404).json(returnObject.put("msg", "User not found: " + e.getMessage()));
            } catch (Exception e) {
                e.printStackTrace(); // For debugging
                ctx.status(500).json(returnObject.put("msg", "An error occurred: " + e.getMessage()));
            }
        };
    }
    private String getTokenFromHeader(Context ctx) {
        String header = ctx.header("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length()); // Extract the token
        }
        return null; // Return null if the token is missing or malformed
    }

}