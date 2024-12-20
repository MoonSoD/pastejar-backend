package cz.vsb.mel0102.repository;

import cz.vsb.mel0102.database.DatabaseExecutor;
import cz.vsb.mel0102.entities.MagicCode;
import cz.vsb.mel0102.repository.magicCode.IMagicCodeRepository;
import cz.vsb.mel0102.repository.magicCode.MemMagicCodeRepository;
import cz.vsb.mel0102.repository.paste.IPasteRepository;
import cz.vsb.mel0102.repository.paste.PostgrePasteRepository;
import cz.vsb.mel0102.repository.user.IUserRepository;
import cz.vsb.mel0102.repository.user.PostgreUserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RepositoryManager {

    List<MagicCode> magicCodes = new ArrayList<>();

    @Getter
    private IUserRepository userRepository;

    @Getter
    private IPasteRepository pasteRepository;

    @Getter
    private IMagicCodeRepository magicCodeRepository;

    @Inject
    public RepositoryManager(DataSource dataSource) {
        DatabaseExecutor databaseExecutor = new DatabaseExecutor(dataSource);

        this.userRepository = new PostgreUserRepository(databaseExecutor);
        this.pasteRepository = new PostgrePasteRepository(databaseExecutor);

        magicCodes.add(new MagicCode(1, "123", 4, false));
        //magicCodes.add(new MagicCode(2, "321", 3, true));

        this.magicCodeRepository = new MemMagicCodeRepository(magicCodes);
    }
}
