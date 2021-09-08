package com.skyd.skin.core

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.skyd.skin.SkinManager
import com.skyd.skin.core.listeners.ChangeSkinListener

open class SkinBaseFragment : Fragment(), ChangeSkinListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SkinManager.addListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SkinManager.removeListener(this)
    }

    override fun onChangeSkin() {}

}