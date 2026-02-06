package com.example.menukita.repository

import com.example.menukita.model.Menu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuRepository {

    private val database = FirebaseDatabase.getInstance("https://menukita-feb11-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val menuRef = database.getReference("menus")

    // CREATE
    fun addMenu(menu: Menu, onComplete: (Boolean) -> Unit) {
        val id = menuRef.push().key ?: return
        val newMenu = menu.copy(id = id)
        menuRef.child(id).setValue(newMenu)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // READ (Realtime Listener)
    fun getMenus(onDataChange: (List<Menu>) -> Unit, onError: (String) -> Unit) {
        menuRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuList = mutableListOf<Menu>()
                for (menuSnapshot in snapshot.children) {
                    val menu = menuSnapshot.getValue(Menu::class.java)
                    menu?.let { menuList.add(it) }
                }
                onDataChange(menuList)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }

    // UPDATE
    fun updateMenu(menu: Menu, onComplete: (Boolean) -> Unit) {
        val id = menu.id ?: return
        menuRef.child(id).setValue(menu)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // DELETE
    fun deleteMenu(id: String, onComplete: (Boolean) -> Unit) {
        menuRef.child(id).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
}
