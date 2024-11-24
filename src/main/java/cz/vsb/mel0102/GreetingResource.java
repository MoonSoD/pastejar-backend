package cz.vsb.mel0102;

import cz.vsb.mel0102.repository.RepositoryManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/hello")
public class GreetingResource {

    @Inject
    RepositoryManager repos;
    @Inject
    Logger logger;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        var users = repos.getUserRepository().findAll();
        System.out.println(users.toString());
        return "Hello from Quarkus REST";
    }
}
