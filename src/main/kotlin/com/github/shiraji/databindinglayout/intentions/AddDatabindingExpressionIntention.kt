package com.github.shiraji.databindinglayout.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

class AddDatabindingExpressionIntention : IntentionAction {
    override fun getFamilyName() = text
    override fun getText() = "Wrap with @{}"
    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val xmlAttribute = getXmlAttribute(editor, file) ?: return false
        val value = xmlAttribute.value ?: return false
        return !xmlAttribute.isLayoutTag() && !value.hasDatabindingExpression()
    }

    private fun XmlAttribute.isLayoutTag() = "layout" == PsiTreeUtil.getParentOfType(this, XmlTag::class.java)?.name

    private fun String.hasDatabindingExpression() = startsWith("@{") && endsWith("}")

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val xmlAttribute = getXmlAttribute(editor, file) ?: return
        xmlAttribute.value = "@{${xmlAttribute.value}}"
    }

    private fun getXmlAttribute(editor: Editor?, file: PsiFile?): XmlAttribute? {
        if (file !is XmlFile) return null
        val rootTag = file.rootTag ?: return null
        val rootTagName = rootTag.name
        if (rootTagName != "layout" || rootTag.getAttribute("xmlns:android") == null) return null
        val offset = editor?.caretModel?.offset ?: return null
        val psiElement = file.findElementAt(offset)
        return PsiTreeUtil.getParentOfType(psiElement, XmlAttribute::class.java)
    }
}