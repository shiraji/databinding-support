package com.github.shiraji.databindinglayout

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.XmlElementFactory
import com.intellij.psi.xml.XmlFile

class ConvertToDatabindingLayoutIntention : IntentionAction {
    override fun getText() = "Convert to databinding layout"
    override fun getFamilyName() = "Convert to databinding layout"
    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (file !is XmlFile) return false
        val rootTag = file.rootTag ?: return false
        return rootTag.name != "layout"
                && rootTag.getAttribute("xmlns:android") != null
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is XmlFile) return
        val rootTag = file.rootTag ?: return
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