/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelabs.state.todo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelabs.state.util.generateRandomTodoItem
import kotlin.random.Random

/**
 * Stateless component that is responsible for the entire todo screen.
 *
 * @param items (state) list of [TodoItem] to display
 * @param onAddItem (event) request an item be added
 * @param onRemoveItem (event) request an item be removed
 */
@Composable
fun TodoScreen(
    items: List<TodoItem>,
    currentlyEditing: TodoItem?,
    onAddItem: (TodoItem) -> Unit,
    onRemoveItem: (TodoItem) -> Unit,
    onStartEdit: (TodoItem) -> Unit,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit
) {
    Column {
        val enableTopSelection = currentlyEditing == null
        TodoItemInputBackground(elevate = enableTopSelection) {
            if(enableTopSelection)
                TodoItemEntryInput(onItemComplete = onAddItem)
            else{
                Text(
                    "Editing item",
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            items(items = items) { todo ->
               if(currentlyEditing?.id == todo.id){
                   TodoItemInlineEditor(
                       item = currentlyEditing,
                       onEditItemChange = onEditItemChange,
                       onEditDone = { onRemoveItem(todo) }) {
                   }
               }else{
                   TodoRow(
                       todo = todo,
                       onItemClicked = { onStartEdit(it) },
                       modifier = Modifier.fillParentMaxWidth()
                   )
               }
            }
        }

        // For quick testing, a random item generator button
        Button(
            onClick = { onAddItem(generateRandomTodoItem()) },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text("Add random item")
        }
    }
}
@Composable
fun TodoItemInlineEditor(
    item: TodoItem,
    onEditItemChange: (TodoItem) -> Unit,
    onEditDone: () -> Unit,
    onRemoveItem: () -> Unit
) = TodoItemInput(
    text = item.task,
    onTextChange = {onEditItemChange(item.copy(task = it))},
    icon = item.icon,
    onIconChange = {onEditItemChange(item.copy(icon = it))},
    submit = onEditDone,
    iconsVisible = true
){

    Row {
        val shrinkButtons = Modifier.widthIn(20.dp)
        TextButton(onClick = onEditDone, modifier = shrinkButtons) {
            Text(
                text = "\uD83D\uDCBE", // floppy disk
                textAlign = TextAlign.End,
                modifier = Modifier.width(30.dp)
            )
        }
        TextButton(onClick = onRemoveItem, modifier = shrinkButtons) {
            Text(
                text = "❌",
                textAlign = TextAlign.End,
                modifier = Modifier.width(30.dp)
            )
        }
    }

}
/**
 * Stateless composable that displays a full-width [TodoItem].
 *
 * @param todo item to show
 * @param onItemClicked (event) notify caller that the row was clicked
 * @param modifier modifier for this element
 */
@Composable
fun TodoRow(
    todo: TodoItem,
    onItemClicked: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
    iconAlpha : Float = remember(todo.id) { randomTint() }
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked(todo) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(todo.task)
        //val iconAlpha = randomTint()
        /*
        * remember는 구성 가능한 함수에 메모리를 제공합니다.
          remember로 계산된 값은 컴포지션 트리에 저장되고 remember 키가 변경되는 경우에만 다시 계산됩니다.
           private val 속성이 객체에서 실행하는 같은 방식으로 함수에 단일 객체의 저장소를 제공하는 것으로 remember를 생각하면 됩니다.
            처음 구성될 때는 remember가 항상 randomTint를 호출하고 다음 재구성의 결과를 기억합니다. 또한 전달된
            * t-odo.id도 추적합니다. 그러면 새 t-odo.id가 TodoRow에 전달되지 않는 한 재구성 중에 randomTint 호출을 건너뛰고 기억된 값을 반환합니다.
         */

        Icon(
            imageVector = todo.icon.imageVector,
            tint = LocalContentColor.current.copy(alpha = iconAlpha),
            contentDescription = stringResource(id = todo.icon.contentDescription)
        )
    }
}
//composable 함수가 아닌 외부에 의해서 변경되는 값 : 부수효과
//
private fun randomTint(): Float {
    return Random.nextFloat().coerceIn(0.3f, 0.9f)
}

@Preview
@Composable
fun PreviewTodoScreen() {
    val items = listOf(
        TodoItem("Learn compose", TodoIcon.Event),
        TodoItem("Take the codelab"),
        TodoItem("Apply state", TodoIcon.Done),
        TodoItem("Build dynamic UIs", TodoIcon.Square)
    )
    TodoScreen(items, null,{},{}, {}, {},{} )
}

@Preview
@Composable
fun PreviewTodoRow() {
    val todo = remember { generateRandomTodoItem() }
    TodoRow(todo = todo, onItemClicked = {}, modifier = Modifier.fillMaxWidth())
}

//스테이트풀(Stateful) 컴포저블은 시간이 지남에 따라 변경될 수 있는 상태를 소유하는 컴포저블입니다.
/*
이 함수는 remember를 사용하여 메모리를 자체적으로 추가한
 다음 메모리에 mutableStateOf를 저장하여 관찰 가능한 상태 홀더를 제공하는
 내장 Compose 유형인 MutableState<String>을 만듭니다.
 value가 변경되면 이 상태를 읽는 구성 가능한 함수가 자동으로 재구성됩니다.
 */
@Composable
fun TodoInPutTextField(text: String, onTextChange: (String) -> Unit,modifier: Modifier){
    TodoInputText(text, onTextChange, modifier)
}


/*
스테이트리스(Stateless) 컴포저블에는 UI 관련 코드가 모두 있고 스테이트풀(Stateful) 컴포저블에는 UI 관련 코드가 없습니다.
이를 통해 상태를 다른 방식으로 지원하고자 하는 상황에서 UI 코드를 재사용 가능하게 만들 수 있습니다.
 */
@Composable
fun TodoItemEntryInput(onItemComplete: (TodoItem) -> Unit) {
    val (text, setText) = remember { mutableStateOf("") }
    val (icon, setIcon) = remember { mutableStateOf(TodoIcon.Default)}
    val iconsVisible = text.isNotBlank()
    val submit = {
        onItemComplete(TodoItem(text, icon))
        setIcon(TodoIcon.Default)
        setText("")
    }
    TodoItemInput(
        text = text,
        onTextChange = setText,
        icon = icon,
        onIconChange = setIcon,
        submit = submit,
        iconsVisible = iconsVisible){
        TodoEditButton(
            onClick = submit,
            text = "Add",
            enabled = text.isNotBlank()
        )

    }
}


@Composable
fun TodoItemInput(
    text: String,
    onTextChange: (String) -> Unit,
    icon: TodoIcon,
    onIconChange: (TodoIcon) -> Unit,
    submit: () -> Unit,
    iconsVisible: Boolean,
    buttonSlot: @Composable() () -> Unit,
) {
    Column {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            TodoInputText(
                text,
                onTextChange,
                Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                submit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(Modifier.align(Alignment.CenterVertically)) {
                buttonSlot()
            }
        }
        if (iconsVisible) {
            AnimatedIconRow(icon, onIconChange, Modifier.padding(top = 8.dp))
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


