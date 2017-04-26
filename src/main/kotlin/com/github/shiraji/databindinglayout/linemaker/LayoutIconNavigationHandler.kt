package com.github.shiraji.databindinglayout.linemaker

import com.github.shiraji.databindinglayout.collectLayoutVariableTypesOf
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.xml.XmlTagImpl
import com.intellij.psi.xml.XmlAttribute
import java.awt.event.MouseEvent

class LayoutIconNavigationHandler(val qualifiedName: String) : GutterIconNavigationHandler<PsiElement> {
    override fun navigate(mouseEvent: MouseEvent?, psiElement: PsiElement?) {
        psiElement ?: return
        val types = collectLayoutVariableTypesOf(psiElement.project, qualifiedName)
        if (types == null || types.isEmpty()) return
        when (types.size) {
            0 -> return
            1 -> jumpToLayout(types.first())
            else -> createJumpToLayoutpopup(types, mouseEvent)
        }
    }

    private fun createJumpToLayoutpopup(types: List<XmlAttribute>, mouseEvent: MouseEvent?) {
        val actions = types.map {
            object : AnAction() {
                override fun actionPerformed(p0: AnActionEvent?) {
                    jumpToLayout(it)
                }
            }.apply {
                // did not add text as AnAction's constructor since it remove '_'.
                templatePresentation.setText(it.containingFile.name, false)
                templatePresentation.icon = AllIcons.FileTypes.Xml
            }
        }
        val group = DefaultActionGroup().apply { addAll(actions) }
        JBPopupFactory.getInstance()
                .createActionGroupPopup("Go to layout file", group, SimpleDataContext.getProjectContext(null), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, true).run {
            if (mouseEvent != null) setLocation(mouseEvent.locationOnScreen)
            show(null)
        }
    }

    private fun jumpToLayout(attribute: XmlAttribute) {
        (attribute.parent as? XmlTagImpl)?.navigate(true) ?: attribute.containingFile.navigate(true)
    }

}