package com.github.shiraji.databindinglayout.linemaker

import com.github.shiraji.databindinglayout.collectLayoutVariableTypesOf
import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.util.containers.isNullOrEmpty

class LayoutNavigationLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val psiClass = element as? PsiClass ?: return null
        val qualifiedName = psiClass.qualifiedName ?: return null
        if (collectLayoutVariableTypesOf(psiClass.project, qualifiedName).isNullOrEmpty()) return null
        return LineMarkerInfo<PsiElement>(psiClass, psiClass.nameIdentifier!!.textRange, AllIcons.FileTypes.Xml, Pass.UPDATE_ALL, null, LayoutIconNavigationHandler(qualifiedName), GutterIconRenderer.Alignment.LEFT)
    }

    override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
        // not sure what I need to do here...
    }
}