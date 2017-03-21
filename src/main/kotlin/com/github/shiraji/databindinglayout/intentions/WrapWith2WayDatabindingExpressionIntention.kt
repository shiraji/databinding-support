package com.github.shiraji.databindinglayout.intentions

import com.intellij.psi.xml.XmlAttribute

class WrapWith2WayDatabindingExpressionIntention : WrapWithDatabindingIntention() {
    override fun getText() = "Wrap with @={}"
    override fun getTemplate(xmlAttribute: XmlAttribute) = "@={${xmlAttribute.value}}"
}