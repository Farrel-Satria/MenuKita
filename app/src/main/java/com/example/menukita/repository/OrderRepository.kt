package com.example.menukita.repository

import com.example.menukita.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderRepository {

    private val database = FirebaseDatabase.getInstance("https://menukita-feb11-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val orderRef = database.getReference("orders")

    fun addOrder(order: Order, onComplete: (Boolean) -> Unit) {
        val id = orderRef.push().key ?: return
        val newOrder = order.copy(id = id)
        orderRef.child(id).setValue(newOrder)
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
    }

    fun getOrdersByUserEmail(userEmail: String, onDataChange: (List<Order>) -> Unit, onError: (String) -> Unit) {
        orderRef.orderByChild("userEmail").equalTo(userEmail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<Order>()
                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        if (order != null) orders.add(order)
                    }
                    onDataChange(orders)
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    fun getAllOrders(onDataChange: (List<Order>) -> Unit, onError: (String) -> Unit) {
        orderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    if (order != null) orders.add(order)
                }
                onDataChange(orders)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }

    fun updateStatus(orderId: String, status: String, onComplete: (Boolean) -> Unit) {
        orderRef.child(orderId).child("status").setValue(status)
            .addOnCompleteListener { task -> onComplete(task.isSuccessful) }
    }
}
