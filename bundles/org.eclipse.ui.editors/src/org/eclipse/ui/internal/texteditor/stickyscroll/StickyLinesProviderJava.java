package org.eclipse.ui.internal.texteditor.stickyscroll;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.StyledText;

import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISourceViewer;

import org.eclipse.ui.texteditor.stickyscroll.IStickyLinesProvider;

public class StickyLinesProviderJava implements IStickyLinesProvider {

	@Override
	public List<StickyLine> get(ISourceViewer sourceViewer, StickyLinesProperties properties) {
		StyledText textWidget= sourceViewer.getTextWidget();

		int topIndex= textWidget.getTopIndex();

		ArrayList<StickyLine> stickyLines= new ArrayList<>();

		for (int i= 0; i < topIndex; i++) {
			String line= textWidget.getLine(i);

			String trimmedLine= line.trim();
			if (trimmedLine.startsWith("public") || trimmedLine.startsWith("protected") || trimmedLine.startsWith("private")) {
				stickyLines.add(new StickyLine(line, mapLineNumberToSourceViewerLine(i, sourceViewer)));
			}
		}

		return stickyLines;
	}

	private int mapLineNumberToSourceViewerLine(int lineNumber, ISourceViewer sourceViewer) {
		if (sourceViewer instanceof ITextViewerExtension5 extension) {
			return extension.widgetLine2ModelLine(lineNumber);
		}
		return lineNumber;
	}

}
