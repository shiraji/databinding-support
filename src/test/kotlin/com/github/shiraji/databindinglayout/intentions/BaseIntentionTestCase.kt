package com.github.shiraji.databindinglayout.intentions

import com.intellij.codeInsight.daemon.LightIntentionActionTestCase
import java.io.File

abstract class BaseIntentionTestCase(val folderName: String) : LightIntentionActionTestCase() {

    override fun getFileSuffix(fileName: String?): String? {
        fileName ?: return null
        if (!fileName.startsWith("before")) return null
        return fileName.substring("before".length)
    }

    override fun getBasePath(): String {
        return "/src/test/resources/intentions/$folderName"
    }

    override fun getTestDataPath(): String {
        //watch out for working directory with which you run this test
        return File("").absolutePath
    }
}