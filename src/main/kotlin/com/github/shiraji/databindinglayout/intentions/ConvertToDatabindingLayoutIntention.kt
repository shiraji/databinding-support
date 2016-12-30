package com.github.shiraji.databindinglayout.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.XmlElementFactory

class ConvertToDatabindingLayoutIntention : IntentionAction {

    val whiteList = listOf("manifest", "project", "component", "module")

    override fun getText() = "Convert to databinding layout"
    override fun getFamilyName() = "Convert to databinding layout"
    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val rootTag = file.getRootTag() ?: return false
        if (rootTag.isDatabindingRootTag()) return false
        return !whiteList.contains(rootTag.name)
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val rootTag = file.getRootTag() ?: return
        val xmlnsAttribute = rootTag.attributes
                .filter { it.name.startsWith("xmlns:") }
        val attributeText = xmlnsAttribute
                .map { it.text }
                .joinToString(separator = " ")
        val factory = XmlElementFactory.getInstance(project)

        xmlnsAttribute.forEach { it.delete() }
        val layoutXmlTag = factory.createTagFromText("<layout $attributeText>${rootTag.text}</layout>", XMLLanguage.INSTANCE)
        rootTag.replace(layoutXmlTag)
    }
}