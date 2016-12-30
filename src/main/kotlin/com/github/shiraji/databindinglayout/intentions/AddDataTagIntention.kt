package com.github.shiraji.databindinglayout.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.XmlElementFactory
import com.intellij.psi.xml.XmlFile

class AddDataTagIntention : IntentionAction {
    override fun getFamilyName() = "Add <data> tag"
    override fun getText() = "Add <data> tag"

    override fun startInWriteAction() = true

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        val rootTag = file.getRootTag() ?: return false
        return rootTag.isDatabindingRootTag() && rootTag.findSubTags("data").isEmpty()
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (file !is XmlFile) return
        val newTag = file.rootTag?.addSubTag(XmlElementFactory.getInstance(project).createTagFromText("<data></data>", XMLLanguage.INSTANCE), true)
        newTag?.value?.textRange?.startOffset?.let { editor?.caretModel?.moveToOffset(it) }
    }
}