package com.github.khanshoaib3.steamcompanion.ui.screen.detail.components

import android.content.res.Configuration
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.khanshoaib3.steamcompanion.R
import com.github.khanshoaib3.steamcompanion.data.model.detail.Category
import com.github.khanshoaib3.steamcompanion.ui.theme.SteamCompanionTheme

@Composable
fun CategoryChip(modifier: Modifier = Modifier, category: Category) {
    var isOpen by remember { mutableStateOf(false) }
    val view = LocalView.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(true, onClick = {
                isOpen = !isOpen
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            })
            .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
            .background(color = if (isOpen) MaterialTheme.colorScheme.secondary else Color.Unspecified)
            .animateContentSize()
    ) {
        AsyncImage(
            model = "https://steamdb.info/static/img/categories/${category.id}.png",
            contentDescription = category.description,
            placeholder = painterResource(R.drawable.windows_icon),
            modifier = Modifier
                .size(28.dp)
                .padding(start = dimensionResource(R.dimen.padding_very_small))
        )
        AnimatedVisibility(isOpen) {
            Text(
                category.description,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_very_small))
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GameDetailScreenPreview() {
    SteamCompanionTheme {
        CategoryChip(category = Category(id = 2, description = "Single-player"))
    }
}
