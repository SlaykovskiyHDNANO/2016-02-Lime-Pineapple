package db.services;


import db.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface AccountService {
    Collection<User> getUsers();

    boolean hasUser(long id);
    boolean hasUser(@NotNull String username);

    long addUser(@NotNull User user);

    boolean removeUser(long id);
    boolean removeUser(@NotNull String username);

    User getUser(long id);
    User getUser(@NotNull String username);

    int getCount();
    boolean changeUser(@NotNull User user);

    void clear();

}
