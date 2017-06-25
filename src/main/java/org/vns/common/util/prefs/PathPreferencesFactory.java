/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.common.util.prefs;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

public class PathPreferencesFactory implements PreferencesFactory {
    private static final String FACTORY = "java.util.prefs.PreferencesFactory";//NOI18N
    
    @Override
    public Preferences userRoot() {
        return PathPreferences.pathUserRoot();
    }
    
    @Override
    public Preferences systemRoot() {
        return PathPreferences.pathSystemRoot();
    }

    public static void register() {
        if (System.getProperty(FACTORY) == null) {
            System.setProperty(FACTORY,PathPreferencesFactory.class.getName());
        }
    }
}
