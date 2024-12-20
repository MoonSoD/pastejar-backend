package cz.vsb.mel0102.repository.magicCode;

import cz.vsb.mel0102.entities.MagicCode;
import cz.vsb.mel0102.util.AuthUtil;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@AllArgsConstructor
public class MemMagicCodeRepository implements IMagicCodeRepository {

    private final List<MagicCode> magicCodes;

    @Override
    public void insert(MagicCode entity) {
        magicCodes.add(entity);
    }

    @Override
    public void update(long id, MagicCode entity) {
        for (int i = 0; i < magicCodes.size(); i++) {
            if (magicCodes.get(i).getId() == id) {
                magicCodes.set(i, entity);
            }
        }
    }

    @Override
    public void delete(long id) {
        magicCodes.removeIf(magicCode -> magicCode.getId() == id);
    }

    @Override
    public void deleteAll() {
        magicCodes.clear();
    }

    @Override
    public Optional<MagicCode> findById(long id) {
        return magicCodes.stream().filter(magicCode -> magicCode.getId() == id).findFirst();
    }

    @Override
    public List<MagicCode> findAll() {
        return magicCodes;
    }

    @Override
    public Optional<MagicCode> findByMagicCode(String magicCode) {
        return magicCodes.stream().filter(code -> code.getCode().equals(magicCode)).findFirst();
    }

    @Override
    public MagicCode generateMutable(long pasteId) {
        var newCode = new MagicCode(
                magicCodes.size() + 1,
                AuthUtil.generateRandomHash(),
                pasteId,
                true
        );

        magicCodes.add(newCode);

        return newCode;
    }

    @Override
    public MagicCode generateImmutable(long pasteId) {
        var newCode = new MagicCode(
                magicCodes.size() + 1,
                AuthUtil.generateRandomHash(),
                pasteId,
                false
        );

        magicCodes.add(newCode);

        return newCode;
    }


    @Override
    public boolean canCodeAccessPaste(long pasteId, String code) {
        var paste = findByMagicCode(code);

        if (paste.isEmpty()) {
            return false;
        }

        return paste.get().getPasteId() == pasteId;
    }
}
