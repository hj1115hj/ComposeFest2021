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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.codelabs.state.ui.StateCodelabTheme

class TodoActivity : AppCompatActivity() {

    val todoViewModel by viewModels<TodoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StateCodelabTheme {
                Surface {
                  TodoActivityScreen(todoViewModel)
                }
            }
        }
    }
}
/* 이 컴포저블은 ViewModel에 저장된 상태와 프로젝트에 이미 정의되어 있는 TodoScreen 컴포저블 사이를 연결합니다.
 viewModel을 직접 사용하도록 TodoScreen을 변경할 수 있지만 그러면 TodoScreen의 재사용성이 조금 떨어집니다.
 list<TodoItem> 과 같은 더 간단한 매개변수를 선호함으로써 TodoScreen은 상태를 끌어올린 특정 위치에 결합되지 않습니다.
 * */
@Composable
private fun TodoActivityScreen(todoViewModel: TodoViewModel){
    //라이브 데이터를 관찰하여 state 객체로 변환
    //ListOf()는 LiveData가 초기화되기 전에 가능한 null 결과를 피하기 위한 초깃값입니다. 전달되지 않으면 items는 null을 허용하는 List<TodoItem>?이 됩니다.
    //by는 Kotlin의 속성 위임 문법이며 이를 통해 자동으로 State<List<TodoItem>>을 observeAsState에서 일반 List<TodoItem>으로 래핑 해제할 수 있습니다.
    //val items: List<TodoItem> by todoViewModel.todoItems.observeAsState(listOf())
    // TodoScreen이 onAddItem이나 onRemoveItem을 호출하면 ViewModel의 올바른 이벤트로 호출을 전달할 수 있습니다,
/*    TodoScreen(
        items = items,
        onAddItem = {  todoViewModel.addItem(it)}, //TodoScreen -> ViewModel (event)
        onRemoveItem = {todoViewModel.removeItem(it)}
    )*/

    TodoScreen(
        items = todoViewModel.todoItems,
        currentlyEditing = todoViewModel.currentEditItem,
        onAddItem =  todoViewModel::addItem, //TodoScreen -> ViewModel (event)
        onRemoveItem = todoViewModel::removeItem,
        onStartEdit = todoViewModel::onEditItemSelected,
        onEditItemChange = todoViewModel::onEditItemChange,
        onEditDone = todoViewModel::onEditDone
    )
}

