package com.qxtao.easyenglish.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import org.greenrobot.eventbus.EventBus


abstract class BaseFragment<VB : ViewBinding>(val bindingBlock:(LayoutInflater, ViewGroup?,Boolean) -> VB) : Fragment(), View.OnClickListener {

    private val NON_CODE = -1
    private lateinit var _binding: VB
    private lateinit var _context: Context
    private var _listener: OnFragmentInteractionListener? = null
    protected val binding get() = _binding
    protected val mContext get() = _context
    protected val mListener get() = _listener!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
        _listener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(
                "$context must implement OnFragmentInteractionListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        _listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(data: String?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = bindingBlock(inflater,container,false)
        return _binding.root
    }

    protected open fun onCreateView() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        initViews()
        initEvents()
        addListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this)
        }
    }

    /**
     * is bind eventbus
     *
     * 是否绑定eventbus事件
     */
    protected open fun isBindEventBusHere(): Boolean { return false }

    /**
     * Callback [bindViews] method
     *
     * 回调 [bindViews] 方法
     */
    protected open fun bindViews(){}

    /**
     * Callback [initViews] method
     *
     * 回调 [initViews] 方法
     */
    protected open fun initViews(){}

    /**
     * Callback [initEvents] method
     *
     * 回调 [initEvents] 方法
     */
    protected open fun initEvents(){}

    /**
     * Callback [addListener] method
     *
     * 回调 [addListener] 方法
     */
    protected open fun addListener() {}

    /**
     * startActivity
     *
     * @param clazz target Activity
     */
    protected open fun go(clazz: Class<out Activity?>?) {
        goActivity(clazz, null, NON_CODE, false)
    }

    /**
     * show short toast
     *
     * Toast通知
     */
    protected fun showShortToast(str: String) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show()
    }

    /**
     * show long toast
     *
     * Toast通知
     */
    protected fun showLongToast(str: String) {
        Toast.makeText(mContext, str, Toast.LENGTH_LONG).show()
    }

    /**
     * startActivity with bundle
     *
     * @param clazz target Activity
     */
    protected open fun go(clazz: Class<out Activity?>?, bundle: Bundle?) {
        goActivity(clazz, bundle, NON_CODE, false)
    }

    protected open fun goForResult(clazz: Class<out Activity?>?, requestCode: Int) {
        goActivity(clazz, null, requestCode, false)
    }

    protected open fun goForResult(
        clazz: Class<out Activity?>?,
        bundle: Bundle?,
        requestCode: Int
    ) {
        goActivity(clazz, bundle, requestCode, false)
    }

    /**
     * Activity 跳转
     *
     * @param clazz  目标activity
     * @param bundle 传递参数
     * @param finish 是否结束当前activity
     */
    open fun goActivity(
        clazz: Class<out Activity?>?,
        bundle: Bundle?,
        requestCode: Int,
        finish: Boolean
    ) {
        requireNotNull(clazz) { "you must pass a target activity where to go." }
        val intent = Intent(activity, clazz)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        if (null != bundle) {
            intent.putExtras(bundle)
        }
        if (requestCode > NON_CODE) {
            startActivityForResult(intent, requestCode)
        } else {
            startActivity(intent)
        }
        if (finish) {
            requireActivity().finish()
        }
    }


    /**
     * Block problems caused by quick clicks
     * 屏蔽快速点击而引发的问题
     */
    protected open fun onWidgetClick(view: View?) {}
    private var lastTime = 0L
    private var currentView: View? = null
    override fun onClick(view: View) {
        val nowTime = System.currentTimeMillis()
        if (nowTime - lastTime > 1000L || view.id == currentView!!.id) {
            lastTime = nowTime
            currentView = view
            onWidgetClick(view)
        }
    }

}