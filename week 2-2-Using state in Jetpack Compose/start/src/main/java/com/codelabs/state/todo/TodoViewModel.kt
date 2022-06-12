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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TodoViewModel : ViewModel() {
 /*   private var _todoItems = MutableLiveData(listOf<TodoItem>())
    val todoItems: LiveData<List<TodoItem>> = _todoItems

    fun addItem(item: TodoItem) {
        _todoItems.value = _todoItems.value!! + listOf(item)
    }

    fun removeItem(item: TodoItem) {
        _todoItems.value = _todoItems.value!!.toMutableList().also {
            it.remove(item)
        }
    }*/
    /*
    mutableStateListOf를 사용하면 관찰 가능한 MutableList의 인스턴스를 만들 수 있습니다. 즉, MutableList와 동일한 방식으로 todoItems를 사용할 수 있어 LiveData<List> 사용의 오버헤드가 삭제됩니다.
    todoItems 선언은 짧으며 LiveData 버전과 동일한 동작을 캡처합니다.
    mutableStateListOf 및 MutableState로 실행된 작업은 Compose용입니다.
    이 ViewModel을 뷰 시스템에서도 사용한 경우 LiveData를 계속 사용하는 것이 좋습니다.
     */

    //private state
    private var currentEditPosition by mutableStateOf(-1)

    var todoItems = mutableStateListOf<TodoItem>()
        private set

    //state
    /*
    컴포저블이 currentEditItem을 호출할 때마다 todoItems 및 currentEditPosition의 변경사항을 모두 관찰합니다. 둘 중 하나가 변경되면 컴포저블은 getter를 다시 호출하여 새 값을 가져옵니다.
     onEditItemSelected 및 onEditDone 이벤트는 currentEditPosition을 변경하기만 합니다.
     currentEditPosition을 변경하면 Compose는 currentEditItem을 읽는 모든 컴포저블을 재구성합니다.
     onEditItemChange 이벤트는 currentEditPosition에서 목록을 업데이트합니다.
     그러면 currentEditItem 및 todoItems에서 반환된 값이 모두 동시에 변경됩니다.
     그 전에 호출자가 잘못된 항목을 쓰려고 하지 않는지 확인하는 안전 확인이 있습니다.
     State<T> 변환이 작동하려면 State<T> 객체에서 상태를 읽어야 합니다.
      currentEditPosition을 일반 Int(private var currentEditPosition = -1)로 정의했다면 Compose는 변경사항을 관찰할 수 없습니다.
     */
    val currentEditItem: TodoItem?
        get() = todoItems.getOrNull(currentEditPosition)

    fun addItem(item: TodoItem) {
        todoItems.add(item)
    }
    fun removeItem(item: TodoItem) {
        todoItems.remove(item)
        onEditDone()
    }

    //event: onEditItemSelected

    fun onEditItemSelected(item: TodoItem){
        currentEditPosition = todoItems.indexOf(item) //// don't keep the editor open when removing items
    }


    //event: onEditDone
    fun onEditDone(){
        currentEditPosition = -1
    }

    //event: onEditItemChange
    fun onEditItemChange(item: TodoItem){
        val currentItem = requireNotNull(currentEditItem)
        require(currentItem.id == item.id){
            "You can only change an item with the same id as currentEditItem"
        }
        todoItems[currentEditPosition] = item
    }
}
