package cz.vsb.mel0102.repository;

import cz.vsb.mel0102.database.DatabaseExecutor;
import cz.vsb.mel0102.repository.user.IUserRepository;
import cz.vsb.mel0102.repository.user.PostgreUserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;

import javax.sql.DataSource;

@ApplicationScoped
public class RepositoryManager {

    @Getter
    private IUserRepository userRepository;

    @Inject
    public RepositoryManager(DataSource dataSource) {
        DatabaseExecutor databaseExecutor = new DatabaseExecutor(dataSource);

        this.userRepository = new PostgreUserRepository(databaseExecutor);
    }
}
