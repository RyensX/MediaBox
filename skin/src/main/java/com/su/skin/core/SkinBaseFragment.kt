package com.su.skin.core

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.su.skin.SkinManager
import com.su.skin.core.listeners.ChangeSkinListener

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