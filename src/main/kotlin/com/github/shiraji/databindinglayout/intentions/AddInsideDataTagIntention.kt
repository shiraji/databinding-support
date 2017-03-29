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

abstract class AddInsideDataTagIntention : IntentionAction {
    abstract override fun getText(): String
    override fun getFamilyName() = text
    override fun startInWriteAction() = true
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) = file.hasDatabindingLayout()

    abstract val tagName: String
    abstract val tagTemplate: String
    abstract fun moveCaret(addedTag: XmlTag?, editor: Editor?)

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is XmlFile) return
        val rootTag = file.rootTag ?: return
        val dataTag = rootTag.findFirstDataTag()
        val factory = XmlElementFactory.getInstance(project)

        val addedTag = if (dataTag == null) {
            val newTag = file.rootTag?.addSubTag(factory.createTagFromText("<data>$tagTemplate</data>", XMLLanguage.INSTANCE), true) ?: return
            newTag.findFirstSubTag(tagName)
        } else {
            val newTag = factory.createTagFromText(tagTemplate, XMLLanguage.INSTANCE)

            val lastImportTag = dataTag.findLastSubTag(tagName)
            if (lastImportTag == null) {
                dataTag.addSubTag(newTag, true) ?: return
            } else {
                dataTag.addAfter(newTag, lastImportTag) as? XmlTag ?: return
            }
        }
        moveCaret(addedTag, editor)
    }
}