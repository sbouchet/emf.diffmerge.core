/**
 * <copyright>
 * 
 * Copyright (c) 2010-2012 Thales Global Services S.A.S.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Thales Global Services S.A.S. - initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.emf.diffmerge.gmf;

import org.eclipse.osgi.util.NLS;


/**
 * String management.
 * @author Olivier Constant
 */
public class Messages extends NLS {
  private static final String BUNDLE_NAME = "org.eclipse.emf.diffmerge.gmf.messages"; //$NON-NLS-1$
  public static String GMFScope_CannotLoad;
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
    // Nothing needed
  }
}
