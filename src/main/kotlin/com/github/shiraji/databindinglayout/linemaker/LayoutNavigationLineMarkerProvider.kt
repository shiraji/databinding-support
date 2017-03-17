package com.github.shiraji.databindinglayout.linemaker

import com.github.shiraji.databindinglayout.intentions.isLayoutTag
import com.intellij.codeHighlighting.Pass
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.ide.highlighter.XmlFileType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.source.xml.XmlTagImpl
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.containers.isNullOrEmpty
import java.awt.event.MouseEvent

class LayoutNavigationLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val psiClass = element as? PsiClass ?: return null
        val project = psiClass.project

        val psiManager = PsiManager.getInstance(project)

        val types: List<XmlAttribute>? = FileTypeIndex.getFiles(XmlFileType.INSTANCE, ProjectScope.getProjectScope(project)).filterNot {
            it.path.contains("/.idea/")
        }.map {
            (psiManager.findFile(it) as? XmlFile)?.rootTag
        }.filterNotNull().filter(XmlTag::isLayoutTag).map {
            it.findSubTags("data")
        }.flatMap {
            it.asIterable()
        }.filterNotNull().flatMap {
            it.findSubTags("variable").asIterable()
        }.map {
            it.getAttribute("type")
        }.filterNotNull().filter {
            it.value == psiClass.qualifiedName
        }

        // Smart cast won't work with isNullOrEmpty()
        if (types == null || types.isNullOrEmpty()) return null
        return LineMarkerInfo<PsiElement>(element, element.textRange, AllIcons.FileTypes.Xml, Pass.UPDATE_ALL, null, LayoutIconNavigationHandler(types), GutterIconRenderer.Alignment.LEFT)
    }

    override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
        // not sure what I need to do here...
    }

    private class LayoutIconNavigationHandler(val types: List<XmlAttribute>) : GutterIconNavigationHandler<PsiElement> {
        override fun navigate(mouseEvent: MouseEvent?, psiElement: PsiElement?) {
            when (types.size) {
                0 -> return
                1 -> jumpToLayout(types.first())
                else -> createJumpToLayoutpopup(mouseEvent)
            }
        }

        private fun createJumpToLayoutpopup(mouseEvent: MouseEvent?) {
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
}