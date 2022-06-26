package eu.overnetwork.util;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class AssingRole {
    public AssingRole(Server server, User user, Long roleID) {
        server.getRoleById(roleID).ifPresent(role -> role.addUser(user));
    }
}
