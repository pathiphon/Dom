package com.adedom.library.extension

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adedom.library.util.ItemDecoration

//mRecyclerView.recyclerVertical { it.adapter = mAdapter }
fun RecyclerView.recyclerVertical(isStackEnd: Boolean = false, rv: (RecyclerView) -> Unit) {
    this.apply {
        val lm = LinearLayoutManager(context)
        lm.stackFromEnd = isStackEnd
        layoutManager = lm
        setHasFixedSize(true)
        rv.invoke(this)
    }
}

//mRecyclerView.recyclerGrid { it.adapter = mAdapter }
fun RecyclerView.recyclerGrid(rv: (RecyclerView) -> Unit) {
    this.apply {
        layoutManager = GridLayoutManager(context, 2)
        addItemDecoration(
            ItemDecoration(
                2,
                ItemDecoration.dpToPx(10, resources),
                true
            )
        )
        rv.invoke(this)
    }
}
