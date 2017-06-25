/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.common.util.prefs;

import java.nio.file.Paths;
import java.util.prefs.AbstractPreferences;

/**
 *
 * @author Valery
 */
public class SystemRootPreferences extends PathPreferences {


        public SystemRootPreferences() {
            super(false);
        }

        public SystemRootPreferences(PathPreferences parent, String name) {
            super(parent, name);
        }

        @Override
        protected AbstractPreferences childSpi(String name) {
            return new SystemRootPreferences(this, name);
        }

        @Override
        protected Storage getStorage(String absolutePath) {
            return PathStorage.instanceReadOnly(absolutePath());
        }

    @Override
    protected Storage getStorage(PathPreferences parent, String absolutePath) {
        return PathStorage.instanceReadOnly(absolutePath());
    }
}