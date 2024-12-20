package cz.vsb.mel0102;

import cz.vsb.mel0102.entities.Paste;
import cz.vsb.mel0102.entities.User;
import cz.vsb.mel0102.repository.RepositoryManager;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.logging.Log;

import java.util.List;

@ApplicationScoped
public class ExpirationBean {

    @Inject
    RepositoryManager repositoryManager;

    @Inject
    Mailer mailer;

    private void sendExpirationEmail(String email) {
        mailer.send(
                Mail.withText(
                        email,
                        "Paste expiration",
                        "You paste has expired :/"
                )
        );
    }

//    private void sendNewsletterEmail(String email, List<Paste> pastes) {
//        StringBuilder sb = new StringBuilder();
//
//        pastes.subList(0, 2).forEach(paste -> {
//            sb.append("http://localhost:5173/pastes/all/" + paste.getId())
//        });
//
//        mailer.send(
//                Mail.withText(
//                        email,
//                        "Hot new pastes!",
//                        "Here are the hottest new pastes of the past week: "
//                )
//        );
//    }

    @Scheduled(every = "60s")
    void deleteExpired() {
        List<User> usersWithDeletedPastes = repositoryManager.getPasteRepository().deleteExpired();
        for (User user : usersWithDeletedPastes) {
            Log.info("Deleted paste of " + user.getEmail() + ", sending notification...");
           // sendExpirationEmail(user.getEmail());
        }
        Log.info("Finished deleting expired pastes...");
    }
}
