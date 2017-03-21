package com.github.shiraji.databindinglayout.intentions

import com.github.shiraji.databindinglayout.*
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute

abstract class WrapWithDatabindingIntention : IntentionAction {
    override fun getFamilyName() = text
    override fun startInWriteAction() = true

    abstract protected fun getTemplate(xmlAttribute: XmlAttribute): String

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (!file.hasDatabindingLayout()) return false
        val xmlAttribute = getPointingXmlAttribute(editor, file) ?: return false
        return !xmlAttribute.isLayoutTag()
                && !xmlAttribute.hasDatabindingExpression()
                && !xmlAttribute.has2WayDatabindingExpression()
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val xmlAttribute = getPointingXmlAttribute(editor, file) ?: return
        xmlAttribute.value = getTemplate(xmlAttribute)
    }

}