package cz.vsb.mel0102.repository.paste;

import cz.vsb.mel0102.entities.Paste;
import cz.vsb.mel0102.entities.User;
import cz.vsb.mel0102.repository.IRepository;

import java.util.List;

public interface IPasteRepository extends IRepository<Paste> {
    List<User> deleteExpired();
}
