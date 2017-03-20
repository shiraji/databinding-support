package com.github.shiraji.databindinglayout.intentions

import com.github.shiraji.databindinglayout.findFirstDataTag
import com.github.shiraji.databindinglayout.findLastSubTag
import com.github.shiraji.databindinglayout.hasDatabindingLayout
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
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) = file.hasDatabindingLayout()

    companion object {
        private const val IMPORT_TAG_TEMPLATE = "<import type=\"\"/>"
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is XmlFile) return
        val rootTag = file.rootTag ?: return
        val dataTag = rootTag.findFirstDataTag()
        val factory = XmlElementFactory.getInstance(project)

        val addedTag = if (dataTag == null) {
            val newTag = file.rootTag?.addSubTag(factory.createTagFromText("<data>$IMPORT_TAG_TEMPLATE</data>", XMLLanguage.INSTANCE), true) ?: return
            newTag.findFirstSubTag("import")
        } else {
            val newTag = factory.createTagFromText(IMPORT_TAG_TEMPLATE, XMLLanguage.INSTANCE)

            val lastImportTag = dataTag.findLastSubTag("import")
            if (lastImportTag == null) {
                dataTag.addSubTag(newTag, true) ?: return
            } else {
                dataTag.addAfter(newTag, lastImportTag) as? XmlTag ?: return
            }

        }
        moveCaret(addedTag, editor)
    }

    private fun moveCaret(tag: XmlTag?, editor: Editor?) {
        tag?.getAttribute("type")?.valueElement?.textOffset?.let { editor?.caretModel?.moveToOffset(it) }
    }

}