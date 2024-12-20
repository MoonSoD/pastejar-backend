package cz.vsb.mel0102.repository.magicCode;

import cz.vsb.mel0102.entities.MagicCode;
import cz.vsb.mel0102.repository.IRepository;

import java.util.Optional;

public interface IMagicCodeRepository extends IRepository<MagicCode> {
    Optional<MagicCode> findByMagicCode(String magicCode);
    MagicCode generateMutable(long pasteId);
    MagicCode generateImmutable(long pasteId);
    boolean canCodeAccessPaste(long pasteId, String code);
}
