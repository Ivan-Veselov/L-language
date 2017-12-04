package ru.spbau.mit.bachelors2015.veselov

import org.antlr.v4.runtime.misc.ParseCancellationException
import org.apache.commons.io.FileUtils
import org.junit.Test
import ru.spbau.mit.bachelors2015.veselov.ast.AstFile
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFails


class TestClass {
    @Test
    fun testValid() {
        val sourceRoot = Paths.get(javaClass.getResource("/source/").toURI())
        val fileNames = gatherNames(sourceRoot)

        for (fileName in fileNames) {
            println(fileName)
            checkProgram(fileName)
        }
    }

    @Test
    fun testInvalid() {
        val sourceRoot = Paths.get(javaClass.getResource("/invalid/").toURI())
        val fileNames = gatherNames(sourceRoot)

        for (fileName in fileNames) {
            println(fileName)

            assertFails {
                buildAst(fileToString("/invalid/$fileName.L"))
            }
        }
    }

    private fun gatherNames(sourceRoot: Path) : List<String> {
        val fileNames = ArrayList<String>()

        Files.walkFileTree(sourceRoot, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                if (file.toString().endsWith(".L")) {
                    val fileName = sourceRoot.relativize(file).toString()
                    fileNames.add(fileName.substring(0, fileName.length - 2))
                }

                return FileVisitResult.CONTINUE
            }
        })

        return fileNames
    }

    private fun printAst(ast: AstFile) : String {
        val byteArray = ByteArrayOutputStream()
        PrintStream(byteArray).use {
            ast.traverse(PrettyPrinter(it))
            it.flush()
            return byteArray.toString()
        }
    }

    private fun checkProgram(fileName: String) {
        assertEquals(fileToString("/ast/$fileName"),
                     printAst(buildAst(fileToString("/source/$fileName.L"))))
    }

    private fun fileToString(fileName: String): String {
        val pathToFile = Paths.get(javaClass.getResource(fileName).file)
        return FileUtils.readFileToString(pathToFile.toFile(), null as Charset?)
    }
}