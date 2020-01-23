/*
 * This file is part of MCPIDE, licensed under the MIT License (MIT).
 *
 * Copyright (c) kenzierocks <https://kenzierocks.me>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.kenzierocks.mcpide.fx

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.ListCell
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import me.kenzierocks.mcpide.controller.SearchResult
import org.fxmisc.richtext.model.ReadOnlyStyledDocument

class SearchResultListCell : ListCell<SearchResult>() {
    override fun updateItem(item: SearchResult?, empty: Boolean) {
        super.updateItem(item, empty)
        graphic = item?.takeUnless { empty }?.let { initializeGraphic(item) }
    }

    private fun initializeGraphic(item: SearchResult): Node {
        val textArea = MappingTextArea()

        fun updateDoc(doc: JeaDoc) {
            textArea.replace(doc)
            val paragraph = textArea.getParagraph(0)
            val start = item.columns.first
            val end = (item.columns.last + 1).coerceAtMost(paragraph.length())
            val existingSpans = textArea.getStyleSpans(0, start, end)
            textArea.setStyleSpans(0, start, existingSpans.mapStyles { it.copy(highlighted = true) })
        }

        fun updateToHighlightedDoc() {
            updateDoc(item.highlighting.getCompleted())
        }
        if (item.highlighting.isCompleted) {
            updateToHighlightedDoc()
        } else {
            updateDoc(ReadOnlyStyledDocument.fromString(
                item.line,
                textArea.initialParagraphStyle,
                textArea.initialTextStyle,
                textArea.segOps
            ))
            item.highlighting.invokeOnCompletion {
                Platform.runLater { updateToHighlightedDoc() }
            }
        }
        textArea.isEditable = false
        HBox.setHgrow(textArea, Priority.ALWAYS)
        return HBox(
            textArea
        )
    }
}
