package com.github.shiraji.databindinglayout.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.psi.xml.XmlTag

class AddImportTagIntention : AddInsideDataTagIntention() {
    override val tagName = "import"
    override val tagTemplate = "<import type=\"\"/>"
    override fun moveCaret(addedTag: XmlTag?, editor: Editor?) {
        addedTag?.getAttribute("type")?.valueElement?.textOffset?.let { editor?.caretModel?.moveToOffset(it) }
    }
}