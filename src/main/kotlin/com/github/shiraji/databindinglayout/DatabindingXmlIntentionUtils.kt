package com.github.shiraji.databindinglayout

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

fun getPointingElement(editor: Editor?, file: PsiFile?): PsiElement? {
    val offset = editor?.caretModel?.offset ?: return null
    return file?.findElementAt(offset)
}

fun getPointingXmlAttribute(editor: Editor?, file: PsiFile?) = getPointingParentElement<XmlAttribute>(editor, file)

inline fun <reified T : PsiElement> getPointingParentElement(editor: Editor?, file: PsiFile?): T? {
    val psiElement = getPointingElement(editor, file) ?: return null
    return PsiTreeUtil.getParentOfType(psiElement, T::class.java)
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

fun XmlTag.findFirstDataTag() = findFirstSubTag("data")

fun XmlAttribute.hasDatabindingExpression(): Boolean {
    val value = value ?: return false
    return value.startsWith("@{") && value.endsWith("}")
}

fun XmlAttribute.has2WayDatabindingExpression(): Boolean {
    val value = value ?: return false
    return value.startsWith("@={") && value.endsWith("}")
}

fun collectLayoutVariableTypesOf(psiClass: PsiClass): List<XmlAttribute>? {
    val project = psiClass.project
    val psiManager = PsiManager.getInstance(project)
    return FileTypeIndex.getFiles(XmlFileType.INSTANCE, ProjectScope.getProjectScope(project)).filterNot {
        it.path.contains("/.idea/")
    }.map {
        (psiManager.findFile(it) as? XmlFile)?.rootTag
    }.filterNotNull().filter(XmlTag::isLayoutTag).flatMap {
        it.findSubTags("data").asIterable()
    }.filterNotNull().flatMap {
        it.findSubTags("variable").asIterable()
    }.map {
        it.getAttribute("type")
    }.filterNotNull().filter {
        it.value == psiClass.qualifiedName
    }
}

fun XmlTag.findLastSubTag(tagName: String): XmlTag? {
    val tags = findSubTags(tagName)
    return if (tags.isNotEmpty()) tags[tags.lastIndex] else null
}

