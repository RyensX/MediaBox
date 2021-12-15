package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.bean.DataSourceFileBean
import com.skyd.imomoe.bean.ResponseDataType
import com.skyd.imomoe.config.Const.ViewHolderTypeString.Companion.DATA_SOURCE_1
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class ConfigDataSourceViewModel : ViewModel() {
    var dataSourceList: MutableList<DataSourceFileBean> = ArrayList()
    var mldDataSourceList: MutableLiveData<Pair<ResponseDataType, MutableList<DataSourceFileBean>>> =
        MutableLiveData()

    fun resetDataSource() = setDataSource(DataSourceManager.DEFAULT_DATA_SOURCE)

    fun setDataSource(name: String) {
        DataSourceManager.dataSourceName = name
        DataSourceManager.clearCache()
        Util.restartApp()
    }

    fun deleteDataSource(bean: DataSourceFileBean) {
        bean.file.delete()
        getDataSourceList()
    }

    fun getDataSourceList(directoryPath: String = DataSourceManager.getJarDirectory()) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val directory = File(directoryPath)
                if (!directory.isDirectory) {
                    mldDataSourceList.postValue(ResponseDataType.FAILED to ArrayList())
                } else {
                    val jarList = directory.listFiles { _, name ->
                        name.endsWith(".ads", true) ||
                                name.endsWith(".jar", true)
                    }
                    mldDataSourceList.postValue(
                        ResponseDataType.REFRESH to (jarList ?: emptyArray())
                            .map {
                                DataSourceFileBean(
                                    DATA_SOURCE_1, "", it,
                                    it.name == DataSourceManager.dataSourceName
                                )
                            }
                            .toMutableList()
                    )
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
                mldDataSourceList.postValue(ResponseDataType.FAILED to ArrayList())
            }
        }
    }

    companion object {
        const val TAG = "ConfigDataSourceViewModel"
    }
}