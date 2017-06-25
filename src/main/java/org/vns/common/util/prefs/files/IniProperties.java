package org.vns.common.util.prefs.files;

import java.util.logging.Logger;

/**
 *
 * @author Valery
 */
public class IniProperties extends BaseProperties{

    private static final Logger LOG = Logger.getLogger(IniProperties.class.getName());
    
    public IniProperties() {
        super();
    }

    @Override
    public Section section() {
        return super.section();
    }

    @Override
    public Section section(String sectionName) {
        return super.section(sectionName);
    }
    @Override
    protected IniFile iniFile() {
        return iniFile;
    }


    @Override
    public Section[] sections() {
        return super.sections();
    }
}
