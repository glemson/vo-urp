/*
 * JPARequestListener.java
 * 
 * Created on 12 sept. 2007, 15:44:15
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ivoa.web.servlet;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import org.ivoa.service.VO_URP_Facade;

/**
 *
 * @author laurent
 */
public class JPARequestListener implements ServletRequestListener {

 public void requestInitialized(ServletRequestEvent evt) {
 }

 public void requestDestroyed(ServletRequestEvent evt) {

   final VO_URP_Facade facade = VO_URP_Facade.getInstance();
   if (facade != null) {
     facade.closeEntityManager();
   }
 }
}
