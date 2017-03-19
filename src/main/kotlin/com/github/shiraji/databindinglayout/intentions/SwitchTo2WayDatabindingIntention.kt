package com.github.shiraji.databindinglayout.intentions

import com.github.shiraji.databindinglayout.getPointingXmlAttribute
import com.github.shiraji.databindinglayout.hasDatabindingExpression
import com.github.shiraji.databindinglayout.hasDatabindingLayout
import com.github.shiraji.databindinglayout.isLayoutTag
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class SwitchTo2WayDatabindingIntention : IntentionAction {
    override fun getFamilyName() = text
    override fun getText() = "Switch to 2-way databinding '@={}'"
    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (!file.hasDatabindingLayout()) return false
        val xmlAttribute = getPointingXmlAttribute(editor, file) ?: return false
        return !xmlAttribute.isLayoutTag() && xmlAttribute.hasDatabindingExpression()
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        getPointingXmlAttribute(editor, file)?.let { it.value = it.value?.replaceFirst("@{", "@={") }
    }
}