package cz.vsb.mel0102;

import cz.vsb.mel0102.dto.UserRegisterDto;
import cz.vsb.mel0102.entities.User;
import cz.vsb.mel0102.repository.RepositoryManager;
import cz.vsb.mel0102.util.AuthUtil;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestHeader;
import cz.vsb.mel0102.util.AuthUtil;
import java.util.Base64;
import java.util.Optional;

@Path("/api/users")
public class UserResource {

    @Inject
    RepositoryManager repos;

    @Inject
    Logger logger;

     @GET
     @Path("/me")
     public Response me(@RestHeader("Authorization") String authHeader) {
        var userCredentials = AuthUtil.fromHeader(authHeader);

        Log.info("Credentials " + userCredentials.getUsername() + ":" + userCredentials.getPassword());

        Optional<User> user = repos.getUserRepository().findByUsername(userCredentials.getUsername());

        if (user.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok(user.get()).build();
     }

     @POST
     @Path("/register")
     public Response register(UserRegisterDto userRegisterDto) {
         Optional<User> u = repos.getUserRepository().findByUsername(userRegisterDto.username);

         if (u.isPresent()) {
             return Response.status(Response.Status.CONFLICT).build();
         }

         User user = User.builder()
                 .email(userRegisterDto.email)
                 .username(userRegisterDto.username)
                 .password(userRegisterDto.password)
                 .build();

         repos.getUserRepository().insert(user);

         return Response.ok(user).build();
     }

     @GET
     public Response getAllUsers() {
         return Response.ok(repos.getUserRepository().findAll()).build();
     }

}
