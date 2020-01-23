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

import me.kenzierocks.mcpide.SrgType
import me.kenzierocks.mcpide.detectSrgType
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.StyledTextArea
import java.util.Collections

/**
 * TextArea with SRG mappings backing some sections of text.
 *
 * Based on [CodeArea].
 */
open class MappingTextArea : StyledTextArea<Collection<String>, MapStyle>(
    setOf(), { textFlow, styleClasses -> textFlow.styleClass.addAll(styleClasses) },
    DEFAULT_MAP_STYLE, { textExt, style -> textExt.styleClass.addAll(style.styleClasses) },
    false
) {
    init {
        styleClass.add("code-area")

        // load the default style that defines a fixed-width font
        stylesheets.add(CodeArea::class.java.getResource("code-area.css").toExternalForm())

        // don't apply preceding style to typed text
        useInitialStyleForInsertion = true
    }
}

val DEFAULT_MAP_STYLE = MapStyle(text = "", style = Style.DEFAULT_TEXT)

data class MapStyle(
    val text: String,
    val style: Style,
    val jumpTarget: JumpTarget? = null,
    val srgName: String? = null,
    val highlighted: Boolean = false
) {
    val styleClasses: Set<String> by lazy {
        val result = mutableSetOf(style.styleClass)
        when (srgName?.detectSrgType()) {
            SrgType.FIELD -> Collections.addAll(result, "mapped", "field")
            SrgType.METHOD -> Collections.addAll(result, "mapped", "method")
            SrgType.PARAMETER -> Collections.addAll(result, "mapped", "param")
        }
        if (highlighted) {
            result.add("mcpide-highlighted")
        }
        result
    }
}
