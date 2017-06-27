/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vns.common.xml;

import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author Valery
 */
public class XmlHelper {
    private List<String> list;
    public XmlHelper(List<String> list) {
        this.list = list;
    } 
    
    public void forEach(Consumer<String> action) {
        for ( String s : list) {
            if ( s.equals("str2") ) {
                return;
            }
            action.accept(s);
        }
    }
}
