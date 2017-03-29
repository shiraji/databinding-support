package com.github.shiraji.databindinglayout.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.psi.xml.XmlTag

class AddVariableTagIntention : AddInsideDataTagIntention() {
    override fun getText() = "Add <variable> tag"
    override val tagName = "variable"
    override val tagTemplate = "<variable name=\"\" type=\"\"/>"
    override fun moveCaret(addedTag: XmlTag?, editor: Editor?) {
        addedTag?.getAttribute("name")?.valueElement?.textOffset?.let { editor?.caretModel?.moveToOffset(it) }
    }
}