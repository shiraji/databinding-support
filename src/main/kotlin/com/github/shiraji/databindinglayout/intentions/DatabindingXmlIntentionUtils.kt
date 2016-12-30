package com.github.shiraji.databindinglayout.intentions

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

fun getPointingXmlAttribute(editor: Editor?, file: PsiFile?): XmlAttribute? {
    val offset = editor?.caretModel?.offset ?: return null
    val psiElement = file?.findElementAt(offset) ?: return null
    return PsiTreeUtil.getParentOfType(psiElement, XmlAttribute::class.java)
}

fun PsiFile?.getRootTag(): XmlTag? {
    if (this !is XmlFile) return null
    return rootTag
}

fun PsiFile?.hasDatabindingLayout(): Boolean {
    return getRootTag()?.isDatabindingRootTag() ?: return false
}

fun XmlTag.isDatabindingRootTag() = isLayoutTag() && hasNamespaceAndroid()

fun XmlTag.isLayoutTag() = name == "layout"

fun XmlTag.hasNamespaceAndroid() = getAttribute("xmlns:android") != null

fun XmlAttribute.isLayoutTag() = "layout" == PsiTreeUtil.getParentOfType(this, XmlTag::class.java)?.name

fun XmlAttribute.hasDatabindingExpression(): Boolean {
    val value = value ?: return false
    return value.startsWith("@{") && value.endsWith("}")
}

fun XmlAttribute.has2WayDatabindingExpression(): Boolean {
    val value = value ?: return false
    return value.startsWith("@={") && value.endsWith("}")
}