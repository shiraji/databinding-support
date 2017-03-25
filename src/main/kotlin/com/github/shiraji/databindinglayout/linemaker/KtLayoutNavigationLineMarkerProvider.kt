package com.github.shiraji.databindinglayout.linemaker

import com.github.shiraji.databindinglayout.collectLayoutVariableTypesOf
import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.intellij.util.containers.isNullOrEmpty
import org.jetbrains.kotlin.psi.KtClass

class KtLayoutNavigationLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val ktClass = element as? KtClass ?: return null
        val qualifiedName = ktClass.fqName.toString()
        if (collectLayoutVariableTypesOf(ktClass.project, qualifiedName).isNullOrEmpty()) return null
        return LineMarkerInfo<PsiElement>(ktClass, ktClass.nameIdentifier!!.textRange, AllIcons.FileTypes.Xml, Pass.UPDATE_ALL, null, LayoutIconNavigationHandler(qualifiedName), GutterIconRenderer.Alignment.LEFT)
    }

    override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
        // not sure what I need to do here...
    }
}