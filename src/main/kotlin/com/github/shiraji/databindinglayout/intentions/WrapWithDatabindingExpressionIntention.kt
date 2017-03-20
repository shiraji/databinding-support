package com.github.shiraji.databindinglayout.intentions

import com.intellij.psi.xml.XmlAttribute

class WrapWithDatabindingExpressionIntention : WrapWithDatabindingIntention() {
    override fun getText() = "Wrap with @{}"
    override fun getTemplate(xmlAttribute: XmlAttribute) = "@{${xmlAttribute.value}}"
}