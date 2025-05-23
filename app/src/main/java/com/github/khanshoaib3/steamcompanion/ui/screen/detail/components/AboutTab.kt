package com.github.khanshoaib3.steamcompanion.ui.screen.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import coil3.compose.AsyncImage
import com.github.khanshoaib3.steamcompanion.ui.screen.detail.GameData

@Composable
fun AboutTab(modifier: Modifier = Modifier, gameData: GameData) {
    val html = gameData.content?.data?.detailedDescription ?: "<b>Empty</b>"
    // Memoize expensive parsing logic (to not recalculate on recomposition)
    val (annotatedString, imageTags) = remember(html) {
        val processedHtml = prepareHtml(html)
        val annotated = AnnotatedString.fromHtml(
            processedHtml,
            linkStyles = TextLinkStyles(style = SpanStyle(color = Color.Blue))
        )
        val images = extractImageTags(annotated.toString())
        annotated to images
    }

    SelectionContainer {
        RenderAnnotatedContent(
            modifier = modifier,
            annotatedString = annotatedString,
            imageTags = imageTags
        )
    }
}

private fun prepareHtml(html: String): String {
    // Regex to replace img tags with a specific pattern that will be used as a placeholder.
    // https://regex101.com/r/ryzCt3/1
    // <img> to [[img:url]]
    val regex = Regex("""<img\s+[^>]*src=["']([^"']+)["'][^>]*>""")
    return regex.replace(html, "[[img:$1]]")
}

@Composable
private fun RenderAnnotatedContent(
    modifier: Modifier,
    annotatedString: AnnotatedString,
    imageTags: List<Triple<Int, Int, String>>,
) {
    var lastIndex = 0

    Column(modifier = modifier) {
        imageTags.forEach { (start, end, url) ->
            // Render text before image
            if (lastIndex < start) {
                val text = annotatedString.subSequence(lastIndex, start)
                Text(text)
            }

            // Render image
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            lastIndex = end
        }

        // Render remaining text after last image
        if (lastIndex < annotatedString.length) {
            val remainingText = annotatedString.subSequence(lastIndex, annotatedString.length)
            if (remainingText.isNotEmpty()) Text(remainingText)
        }
    }
}

private fun extractImageTags(input: String): List<Triple<Int, Int, String>> {
    val regex = Regex("""\[\[img:(.*?)]]""")
    return regex.findAll(input).map { match ->
        val start = match.range.first
        val end = match.range.last + 1 // +1 for correct range
        val url = match.groupValues[1]
        Triple(start, end, url)
    }.toList()
}
