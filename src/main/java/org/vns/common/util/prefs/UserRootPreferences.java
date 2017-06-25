package org.vns.common.util.prefs;

import java.util.prefs.AbstractPreferences;

/**
 *
 * @author Valery
 */
public class UserRootPreferences extends PathPreferences {

    public UserRootPreferences() {
        super(true);
    }

    protected UserRootPreferences(PathPreferences parent, String name) {
        super(parent, name);
    }

    @Override
    protected AbstractPreferences childSpi(String name) {
        return new UserRootPreferences(this, name);
    }

    @Override
    protected Storage getStorage(String absolutePath) {
        return PathStorage.instance(absolutePath());
    }

    @Override
    protected Storage getStorage(PathPreferences parent, String absolutePath) {
        return getStorage(absolutePath);
    }
}
