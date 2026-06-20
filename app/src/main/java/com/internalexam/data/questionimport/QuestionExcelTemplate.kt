package com.internalexam.data.questionimport

import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object QuestionExcelTemplate {
    fun createBytes(
        subjectNames: List<String> = emptyList(),
        topicNames: List<String> = emptyList()
    ): ByteArray {
        val rows = listOf(
            listOf(
                "QUESTION_KEY",
                "SUBJECT_NAME",
                "TOPIC_NAME",
                "CONTENT",
                "TYPE",
                "DIFFICULTY",
                "ANSWER_CONTENT",
                "ANSWER_CORRECT",
                "ANSWER_EXPLANATION"
            ),
            listOf(
                "Q001",
                subjectNames.firstOrNull().orEmpty(),
                topicNames.firstOrNull().orEmpty(),
                "CPU stands for what?",
                "SINGLE",
                "EASY",
                "Central Processing Unit",
                "TRUE",
                "CPU is the processor that executes instructions."
            ),
            listOf(
                "Q001",
                subjectNames.firstOrNull().orEmpty(),
                topicNames.firstOrNull().orEmpty(),
                "CPU stands for what?",
                "SINGLE",
                "EASY",
                "Computer Power Utility",
                "FALSE",
                ""
            ),
            listOf(
                "Q002",
                subjectNames.firstOrNull().orEmpty(),
                topicNames.firstOrNull().orEmpty(),
                "Wi-Fi allows devices to connect to a network without cables.",
                "TRUE_FALSE",
                "EASY",
                "True",
                "TRUE",
                ""
            ),
            listOf(
                "Q002",
                subjectNames.firstOrNull().orEmpty(),
                topicNames.firstOrNull().orEmpty(),
                "Wi-Fi allows devices to connect to a network without cables.",
                "TRUE_FALSE",
                "EASY",
                "False",
                "FALSE",
                ""
            )
        )
        val listRows = buildListRows(subjectNames, topicNames)
        val output = ByteArrayOutputStream()
        ZipOutputStream(output).use { zip ->
            zip.putText("[Content_Types].xml", contentTypesXml())
            zip.putText("_rels/.rels", rootRelationshipsXml())
            zip.putText("xl/workbook.xml", workbookXml())
            zip.putText("xl/_rels/workbook.xml.rels", workbookRelationshipsXml())
            zip.putText("xl/styles.xml", stylesXml())
            zip.putText("xl/worksheets/sheet1.xml", worksheetXml(rows, dataValidationsXml(listRows)))
            zip.putText("xl/worksheets/sheet2.xml", worksheetXml(listRows, null))
        }
        return output.toByteArray()
    }

    private fun buildListRows(
        subjectNames: List<String>,
        topicNames: List<String>
    ): List<List<String>> {
        val subjects = subjectNames.map { it.trim() }.filter { it.isNotBlank() }.distinct().sorted()
        val topics = topicNames.map { it.trim() }.filter { it.isNotBlank() }.distinct().sorted()
        val typeOptions = listOf("SINGLE", "MULTI", "TRUE_FALSE", "FILL_BLANK")
        val difficultyOptions = listOf("EASY", "MEDIUM", "HARD")
        val maxRows = maxOf(subjects.size, topics.size, typeOptions.size, difficultyOptions.size)
        val rows = mutableListOf(listOf("SUBJECT", "TOPIC", "TYPE", "DIFFICULTY"))
        repeat(maxRows) { index ->
            rows += listOf(
                subjects.getOrNull(index).orEmpty(),
                topics.getOrNull(index).orEmpty(),
                typeOptions.getOrNull(index).orEmpty(),
                difficultyOptions.getOrNull(index).orEmpty()
            )
        }
        return rows
    }

    private fun ZipOutputStream.putText(path: String, text: String) {
        putNextEntry(ZipEntry(path))
        write(text.toByteArray(Charsets.UTF_8))
        closeEntry()
    }

    private fun contentTypesXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/worksheets/sheet2.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>"""
    }

    private fun rootRelationshipsXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>"""
    }

    private fun workbookXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Question Import" sheetId="1" r:id="rId1"/>
    <sheet name="Lists" sheetId="2" state="hidden" r:id="rId2"/>
  </sheets>
</workbook>"""
    }

    private fun workbookRelationshipsXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet2.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""
    }

    private fun stylesXml(): String {
        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="2">
    <font><sz val="11"/><color theme="1"/><name val="Calibri"/></font>
    <font><b/><sz val="11"/><color theme="1"/><name val="Calibri"/></font>
  </fonts>
  <fills count="2">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
  </fills>
  <borders count="1"><border><left/><right/><top/><bottom/><diagonal/></border></borders>
  <cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
  <cellXfs count="2">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
    <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/>
  </cellXfs>
</styleSheet>"""
    }

    private fun worksheetXml(rows: List<List<String>>, dataValidations: String?): String {
        val body = rows.mapIndexed { rowIndex, row ->
            val cells = row.mapIndexed { columnIndex, value ->
                val reference = "${columnName(columnIndex)}${rowIndex + 1}"
                val style = if (rowIndex == 0) """ s="1"""" else ""
                """<c r="$reference"$style t="inlineStr"><is><t>${escapeXml(value)}</t></is></c>"""
            }.joinToString("")
            """<row r="${rowIndex + 1}">$cells</row>"""
        }.joinToString("")

        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <sheetViews><sheetView workbookViewId="0"><pane ySplit="1" topLeftCell="A2" activePane="bottomLeft" state="frozen"/></sheetView></sheetViews>
  <cols>
    <col min="1" max="3" width="18" customWidth="1"/>
    <col min="4" max="4" width="44" customWidth="1"/>
    <col min="5" max="6" width="16" customWidth="1"/>
    <col min="7" max="9" width="28" customWidth="1"/>
  </cols>
  <sheetData>$body</sheetData>
  ${dataValidations.orEmpty()}
</worksheet>"""
    }

    private fun dataValidationsXml(listRows: List<List<String>>): String {
        val subjectCount = listRows.drop(1).count { it.getOrNull(0).orEmpty().isNotBlank() }
        val topicCount = listRows.drop(1).count { it.getOrNull(1).orEmpty().isNotBlank() }
        val validations = buildList {
            if (subjectCount > 0) add(validation("B2:B1000", "'Lists'!\$A\$2:\$A\$${subjectCount + 1}"))
            if (topicCount > 0) add(validation("C2:C1000", "'Lists'!\$B\$2:\$B\$${topicCount + 1}"))
            add(validation("E2:E1000", "'Lists'!\$C\$2:\$C\$5"))
            add(validation("F2:F1000", "'Lists'!\$D\$2:\$D\$4"))
            add(validation("H2:H1000", "\"TRUE,FALSE\""))
        }
        return """<dataValidations count="${validations.size}">${validations.joinToString("")}</dataValidations>"""
    }

    private fun validation(range: String, formula: String): String {
        return """<dataValidation type="list" allowBlank="1" showErrorMessage="1" sqref="$range"><formula1>${escapeXml(formula)}</formula1></dataValidation>"""
    }

    private fun columnName(index: Int): String {
        var value = index + 1
        val builder = StringBuilder()
        while (value > 0) {
            val remainder = (value - 1) % 26
            builder.insert(0, 'A' + remainder)
            value = (value - 1) / 26
        }
        return builder.toString()
    }

    private fun escapeXml(value: String): String {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}
