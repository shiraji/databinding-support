package com.github.shiraji.databindinglayout.intentions

import com.github.shiraji.databindinglayout.*
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class Add2WayDatabindingExpressionIntention : IntentionAction {
    override fun getFamilyName() = text
    override fun getText() = "Wrap with @={}"
    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (!file.hasDatabindingLayout()) return false
        val xmlAttribute = getPointingXmlAttribute(editor, file) ?: return false
        return !xmlAttribute.isLayoutTag()
                && !xmlAttribute.hasDatabindingExpression()
                && !xmlAttribute.has2WayDatabindingExpression()
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val xmlAttribute = getPointingXmlAttribute(editor, file) ?: return
        xmlAttribute.value = "@={${xmlAttribute.value}}"
    }
}