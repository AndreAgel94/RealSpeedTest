package com.example.realspeedtest.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.realspeedtest.R

@Composable
fun ProfilePage() {
    Card(
        elevation = 6.dp,
        modifier = Modifier
            .padding(top = 100.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
    ) {
        ConstraintLayout() {
            val (
                image,
                nameText,
                nacionalityText,
                profileInfoRow,
                buttonRow,
            ) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.pitbull),
                contentDescription = "pitbull",
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = androidx.compose.ui.graphics.Color.Red,
                        shape = CircleShape
                    )
                    .constrainAs(image) {
                        top.linkTo(parent.top,16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    },
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Pitbull",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.constrainAs(nameText) {
                    top.linkTo(image.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            Text(text = "Brazil", modifier = Modifier.constrainAs(nacionalityText) {
                top.linkTo(nameText.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .constrainAs(profileInfoRow){
                        top.linkTo(nacionalityText.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
            {
                ProfileBasicInfo(count = "150", title = "Followers")
                ProfileBasicInfo(count = "100", title = "Following")
                ProfileBasicInfo(count = "150", title = "Posts")
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(buttonRow){
                        top.linkTo(profileInfoRow.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    }
            ) {
                Button(onClick = {}) {
                    Text(text = "Follow User")
                }
                Button(onClick = {}) {
                    Text(text = "Direct Message")
                }
            }

        }
    }
}

@Composable
fun ProfileBasicInfo(count: String, title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = count, fontWeight = FontWeight.Bold)
        Text(text = title)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    ProfilePage()
}