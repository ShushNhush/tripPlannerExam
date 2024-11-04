package app.security.daos;


import app.security.exceptions.ApiException;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.ValidationException;
import app.security.dtos.UserDTO;
import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityDAO implements ISecurityDAO {

    private static ISecurityDAO instance;
    private static EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public UserDTO getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = getEntityManager()) {

            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);

            query.setParameter("username", username);
            User user = null;

            if (query.getResultList().isEmpty())
                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
            user = query.getSingleResult();
            user.getRoles().size(); // force roles to be fetched from db
            if (!user.verifyPassword(password))
                throw new ValidationException("Wrong password");
            return new UserDTO(user.getUsername(), user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()));
        }
    }

    @Override
    public User createUser(String username, String password) {
        try (EntityManager em = getEntityManager()) {

            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);

            query.setParameter("username", username);

            User userEntity = null;

            if (!query.getResultList().isEmpty())
                throw new EntityExistsException("User with username: " + username + " already exists");

            userEntity = new User(username, password);
            em.getTransaction().begin();
            Role userRole = em.find(Role.class, "user");
            if (userRole == null)
                userRole = new Role("user");
            em.persist(userRole);
            userEntity.addRole(userRole);
            em.persist(userEntity);
            em.getTransaction().commit();
            return userEntity;
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(400, e.getMessage());
        }
    }

    public List<UserDTO> getAllUsers() {
        try (EntityManager em = getEntityManager()) {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            return query.getResultList().stream().map(u -> new UserDTO(u.getUsername(), u.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet()))).collect(Collectors.toList());
        }
    }

    @Override
    public User addRole(UserDTO userDTO, String newRole) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();

            // Find the user and lock it for update
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username",
                    User.class
            );
            query.setParameter("username", userDTO.getUsername());

            User user = query.getSingleResult();
            if (user == null) {
                throw new EntityNotFoundException("No user found with username: " + userDTO.getUsername());
            }

            // Find or create the role
            Role role = em.find(Role.class, newRole);
            if (role == null) {
                role = new Role(newRole);
                em.persist(role);
                em.flush(); // Ensure role is persisted before adding to user
            }

            // Add role to user and user to role
            user.addRole(role);
            em.merge(user); // Merge the updated user

            em.getTransaction().commit();

            // Refresh to ensure we have the latest data
            em.refresh(user);

            return user;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}

