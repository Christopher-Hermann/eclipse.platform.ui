/*******************************************************************************
 * Copyright (c) 2024 SAP SE.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     SAP SE - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.texteditor.stickyscroll;

import java.util.List;

import org.eclipse.swt.custom.StyledText;

import org.eclipse.jface.text.source.ISourceViewer;

import org.eclipse.ui.internal.texteditor.stickyscroll.StickyLine;

/**
 * TODO: major version increase required?
 * 
 * @since 3.19
 */
public interface IStickyLinesProvider {

	/**
	 * Calculate the sticky lines for the given source code in the source viewer for the given
	 * vertical offset.
	 * 
	 * @param sourceViewer The source viewer containing the source code and information about the
	 *            first visible line
	 * @return The list of sticky lines to show
	 * 
	 * @see ISourceViewer#getTopIndex()
	 * @see ISourceViewer#getTextWidget()
	 * @see StyledText#getTopIndex()
	 */
	public List<StickyLine> get(ISourceViewer sourceViewer, StickyLinesProperties properties);

	/**
	 * Properties required to calculate the sticky lines.
	 * 
	 * @param tabWith The with of a tab
	 */
	record StickyLinesProperties(int tabWith) {
	}

}
