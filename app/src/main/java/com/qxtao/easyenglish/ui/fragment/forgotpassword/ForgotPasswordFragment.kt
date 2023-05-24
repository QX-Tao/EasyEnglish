package com.qxtao.easyenglish.ui.fragment.forgotpassword

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.RegexUtils
import com.qxtao.easyenglish.R
import com.qxtao.easyenglish.databinding.FragmentForgotPasswordBinding
import com.qxtao.easyenglish.ui.base.BaseFragment

class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>(FragmentForgotPasswordBinding::inflate) {
    // define variable
    private var isShowPassword = false
    private var isShowCheckPassword = false
    private lateinit var forgotPasswordViewModel: ForgotPasswordViewModel
    // define widget
    private lateinit var btVerify: Button
    private lateinit var btSetPassword: Button
    private lateinit var rlPassword: RelativeLayout
    private lateinit var rlCheckPassword: RelativeLayout
    private lateinit var rlVerCode: RelativeLayout
    private lateinit var etEmail: EditText
    private lateinit var tvRetypeEmail: TextView
    private lateinit var rlEmail: RelativeLayout
    private lateinit var btVerCode: Button
    private lateinit var etVerCode: EditText
    private lateinit var ivClearEmail: ImageView
    private lateinit var ivCorrectEmail: ImageView
    private lateinit var ivEyePassword: ImageView
    private lateinit var ivEyeCheckPassword: ImageView
    private lateinit var etPassword: EditText
    private lateinit var etCheckPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordViewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
    }

    override fun bindViews() {
        btVerify = binding.btVerify
        btSetPassword = binding.btSetPassword
        rlPassword = binding.rlPassword
        rlCheckPassword = binding.rlCheckPassword
        rlVerCode = binding.rlVercode
        etEmail = binding.etEmail
        tvRetypeEmail = binding.tvRetypeEmail
        rlEmail = binding.rlEmail
        btVerCode = binding.btVercode
        ivClearEmail = binding.ivClearEmail
        ivCorrectEmail = binding.ivCorrectEmail
        ivEyePassword = binding.ivEyePassword
        ivEyeCheckPassword = binding.ivEyeCheckPassword
        etPassword = binding.etPassword
        etVerCode = binding.etVercode
        etCheckPassword = binding.etCheckPassword
    }

    override fun initViews() {
        forgotPasswordViewModel.timeLeft.observe(viewLifecycleOwner) {
            if(it.toInt() == 0){
                btVerCode.text = getString(R.string.send)
                btVerCode.isClickable = true
            } else {
                btVerCode.text = String.format(getString(R.string.wait_time), it.toString())
                btVerCode.isClickable = false
            }
        }
        forgotPasswordViewModel.isShowPassword.observe(viewLifecycleOwner){
            if(it) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivEyePassword.isSelected = true
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivEyePassword.isSelected = false
            }
            etPassword.setSelection(etPassword.text.length)
        }
        forgotPasswordViewModel.isShowCheckPassword.observe(viewLifecycleOwner){
            if(it){
                etCheckPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivEyeCheckPassword.isSelected = true
            } else {
                etCheckPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivEyeCheckPassword.isSelected = false
            }
            etCheckPassword.setSelection(etCheckPassword.text.length)
        }
        forgotPasswordViewModel.isShowSetPassword.observe(viewLifecycleOwner){
            if(it){
                ivClearEmail.visibility = View.GONE
                ivCorrectEmail.visibility = View.VISIBLE
                rlVerCode.visibility = View.GONE
                btVerify.visibility = View.GONE
                rlPassword.visibility = View.VISIBLE
                rlCheckPassword.visibility = View.VISIBLE
                btSetPassword.visibility = View.VISIBLE
                tvRetypeEmail.visibility = View.VISIBLE
                etEmail.background = ContextCompat.getDrawable(mContext, R.drawable.sp_login_edittext_n)
            } else {
                ivClearEmail.visibility = if(etEmail.text.isNullOrBlank()) View.GONE else View.VISIBLE
                ivCorrectEmail.visibility = View.GONE
                rlVerCode.visibility = View.VISIBLE
                btVerify.visibility = View.VISIBLE
                rlPassword.visibility = View.GONE
                rlCheckPassword.visibility = View.GONE
                btSetPassword.visibility = View.GONE
                tvRetypeEmail.visibility = View.GONE
                etEmail.background = ContextCompat.getDrawable(mContext, R.drawable.sp_login_edittext)
            }
        }
        forgotPasswordViewModel.isEmailError.observe(viewLifecycleOwner){
            if(it){
                val drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_error)
                drawable!!.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
                etEmail.setError(mContext.getString(R.string.email_error),drawable)
            } else etEmail.error = null
        }
        forgotPasswordViewModel.isPasswordError.observe(viewLifecycleOwner){
            if(it){
                val drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_error)
                drawable!!.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
                etPassword.setError(mContext.getString(R.string.password_format_error),drawable)
            } else etPassword.error = null
        }
        forgotPasswordViewModel.isCheckPasswordError.observe(viewLifecycleOwner){
            if(it){
                val drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_error)
                drawable!!.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
                etCheckPassword.setError(mContext.getString(R.string.entered_passwords_differ),drawable)
            } else etCheckPassword.error = null
        }
    }

    override fun initEvents() {
        btVerify.setOnClickListener(this)
        btSetPassword.setOnClickListener(this)
        tvRetypeEmail.setOnClickListener(this)
        ivClearEmail.setOnClickListener(this)
        btVerCode.setOnClickListener(this)
        ivEyePassword.setOnClickListener(this)
        ivEyeCheckPassword.setOnClickListener(this)
        btSetPassword.setOnClickListener(this)
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
            forgotPasswordViewModel.isEmailError.value = !RegexUtils.isEmail(charSequence.toString())
            if (charSequence.isNullOrBlank()) forgotPasswordViewModel.isEmailError.value = false
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
            forgotPasswordViewModel.isPasswordError.value = !checkPassword(charSequence.toString())
            if (charSequence.isNullOrBlank()) forgotPasswordViewModel.isPasswordError.value = false
        }
        etCheckPassword.doOnTextChanged{ charSequence, start, _, _ ->
            if (charSequence.toString().contains(" ")) {
                val str: List<String> = charSequence.toString().split(" ")
                val sb = StringBuffer()
                for (i in str.indices) {
                    sb.append(str[i])
                }
                etCheckPassword.setText(sb.toString())
                etCheckPassword.setSelection(start)
            }
            forgotPasswordViewModel.isCheckPasswordError.value = etCheckPassword.text.toString() != etPassword.text.toString()
            if (charSequence.isNullOrBlank()) forgotPasswordViewModel.isCheckPasswordError.value = false
        }
    }

    override fun onClick(view: View) {
        when(view){
            btVerify -> {
                val shake: Animation = AnimationUtils.loadAnimation(activity,R.anim.shake)
                if(!RegexUtils.isEmail(etEmail.text)){
                    rlEmail.startAnimation(shake)
                } else {
                    if(etVerCode.text.isEmpty()){
                        rlVerCode.startAnimation(shake)
                    } else {
                        forgotPasswordViewModel.isShowSetPassword.value = true
                        etEmail.isFocusableInTouchMode = false
                        etPassword.focusable = View.FOCUSABLE
                        etPassword.isFocusableInTouchMode = true
                        etPassword.requestFocus()
                        val manager =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        manager.showSoftInput(etPassword, 0)
                    }
                }
            }
            tvRetypeEmail -> {
                etEmail.isFocusableInTouchMode = true
                etEmail.focusable = View.FOCUSABLE
                etEmail.requestFocus()
                etEmail.setSelection(etEmail.text.length)
                val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(etEmail, 0)
                etVerCode.setText(R.string.empty_string)
                etPassword.setText(R.string.empty_string)
                etCheckPassword.setText(R.string.empty_string)
                forgotPasswordViewModel.isShowPassword.value = false
                forgotPasswordViewModel.isShowCheckPassword.value = false
                forgotPasswordViewModel.isShowSetPassword.value = false
                forgotPasswordViewModel.stopTimer()
            }
            btVerCode -> {
                if(!RegexUtils.isEmail(etEmail.text)){
                    val shake: Animation = AnimationUtils.loadAnimation(activity,R.anim.shake)
                    rlEmail.startAnimation(shake)
                } else {
                    forgotPasswordViewModel.setTimer(60000, 1000)
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
                forgotPasswordViewModel.isShowPassword.value = isShowPassword
            }
            ivEyeCheckPassword -> {
                isShowCheckPassword = !isShowCheckPassword
                forgotPasswordViewModel.isShowCheckPassword.value = isShowCheckPassword
            }
            btSetPassword -> {
                val shake: Animation = AnimationUtils.loadAnimation(activity,R.anim.shake)
                if (!RegexUtils.isEmail(etEmail.text)) {
                    rlEmail.startAnimation(shake)
                } else {
                    if (!checkPassword(etPassword.text.toString())) {
                        rlPassword.startAnimation(shake)
                    } else {
                        if (etCheckPassword.text.toString() != etPassword.text.toString()) {
                            rlCheckPassword.startAnimation(shake)
                        } else {
                            mListener.onFragmentInteraction("setNewPassword")
                        }
                    }
                }
            }
        }
    }

    private fun checkPassword(str: String): Boolean {
        val regex = Regex("^[a-zA-Z\\d-*/+.~!@#\$%^&()]{8,18}\$")
        return str.matches(regex) && !str.matches(Regex("^\\d*\$"))
    }
}