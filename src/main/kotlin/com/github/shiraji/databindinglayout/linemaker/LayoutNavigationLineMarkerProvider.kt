package com.github.shiraji.databindinglayout.linemaker

import com.intellij.codeHighlighting.Pass.UPDATE_ALL
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.editor.markup.GutterIconRenderer.Alignment.LEFT
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlFile

class LayoutNavigationLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val psiClass = element as? PsiClass ?: return null
        val project = psiClass.project

        val psiManager = PsiManager.getInstance(project)

        val types: List<XmlAttribute>? = FileTypeIndex.getFiles(XmlFileType.INSTANCE, ProjectScope.getProjectScope(project)).map {
            (psiManager.findFile(it) as? XmlFile)?.rootTag
        }.filterNotNull().filter {
            it.name == "layout"
        }.map {
            it.findSubTags("variable")
        }.filterNotNull().flatMap {
            it.asIterable()
        }.map {
            it.getAttribute("type")
        }.filterNotNull().filter {
            it.value == psiClass.qualifiedName
        }.filterNotNull()

        types ?: return null

        return LineMarkerInfo<PsiElement>(element, element.textRange, AllIcons.Actions.Checked, UPDATE_ALL, null, null, LEFT)
    }

    override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
        // not sure what I need to do here...
    }
}
