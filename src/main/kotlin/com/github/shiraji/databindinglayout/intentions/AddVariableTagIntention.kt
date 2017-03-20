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

class AddVariableTagIntention : IntentionAction {
    override fun getFamilyName() = "Add <variable> tag"
    override fun getText() = "Add <variable> tag"
    override fun startInWriteAction() = true
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) = file.hasDatabindingLayout()

    companion object {
        private const val VARIABLE_TAG_TEMPLATE = "<variable name=\"\" type=\"\"/>"
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is XmlFile) return
        val rootTag = file.rootTag ?: return
        val dataTag = rootTag.findFirstDataTag()
        val factory = XmlElementFactory.getInstance(project)

        val addedVariableTag = if (dataTag == null) {
            val newTag = factory.createTagFromText("<data>$VARIABLE_TAG_TEMPLATE</data>", XMLLanguage.INSTANCE)
            val newDataTag = file.rootTag?.addSubTag(newTag, true) ?: return
            newDataTag.findFirstSubTag("variable")
        } else {
            val newTag = factory.createTagFromText(VARIABLE_TAG_TEMPLATE, XMLLanguage.INSTANCE)

            val lastImportTag = dataTag.findLastSubTag("variable")
            if (lastImportTag == null) {
                dataTag.addSubTag(newTag, true) ?: return
            } else {
                dataTag.addAfter(newTag, lastImportTag) as? XmlTag ?: return
            }
        }
        moveCaretToNameValue(addedVariableTag, editor)
    }

    private fun moveCaretToNameValue(variableTag: XmlTag?, editor: Editor?) {
        variableTag?.getAttribute("name")?.valueElement?.textOffset?.let { editor?.caretModel?.moveToOffset(it) }
    }

}