package com.github.shiraji.databindinglayout.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.XmlElementFactory
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

class AddVariableTagIntention : IntentionAction {
    override fun getFamilyName() = "Add <variable> tag"
    override fun getText() = "Add <variable> tag"

    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val rootTag = file.getRootTag() ?: return false
        return rootTag.isDatabindingRootTag()
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is XmlFile) return
        val rootTag = file.rootTag ?: return
        val dataTag = rootTag.findFirstSubTag("data")
        val factory = XmlElementFactory.getInstance(project)

        if (dataTag == null) {
            val newTag = file.rootTag?.addSubTag(factory.createTagFromText("<data><variable name=\"\" type=\"\"/></data>", XMLLanguage.INSTANCE), true) ?: return
            newTag.findFirstSubTag("variable")?.getAttribute("name")?.valueElement?.textOffset?.let { editor?.caretModel?.moveToOffset(it) }
        } else {
            val lastImportTag = findLastSubTag(dataTag, "import")
            val newTag = XmlElementFactory.getInstance(project).createTagFromText("<variable/>", XMLLanguage.INSTANCE)
            newTag.setAttribute("name", "")
            newTag.setAttribute("type", "")

            val addedTag = if (lastImportTag == null) {
                dataTag.addSubTag(newTag, true)
            } else {
                dataTag.addAfter(newTag, lastImportTag) as XmlTag
            }

            val addedTypeValueOffset = addedTag?.getAttribute("name")?.valueElement?.textOffset
            if (addedTypeValueOffset != null)
                editor?.caretModel?.moveToOffset(addedTypeValueOffset)
        }
    }

    fun findLastSubTag(dataTag: XmlTag, tagName: String): XmlTag? {
        val importTags = dataTag.findSubTags(tagName)
        return if (importTags.isNotEmpty()) importTags[importTags.size - 1] else null
    }
}