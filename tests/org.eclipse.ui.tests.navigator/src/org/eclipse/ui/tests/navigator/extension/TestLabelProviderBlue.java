/*******************************************************************************
 * Copyright (c) 2008, 2010 Oakland Software Incorporated and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Oakland Software Incorporated - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.tests.navigator.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @since 3.3
 */
public class TestLabelProviderBlue extends TestStyledLabelProvider {

	public static TestLabelProviderBlue instance;

	@Override
	protected void initSubclass() {
		backgroundColor = Display.getCurrent().getSystemColor(
				SWT.COLOR_BLUE);
		backgroundColorName = "Blue";
		image = PlatformUI.getWorkbench().getSharedImages().getImage(
				ISharedImages.IMG_ETOOL_SAVE_EDIT);
		instance = this;
	}

}
