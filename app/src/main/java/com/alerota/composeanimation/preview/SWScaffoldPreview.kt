package com.alerota.composeanimation.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alerota.composeanimation.header.STICKY_ELEMENT_HEIGHT_PX
import com.alerota.composeanimation.header.StoreWallScaffold
import com.alerota.composeanimation.header.TOOLBAR_HEIGHT_PX
import com.alerota.composeanimation.header.TOOLBAR_MARGIN_TOP_PX
import com.alerota.composeanimation.ui.component.Toolbar

@Preview
@Composable
fun SheetPw() {
    StoreWallScaffold(
        toolbar = { modifier ->
            Toolbar(
                modifier = modifier
                    .fillMaxWidth()
                    .height(with(LocalDensity.current) { TOOLBAR_HEIGHT_PX.toDp() })
                    .offset(y = with(LocalDensity.current) { TOOLBAR_MARGIN_TOP_PX.toDp() })
            )
        },
        stickyElement = {
            Box(
                modifier = it
                    .height(with(LocalDensity.current) { STICKY_ELEMENT_HEIGHT_PX.toDp() })
                    .width(350.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color.Green)
            )
        },
        body = { modifier, scrollState ->
            LazyColumn(modifier = modifier, state = scrollState) {
                item { Spacer(modifier = Modifier.height(120.dp)) }

                items(50) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 20.dp)
                            .height(80.dp),
                        backgroundColor = Color.LightGray,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Item $it",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize(),
                            fontSize = 20.sp,
                        )
                    }
                }
            }
        }
    )
}