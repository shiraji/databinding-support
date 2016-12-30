package com.github.shiraji.databindinglayout.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

fun getPointingXmlAttribute(editor: Editor?, file: PsiFile?): XmlAttribute? {
    if (file !is XmlFile) return null
    val rootTag = file.rootTag ?: return null
    val rootTagName = rootTag.name
    if (rootTagName != "layout" || rootTag.getAttribute("xmlns:android") == null) return null
    val offset = editor?.caretModel?.offset ?: return null
    val psiElement = file.findElementAt(offset)
    return PsiTreeUtil.getParentOfType(psiElement, XmlAttribute::class.java)
}

fun XmlAttribute.isLayoutTag() = "layout" == PsiTreeUtil.getParentOfType(this, XmlTag::class.java)?.name

fun XmlAttribute.hasDatabindingExpression(): Boolean {
    val value = value ?: return false
    return value.startsWith("@{") && value.endsWith("}")
}