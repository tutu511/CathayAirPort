package com.example.airport.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected lateinit var binding: B
    private lateinit var viewModel: VM

    /**
     * 返回layout
     */
    abstract fun getLayoutId(): Int

    /**
     * 返回vm id，通过package.BR获取
     */
    abstract fun getViewModelId(): Int

    /**
     * 执行控件与业务逻辑
     */
    abstract fun initView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase()
        setContentView(binding.root)
        initView()
    }

    private fun initBase() {
        // 初始化binding
        binding = DataBindingUtil.setContentView(this, getLayoutId())

        // 获取ViewModel类型
        val modelClass: Class<VM> = getViewModelClass()

        // 初始化viewModel
        viewModel = createViewModel(this, modelClass)

        // viewmodel与view绑定
        binding.setVariable(getViewModelId(), viewModel)

        // 绑定生命周期
        binding.lifecycleOwner = this
    }

    private fun getViewModelClass(): Class<VM> {
        // 获取带有泛型的父类
        val type = (javaClass.genericSuperclass as ParameterizedType)

        // 获取泛型参数的类型
        @Suppress("UNCHECKED_CAST")
        return type.actualTypeArguments[1] as Class<VM>
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    /**
     * 创建ViewModel 如果需要自己定义ViewModel 直接复写此方法
     */
    open fun <T : ViewModel> createViewModel(activity: FragmentActivity, cls: Class<T>): T {
        return ViewModelProvider(activity)[cls]
    }
}
