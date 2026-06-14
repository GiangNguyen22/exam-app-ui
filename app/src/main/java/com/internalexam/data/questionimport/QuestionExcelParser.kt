package com.internalexam.data.questionimport

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.Locale
import java.util.zip.ZipInputStream

object QuestionExcelParser {
    private val contentHeaders = setOf("content", "question", "questiontext", "noidung", "cauhoi")
    private val subjectIdHeaders = setOf("subjectid", "idmon", "mamon")
    private val subjectNameHeaders = setOf("subject", "subjectname", "monhoc", "tenmon")
    private val topicIdHeaders = setOf("topicid", "idchude", "machude")
    private val topicNameHeaders = setOf("topic", "topicname", "chude", "tenchude")
    private val typeHeaders = setOf("type", "questiontype", "loai")
    private val difficultyHeaders = setOf("difficulty", "level", "dokho")

    fun parse(inputStream: InputStream): QuestionImportParseResult {
        val entries = readZipEntries(inputStream)
        val sharedStrings = entries["xl/sharedStrings.xml"]?.let(::parseSharedStrings).orEmpty()
        val worksheetPath = findFirstWorksheetPath(entries) ?: return QuestionImportParseResult(
            rows = emptyList(),
            failures = listOf(QuestionImportFailure(1, "Excel file does not contain a worksheet."))
        )
        val table = parseWorksheet(entries.getValue(worksheetPath), sharedStrings)
        return parseRows(table)
    }

    private fun parseRows(table: List<List<String>>): QuestionImportParseResult {
        val headerIndex = table.indexOfFirst { row -> row.any { it.isNotBlank() } }
        if (headerIndex < 0) {
            return QuestionImportParseResult(emptyList(), listOf(QuestionImportFailure(1, "Excel file is empty.")))
        }

        val headers = table[headerIndex].map(::normalizeHeader)
        val contentIndex = findHeader(headers, contentHeaders)
        val subjectIdIndex = findHeader(headers, subjectIdHeaders)
        val subjectNameIndex = findHeader(headers, subjectNameHeaders)
        val topicIdIndex = findHeader(headers, topicIdHeaders)
        val topicNameIndex = findHeader(headers, topicNameHeaders)
        val typeIndex = findHeader(headers, typeHeaders)
        val difficultyIndex = findHeader(headers, difficultyHeaders)
        val missingHeaders = buildList {
            if (contentIndex == null) add("content")
            if (subjectIdIndex == null && subjectNameIndex == null) add("subject_id or subject")
            if (typeIndex == null) add("type")
            if (difficultyIndex == null) add("difficulty")
        }
        if (missingHeaders.isNotEmpty()) {
            return QuestionImportParseResult(
                emptyList(),
                listOf(QuestionImportFailure(headerIndex + 1, "Missing required column(s): ${missingHeaders.joinToString(", ")}."))
            )
        }

        val rows = mutableListOf<QuestionImportRow>()
        val failures = mutableListOf<QuestionImportFailure>()
        table.drop(headerIndex + 1).forEachIndexed { offset, row ->
            val rowNumber = headerIndex + offset + 2
            if (row.all { it.isBlank() }) return@forEachIndexed

            val content = row.valueAt(contentIndex).trim()
            val subjectId = row.valueAt(subjectIdIndex).parseLongCell()
            val subjectName = row.valueAt(subjectNameIndex).trim().ifBlank { null }
            val topicId = row.valueAt(topicIdIndex).parseLongCell()
            val topicName = row.valueAt(topicNameIndex).trim().ifBlank { null }
            val type = normalizeQuestionType(row.valueAt(typeIndex))
            val difficulty = normalizeDifficulty(row.valueAt(difficultyIndex))
            val rowFailures = buildList {
                if (content.isBlank()) add("content is required")
                if (subjectId == null && subjectName == null) add("subject_id or subject is required")
                if (type == null) add("type must be SINGLE, MULTI, TRUE_FALSE, or FILL_BLANK")
                if (difficulty == null) add("difficulty must be EASY, MEDIUM, or HARD")
            }

            if (rowFailures.isNotEmpty()) {
                failures += QuestionImportFailure(rowNumber, rowFailures.joinToString("; "))
            } else {
                rows += QuestionImportRow(
                    rowNumber = rowNumber,
                    subjectId = subjectId,
                    subjectName = subjectName,
                    topicId = topicId,
                    topicName = topicName,
                    content = content,
                    type = type.orEmpty(),
                    difficulty = difficulty.orEmpty()
                )
            }
        }

        return QuestionImportParseResult(rows, failures)
    }

    private fun readZipEntries(inputStream: InputStream): Map<String, ByteArray> {
        val entries = mutableMapOf<String, ByteArray>()
        ZipInputStream(inputStream).use { zip ->
            generateSequence { zip.nextEntry }.forEach { entry ->
                if (!entry.isDirectory) {
                    entries[entry.name] = zip.readBytes()
                }
                zip.closeEntry()
            }
        }
        return entries
    }

    private fun findFirstWorksheetPath(entries: Map<String, ByteArray>): String? {
        val workbook = entries["xl/workbook.xml"] ?: return entries.keys
            .filter { it.startsWith("xl/worksheets/sheet") && it.endsWith(".xml") }
            .sorted()
            .firstOrNull()
        val relationshipId = parseFirstSheetRelationshipId(workbook)
        val relationshipTarget = relationshipId?.let { id ->
            entries["xl/_rels/workbook.xml.rels"]?.let { parseWorkbookRelationships(it)[id] }
        }
        return relationshipTarget?.let { target ->
            if (target.startsWith("/")) target.removePrefix("/") else "xl/${target.removePrefix("/")}"
        }?.takeIf { entries.containsKey(it) } ?: entries.keys
            .filter { it.startsWith("xl/worksheets/sheet") && it.endsWith(".xml") }
            .sorted()
            .firstOrNull()
    }

    private fun parseFirstSheetRelationshipId(xml: ByteArray): String? {
        val parser = newParser(xml)
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "sheet") {
                return parser.getAttributeValue("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id")
                    ?: parser.getAttributeValue(null, "r:id")
            }
        }
        return null
    }

    private fun parseWorkbookRelationships(xml: ByteArray): Map<String, String> {
        val relationships = mutableMapOf<String, String>()
        val parser = newParser(xml)
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG && parser.name == "Relationship") {
                val id = parser.getAttributeValue(null, "Id")
                val target = parser.getAttributeValue(null, "Target")
                if (!id.isNullOrBlank() && !target.isNullOrBlank()) {
                    relationships[id] = target
                }
            }
        }
        return relationships
    }

    private fun parseSharedStrings(xml: ByteArray): List<String> {
        val values = mutableListOf<String>()
        val parser = newParser(xml)
        var insideStringItem = false
        var currentValue = StringBuilder()
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            when {
                parser.eventType == XmlPullParser.START_TAG && parser.name == "si" -> {
                    insideStringItem = true
                    currentValue = StringBuilder()
                }
                parser.eventType == XmlPullParser.TEXT && insideStringItem -> currentValue.append(parser.text)
                parser.eventType == XmlPullParser.END_TAG && parser.name == "si" -> {
                    values += currentValue.toString()
                    insideStringItem = false
                }
            }
        }
        return values
    }

    private fun parseWorksheet(xml: ByteArray, sharedStrings: List<String>): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val parser = newParser(xml)
        var currentCells = mutableMapOf<Int, String>()
        var currentColumnIndex = 0
        var currentCellType: String? = null
        var currentCellValue = StringBuilder()
        var insideCell = false
        var insideValue = false

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            when {
                parser.eventType == XmlPullParser.START_TAG && parser.name == "row" -> {
                    currentCells = mutableMapOf()
                }
                parser.eventType == XmlPullParser.START_TAG && parser.name == "c" -> {
                    insideCell = true
                    currentCellValue = StringBuilder()
                    currentCellType = parser.getAttributeValue(null, "t")
                    currentColumnIndex = parser.getAttributeValue(null, "r")?.let(::columnIndexFromCellReference)
                        ?: currentCells.size
                }
                parser.eventType == XmlPullParser.START_TAG && (parser.name == "v" || parser.name == "t") && insideCell -> {
                    insideValue = true
                }
                parser.eventType == XmlPullParser.TEXT && insideValue -> {
                    currentCellValue.append(parser.text)
                }
                parser.eventType == XmlPullParser.END_TAG && (parser.name == "v" || parser.name == "t") -> {
                    insideValue = false
                }
                parser.eventType == XmlPullParser.END_TAG && parser.name == "c" -> {
                    currentCells[currentColumnIndex] = formatCellValue(currentCellValue.toString(), currentCellType, sharedStrings)
                    insideCell = false
                    insideValue = false
                }
                parser.eventType == XmlPullParser.END_TAG && parser.name == "row" -> {
                    val width = (currentCells.keys.maxOrNull() ?: -1) + 1
                    rows += List(width) { index -> currentCells[index].orEmpty() }
                }
            }
        }
        return rows
    }

    private fun formatCellValue(rawValue: String, cellType: String?, sharedStrings: List<String>): String {
        return when (cellType) {
            "s" -> rawValue.toIntOrNull()?.let { sharedStrings.getOrNull(it) }.orEmpty()
            "inlineStr", "str" -> rawValue
            "b" -> if (rawValue == "1") "TRUE" else "FALSE"
            else -> rawValue.removeSuffix(".0")
        }.trim()
    }

    private fun columnIndexFromCellReference(reference: String): Int {
        return reference.takeWhile { it.isLetter() }.uppercase(Locale.US).fold(0) { total, char ->
            total * 26 + (char - 'A' + 1)
        } - 1
    }

    private fun newParser(xml: ByteArray): XmlPullParser {
        return XmlPullParserFactory.newInstance().newPullParser().apply {
            setInput(ByteArrayInputStream(xml), null)
        }
    }

    private fun findHeader(headers: List<String>, acceptedNames: Set<String>): Int? {
        return headers.indexOfFirst { it in acceptedNames }.takeIf { it >= 0 }
    }

    private fun normalizeHeader(value: String): String {
        return normalizeKey(value).replace(" ", "").replace("-", "").replace("_", "")
    }

    private fun normalizeKey(value: String): String {
        return value.trim().lowercase(Locale.US)
            .replace("đ", "d")
            .replace(Regex("[^a-z0-9_ -]"), "")
    }

    private fun normalizeQuestionType(value: String): String? {
        return when (normalizeKey(value).replace(" ", "_").replace("-", "_")) {
            "single", "single_choice", "one_choice", "trac_nghiem", "mot_dap_an" -> "SINGLE"
            "multi", "multiple", "multiple_choice", "nhieu_dap_an" -> "MULTI"
            "true_false", "boolean", "dung_sai" -> "TRUE_FALSE"
            "fill_blank", "fill_in_blank", "dien_khuyet" -> "FILL_BLANK"
            else -> value.trim().uppercase(Locale.US).takeIf { it in setOf("SINGLE", "MULTI", "TRUE_FALSE", "FILL_BLANK") }
        }
    }

    private fun normalizeDifficulty(value: String): String? {
        return when (normalizeKey(value).replace(" ", "_").replace("-", "_")) {
            "easy", "de" -> "EASY"
            "medium", "normal", "trung_binh", "tb" -> "MEDIUM"
            "hard", "kho" -> "HARD"
            else -> value.trim().uppercase(Locale.US).takeIf { it in setOf("EASY", "MEDIUM", "HARD") }
        }
    }

    private fun List<String>.valueAt(index: Int?): String {
        return index?.let { getOrNull(it) }.orEmpty()
    }

    private fun String.parseLongCell(): Long? {
        return trim().removeSuffix(".0").toLongOrNull()
    }
}
