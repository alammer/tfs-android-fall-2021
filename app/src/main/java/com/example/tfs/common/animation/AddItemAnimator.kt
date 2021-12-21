package com.example.tfs.common.animation

import android.view.animation.Interpolator
import androidx.recyclerview.widget.RecyclerView
import com.example.tfs.common.animation.custom.CommonItemAnimator

class AddItemAnimator : BaseItemAnimator {
    private var defaultAnimator: CommonItemAnimator
    private val animationMap: MutableMap<Int, CommonItemAnimator> = mutableMapOf()

    constructor(defaultAnimator: CommonItemAnimator) {
        this.defaultAnimator = defaultAnimator
    }

    constructor(defaultAnimator: CommonItemAnimator, interpolator: Interpolator) {
        this.defaultAnimator = defaultAnimator
        this.interpolator = interpolator
    }

    override fun animateRemoveImpl(holder: RecyclerView.ViewHolder) {
        val animator = animationMap.getOrElse(holder.itemViewType, { defaultAnimator })
        holder.itemView.animate().apply {
            duration = removeDuration
            interpolator = this@AddItemAnimator.interpolator
            setListener(DefaultRemoveAnimatorListener(holder))
            startDelay = getRemoveDelay(holder)

            animator.animateRemove(holder)
        }.start()

    }

    override fun animateAddImpl(holder: RecyclerView.ViewHolder) {
        val animator = animationMap.getOrElse(holder.itemViewType, { defaultAnimator })
        holder.itemView.animate().apply {
            duration = addDuration
            interpolator = this@AddItemAnimator.interpolator
            setListener(DefaultAddAnimatorListener(holder))
            startDelay = getAddDelay(holder)

            animator.animateAdd(holder)
        }.start()
    }

    override fun preAnimateRemoveImpl(holder: RecyclerView.ViewHolder) {
        val animator = animationMap.getOrElse(holder.itemViewType, { defaultAnimator })
        animator.preAnimateRemove(holder)
    }

    override fun preAnimateAddImpl(holder: RecyclerView.ViewHolder) {
        val animator = animationMap.getOrElse(holder.itemViewType, { defaultAnimator })
        animator.preAnimateAdd(holder)
    }

    fun addViewTypeAnimation(viewType: Int, animator: CommonItemAnimator) {
        animationMap[viewType] = animator
    }

}