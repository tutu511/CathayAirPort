package com.example.airport.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<B : ViewDataBinding, VM : BaseViewModel> : Fragment() {

    protected lateinit var binding: B
    protected lateinit var viewModel: VM

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 初始化binding
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBase()
        initView()
    }

    private fun initBase() {
        // 获取ViewModel类型
        val modelClass: Class<VM> = getViewModelClass()

        // 初始化viewModel
        viewModel = createViewModel(this, modelClass)

        // viewmodel与view绑定
        binding.setVariable(getViewModelId(), viewModel)

        // 绑定生命周期
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun getViewModelClass(): Class<VM> {
        // 获取带有泛型的父类
        val type = (javaClass.genericSuperclass as ParameterizedType)

        // 获取泛型参数的类型
        @Suppress("UNCHECKED_CAST")
        return type.actualTypeArguments[1] as Class<VM>
    }

    /**
     * 创建ViewModel 如果需要自己定义ViewModel 直接复写此方法
     */
    open fun <T : ViewModel> createViewModel(fragment: Fragment, cls: Class<T>): T {
        return ViewModelProvider(fragment)[cls]
    }
}