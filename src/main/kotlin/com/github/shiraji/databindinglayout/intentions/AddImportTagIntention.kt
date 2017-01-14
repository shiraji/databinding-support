package com.github.shiraji.databindinglayout.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.XmlElementFactory
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

class AddImportTagIntention : IntentionAction {
    override fun getFamilyName() = "Add <import> tag"
    override fun getText() = "Add <import> tag"

    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val rootTag = file.getRootTag() ?: return false
        if (!rootTag.isDatabindingRootTag()) return false

        if (rootTag.findFirstSubTag("data") == null) return false

        val parentTag = getPointingParentElement<XmlTag>(editor, file) ?: return false
        return parentTag.name == "data"
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is XmlFile) return
        val dataTag = file.rootTag?.findFirstSubTag("data") ?: return

        val lastImportTag = findLastSubTag(dataTag, "import")
        val newTag = XmlElementFactory.getInstance(project).createTagFromText("<import/>", XMLLanguage.INSTANCE)
        newTag.setAttribute("type", "")

        val addedTag = if (lastImportTag == null) {
            dataTag.addSubTag(newTag, true)
        } else {
            dataTag.addAfter(newTag, lastImportTag) as XmlTag
        }

        val addedTypeValueOffset = addedTag?.getAttribute("type")?.valueElement?.textOffset
        if (addedTypeValueOffset != null)
            editor?.caretModel?.moveToOffset(addedTypeValueOffset)
    }

    fun findLastSubTag(dataTag: XmlTag, tagName: String): XmlTag? {
        val importTags = dataTag.findSubTags(tagName)
        return if (importTags.size > 0) importTags[importTags.size - 1] else null
    }
}