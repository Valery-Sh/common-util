package org.vns.common;

import java.io.InputStream;

/**
 *
 * @author Valery
 */
public class Util {
      public static InputStream getResourceAsStream(String resource) {
        // Try to format as a URL?
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = null;
        if (loader != null) {
            try {
                is = loader.getResourceAsStream(resource);
                if (is == null && resource.startsWith("/")) {
                    is = loader.getResourceAsStream(resource.substring(1));
                }
            } catch (IllegalArgumentException e) {

            }
        }
        if (is == null) {
            loader = Util.class.getClassLoader();
            if (loader != null) {
                is = loader.getResourceAsStream(resource);
                if (is == null && resource.startsWith("/")) {
                    is = loader.getResourceAsStream(resource.substring(1));
                }
            }
        }

        return is;
    }
  
}
