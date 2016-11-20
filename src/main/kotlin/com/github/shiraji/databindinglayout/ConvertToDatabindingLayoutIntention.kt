package com.github.shiraji.databindinglayout

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile

class ConvertToDatabindingLayoutIntention : IntentionAction {
    override fun getText() = "Convert to databinding layout"
    override fun getFamilyName() = "Convert to databinding layout"
    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file !is XmlFile) return false
        val rootTag = file.rootTag ?: return false
        return rootTag.name != "layout"
                && rootTag.getAttribute("xmlns:android") == null
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
    }
}