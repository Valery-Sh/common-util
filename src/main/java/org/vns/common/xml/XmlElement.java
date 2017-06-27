package org.vns.common.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.w3c.dom.Element;

/**
 *
 * @author Valery Shyshkin
 */
public interface XmlElement {

    String getTagName();

    void commitUpdates();

    //void check();    
    XmlCompoundElement getParent();

    void setParent(XmlCompoundElement parent);

    Element getElement();

    /**
     * Sets the property {@code element } to null for this element. Removes the
     * DOM element from it's parent node.
     *
     * @return The previous value of the element
     */
    Element nullElement();


    default XmlElement newInstance() {
        XmlElement element;

        try {
            if (getClass().isMemberClass() && !Modifier.isStatic(getClass().getModifiers())) {
                Class<?> innerClass = getClass();
                Class<?> clazz = getClass().getEnclosingClass();

                Field field = innerClass.getDeclaredField("this$0");
                field.setAccessible(true);

                Constructor<?> ctor = innerClass.getDeclaredConstructor(clazz, String.class);
                ctor.setAccessible(true);
                element = (XmlElement) ctor.newInstance(new Object[]{field.get(this), this.getTagName()});
            } else {
                Class<?> clazz = Class.forName(getClass().getName());
                Constructor<?> ctor = clazz.getDeclaredConstructor(String.class);
                ctor.setAccessible(true);
                element = (XmlElement) ctor.newInstance(new Object[]{this.getTagName()});
            }
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException ex) {
            element = null;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            element = null;
        } catch (ClassNotFoundException | NoSuchFieldException ex) {
            element = null;
        }
        return element;
    }

    XmlAttributes getAttributes();
    /**
     * Returns a new instance of the same type as this element.
     * The {@code tagName} and {@code attributes} is cloned.
     * 
     * @return a new instance of the same type as this element
     */
    default XmlElement getClone() {
        XmlElement element = newInstance();
        if (element != null) {
            element.getAttributes().putAll(getAttributes());
        }

        return element;
    }
    static boolean equals(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 != null) {
            return s1.equals(s2);
        }
        return s2.equals(s1);

    }

}
