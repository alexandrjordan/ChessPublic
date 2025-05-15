package com.jordan.chess.ui.gui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jordan.chess.ui.theme.uiSecondaryColor
import com.jordan.chess.utils.StringKey
import com.jordan.chess.utils.StringResources
import java.time.LocalDate

@Composable
fun AboutSection(
    version: String = "2.1"
) {
    val uriHandler = LocalUriHandler.current
    val year = LocalDate.now().year

    val title = StringResources.getString(StringKey.ABOUT_TITLE)
    val versionText = StringResources.getString(StringKey.ABOUT_VERSION).format(version)
    val projDesc = StringResources.getString(StringKey.ABOUT_PROJECT_DESC)
    val botDesc = StringResources.getString(StringKey.ABOUT_BOT_DESC)
    val repoLabel = StringResources.getString(StringKey.ABOUT_REPO_LABEL)
    val repoUrl = "https://github.com/albertoruibal/karballo"
    val copyrightFmt= StringResources.getString(StringKey.ABOUT_COPYRIGHT)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(25.dp)),
        colors = CardDefaults.cardColors(containerColor = uiSecondaryColor),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "$title · $versionText",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = versionText,
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }

            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = buildString {
                        append(projDesc)
                        append("\n\n")
                        append(botDesc)
                        append("\n\n")
                        append(repoLabel)
                    },
                    fontSize = 14.sp,
                    color = Color.White
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = repoUrl,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline,
                    color = Color.Cyan,
                    modifier = Modifier.clickable {
                        uriHandler.openUri(repoUrl)
                    }
                )

                Spacer(Modifier.height(16.dp))

                val fullCopyright = copyrightFmt.format(year, "Alexandr Jordán")
                val annotated = buildAnnotatedString {
                    val name = "Alexandr Jordán"
                    val parts = fullCopyright.split(name)
                    append(parts[0])
                    pushStringAnnotation(tag = "EMAIL", annotation = "mailto:jordanalexandr4@gmail.com")
                    withStyle(SpanStyle(color = Color.Cyan, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Medium)) {
                        append(name)
                    }
                    pop()
                    if (parts.size > 1) append(parts[1])
                }

                ClickableText(
                    text = annotated,
                    style = TextStyle(fontSize = 14.sp, color = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = { offset ->
                        annotated.getStringAnnotations(tag = "EMAIL", start = offset, end = offset)
                            .firstOrNull()?.let { uriHandler.openUri(it.item) }
                    }
                )
            }
        }
    }
}
