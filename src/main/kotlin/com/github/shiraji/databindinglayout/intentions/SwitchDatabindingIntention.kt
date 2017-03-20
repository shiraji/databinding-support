package com.github.shiraji.databindinglayout.intentions

import com.github.shiraji.databindinglayout.getPointingXmlAttribute
import com.github.shiraji.databindinglayout.hasDatabindingLayout
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

abstract class SwitchDatabindingIntention : IntentionAction {
    override fun getFamilyName() = text
    override fun startInWriteAction() = true
    abstract val oldTemplate: String
    abstract val newTemplate: String

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) = file.hasDatabindingLayout()

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        getPointingXmlAttribute(editor, file)?.let { it.value = it.value?.replaceFirst(oldTemplate, newTemplate) }
    }
}