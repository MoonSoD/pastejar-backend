package cz.vsb.mel0102;

import cz.vsb.mel0102.entities.MagicCode;
import cz.vsb.mel0102.entities.Paste;
import cz.vsb.mel0102.entities.User;
import cz.vsb.mel0102.repository.RepositoryManager;
import cz.vsb.mel0102.util.AuthUtil;
import io.quarkus.logging.Log;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestHeader;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.Objects;
import java.util.Optional;

@Path("/api/paste")
public class PasteResource {

    @Inject
    RepositoryManager repos;

    @Inject
    Mailer mailer;

    @GET
    @Path("/own")
    public Response getOwnPastes(@RestHeader("Authorization") String authHeader) {
        var userCredentials = AuthUtil.fromHeader(authHeader);

        Optional<User> user = repos.getUserRepository().findByUsername(userCredentials.getUsername());

        if (user.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        for (Paste paste : user.get().getPastes()) {
            Log.info("own Paste: " + paste.toString());
        }

        return Response.ok(user.get().pastes).build();
    }

    @POST
    @Path("/all/{id}/code")
    public Response createCode(
            @PathParam("id") Long id,
            @RestHeader("Authorization") String authHeader
    ) {
        var userCredentials = AuthUtil.fromHeader(authHeader);

        if (userCredentials == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Log.info("Fetching user");

        var user = repos.getUserRepository().findByUsername(userCredentials.getUsername());

        if (user.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Log.info("Fetching pastes");

        var paste = repos.getPasteRepository().findById(id);

        Log.info("Paste " + paste.toString());

        if (paste.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Log.info("Comparing " + paste.get().getUserId() + " to " + user.get().getId());
        if (!Objects.equals(paste.get().getUserId(), user.get().getId())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Log.info("Creating code");


        MagicCode code = repos.getMagicCodeRepository().generateImmutable(id);

        return Response.ok(code).build();
    }

    @GET
    @Path("/all/{id}")
    public Response getPasteById(
            @PathParam("id") Long id,
            @RestHeader("Authorization") String authHeader,
            @RestQuery("code") String code
    ) {
        var userCredentials = AuthUtil.fromHeader(authHeader);

        Optional<Paste> paste = repos.getPasteRepository().findById(id);

        if (paste.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Optional<User> user = Optional.empty();

        var codeRepository = repos.getMagicCodeRepository();

        Log.info("Accessing paste with code " + code);

        if (code != null) {
            var magicCode = codeRepository.findByMagicCode(code);

            if (userCredentials != null) {
                user = repos.getUserRepository().findByUsername(userCredentials.getUsername());
            }

            if (magicCode.isEmpty() && user.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            if (magicCode.isPresent() && magicCode.get().getPasteId() != paste.get().getId() && user.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            return Response.ok(paste.get()).build();
        }

        if (userCredentials == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        user = repos.getUserRepository().findByUsername(userCredentials.getUsername());

        if (user.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if (Objects.equals(paste.get().getUserId(), user.get().getId())) {
            return Response.ok(paste.get()).build();
        }

        return Response.ok(paste.get()).build();
    }

    @POST
    @Path("/all/{id}/flag")
    public Response reportPaste(@PathParam("id") Long id) {
        var paste = repos.getPasteRepository().findById(id);

        if (paste.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        MagicCode code = repos.getMagicCodeRepository().generateMutable(id);

        var link = "https://localhost:5173/paste/" + paste.get().getId() + "?code=" + code.getCode() + "&review=true";

        mailer.send(
                Mail.withText(
                        "moonsod8@gmail.com",
                        "Paste report",
                        "A paste was reported by a user, please review it " + link
                )
        );

        return Response.ok().build();
    }

    @GET
    @Path("/all/{id}/{code}/review")
    public Response reviewPaste(@PathParam("id") Long id, @RestQuery("code") String code) {
        var paste = repos.getPasteRepository().findById(id);

        if (paste.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!repos.getMagicCodeRepository().canCodeAccessPaste(paste.get().getId(), code)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        return Response.ok(paste.get()).build();
    }

    @GET
    @Path("/all/export")
    public Response exportAllPastes(@RestHeader("Authorization") String authHeader) {
        var userCredentials = AuthUtil.fromHeader(authHeader);

        if (userCredentials == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Optional<User> user = repos.getUserRepository().findByUsername(userCredentials.getUsername());

        if (user.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        var pastes = user.get().pastes;

        if (pastes == null || pastes.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        try {
            var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String jsonExport = objectMapper.writeValueAsString(pastes);

            return Response
                    .ok(jsonExport)
                    .header("Content-Disposition", "attachment; filename=\"pastes_export.json\"")
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            Log.error("Failed to export pastes", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/all/{id}/{code}/delete")
    public Response deletePaste(
            @PathParam("id") Long id,
            @PathParam("code") String code
    ) {
        var paste = repos.getPasteRepository().findById(id);

        if (paste.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!repos.getMagicCodeRepository().canCodeAccessPaste(paste.get().getId(), code)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Optional<MagicCode> magicCode = repos.getMagicCodeRepository().findByMagicCode(code);

        if (magicCode.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!magicCode.get().isMutable()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Log.info("Deleting paste with code " + code);

        //repos.getPasteRepository().delete(id);

        return Response.ok().build();
    }

}
