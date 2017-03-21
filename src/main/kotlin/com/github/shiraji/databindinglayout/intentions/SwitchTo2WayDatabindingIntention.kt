package com.github.shiraji.databindinglayout.intentions

import com.github.shiraji.databindinglayout.getPointingXmlAttribute
import com.github.shiraji.databindinglayout.hasDatabindingExpression
import com.github.shiraji.databindinglayout.isLayoutTag
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class SwitchTo2WayDatabindingIntention : SwitchDatabindingIntention() {
    override val oldTemplate = "@{"
    override val newTemplate = "@={"
    override fun getText() = "Switch to 2-way databinding '@={}'"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (!super.isAvailable(project, editor, file)) return false
        val xmlAttribute = getPointingXmlAttribute(editor, file) ?: return false
        return !xmlAttribute.isLayoutTag() && xmlAttribute.hasDatabindingExpression()
    }
}