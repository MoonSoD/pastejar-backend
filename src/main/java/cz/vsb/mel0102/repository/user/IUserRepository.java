package cz.vsb.mel0102.repository.user;

import cz.vsb.mel0102.entities.User;
import cz.vsb.mel0102.repository.IRepository;

import java.util.Optional;

public interface IUserRepository extends IRepository<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
