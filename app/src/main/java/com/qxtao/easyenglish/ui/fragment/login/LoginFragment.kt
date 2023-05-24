package com.qxtao.easyenglish.ui.fragment.login

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.RegexUtils
import com.qxtao.easyenglish.R
import com.qxtao.easyenglish.databinding.FragmentLoginBinding
import com.qxtao.easyenglish.ui.base.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    // define variable
    private var isShowPassword = false
    private lateinit var loginViewModel: LoginViewModel
    // define widget
    private lateinit var btLogin: Button
    private lateinit var tvToRegister: TextView
    private lateinit var tvToForgotPassword: TextView
    private lateinit var ivClearEmail: ImageView
    private lateinit var etEmail: EditText
    private lateinit var rlEmail: RelativeLayout
    private lateinit var ivEyePassword: ImageView
    private lateinit var etPassword: EditText
    private lateinit var cbKeepLogin: CheckBox
    private lateinit var ivQQLogin: ImageView
    private lateinit var ivWeChatLogin: ImageView
    private lateinit var ivWeiboLogin: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun bindViews() {
        btLogin = binding.btLogin
        tvToRegister = binding.tvToRegister
        tvToForgotPassword = binding.tvForgetPsd
        ivClearEmail = binding.ivClearEmail
        etEmail = binding.etEmail
        rlEmail = binding.rlEmail
        ivEyePassword = binding.ivEyePassword
        etPassword = binding.etPassword
        cbKeepLogin = binding.cbKeepLogin
        ivQQLogin = binding.ivQqLogin
        ivWeChatLogin = binding.ivWechatLogin
        ivWeiboLogin = binding.ivWeiboLogin
    }

    override fun initViews() {
        loginViewModel.isShowPassword.observe(viewLifecycleOwner){
            if(it) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivEyePassword.isSelected = true
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivEyePassword.isSelected = false
            }
            etPassword.setSelection(etPassword.text.length)
        }
        loginViewModel.isEmailError.observe(viewLifecycleOwner){
            if(it){
                val drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_error)
                drawable!!.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
                etEmail.setError(mContext.getString(R.string.email_error),drawable)
            } else etEmail.error = null
        }
    }

    override fun initEvents() {
        btLogin.setOnClickListener(this)
        tvToRegister.setOnClickListener(this)
        tvToForgotPassword.setOnClickListener(this)
        ivClearEmail.setOnClickListener(this)
        ivEyePassword.setOnClickListener(this)
        ivQQLogin.setOnClickListener(this)
        ivWeChatLogin.setOnClickListener(this)
        ivWeiboLogin.setOnClickListener(this)
    }

    override fun addListener() {
        etEmail.doOnTextChanged{ charSequence, start, _, _ ->
            if (charSequence.toString().contains(" ")) {
                val str: List<String> = charSequence.toString().split(" ")
                val sb = StringBuffer()
                for (i in str.indices) {
                    sb.append(str[i])
                }
                etEmail.setText(sb.toString())
                etEmail.setSelection(start)
            }
            ivClearEmail.visibility = if (charSequence.isNullOrBlank()) View.GONE else View.VISIBLE
            loginViewModel.isEmailError.value = !RegexUtils.isEmail(charSequence.toString())
            if (charSequence.isNullOrBlank()) loginViewModel.isEmailError.value = false
        }
        etPassword.doOnTextChanged{ charSequence, start, _, _ ->
            if (charSequence.toString().contains(" ")) {
                val str: List<String> = charSequence.toString().split(" ")
                val sb = StringBuffer()
                for (i in str.indices) {
                    sb.append(str[i])
                }
                etPassword.setText(sb.toString())
                etPassword.setSelection(start)
            }
        }
    }

    override fun onClick(view: View) {
        when(view) {
            tvToRegister -> mListener.onFragmentInteraction("toRegisterFragment")
            tvToForgotPassword -> mListener.onFragmentInteraction("toForgotPasswordFragment")
            btLogin -> {
                if(!RegexUtils.isEmail(etEmail.text)){
                    val shake: Animation = AnimationUtils.loadAnimation(activity,R.anim.shake)
                    rlEmail.startAnimation(shake)
                } else {
//                1. 使用第三方登录服务：可以使用第三方登录服务（如微信、QQ、微博等）进行登录，登录成功后，将登录信息存储到本地或服务器端，下次打开应用时，先判断用户是否已经登录，
//                如果已经登录，则直接使用第三方登录服务进行登录。这种方法相对安全，但是需要用户授权，且需要与第三方服务进行交互，实现较为复杂。
//                2. 使用 token 实现登录状态保持：可以在用户登录成功后，生成一个 token，并将 token 存储到本地或服务器端，下次打开应用时，
//                先判断用户是否已经登录，如果已经登录，则使用 token 进行身份验证，以保持登录状态。这种方法相对安全，但是需要服务器端支持，且需要考虑 token 的有效期等问题。
                    mListener.onFragmentInteraction("toMainActivity")
                }
            }
            ivClearEmail -> {
                etEmail.setText(R.string.empty_string)
                etEmail.focusable = View.FOCUSABLE
                etEmail.isFocusableInTouchMode = true
                etEmail.requestFocus()
                val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(etEmail, 0)
            }
            ivEyePassword -> {
                isShowPassword = !isShowPassword
                loginViewModel.isShowPassword.value = isShowPassword
            }
        }
    }
}