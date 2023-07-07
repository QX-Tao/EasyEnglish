package com.qxtao.easyenglish.ui.fragment.register

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ScreenUtils
import com.qxtao.easyenglish.R
import com.qxtao.easyenglish.databinding.FragmentRegisterBinding
import com.qxtao.easyenglish.ui.activity.WelcomeActivity
import com.qxtao.easyenglish.ui.base.BaseFragment
import java.util.*


class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    // define variable
    private var isShowPassword = false
    private var isShowCheckPassword = false
    private lateinit var registerViewModel: RegisterViewModel
    // define widget
    private lateinit var rlEmail: RelativeLayout
    private lateinit var btVerCode: Button
    private lateinit var ivClearEmail: ImageView
    private lateinit var etEmail: EditText
    private lateinit var ivEyePassword: ImageView
    private lateinit var ivEyeCheckPassword: ImageView
    private lateinit var etPassword: EditText
    private lateinit var etCheckPassword: EditText
    private lateinit var btRegister: Button
    private lateinit var etVerCode: EditText
    private lateinit var rlVerCode: RelativeLayout
    private lateinit var rlPassword: RelativeLayout
    private lateinit var rlCheckPassword: RelativeLayout
    private lateinit var cbUserAgreement: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
    }

    override fun bindViews() {
        rlEmail = binding.rlEmail
        btVerCode = binding.btVercode
        ivClearEmail = binding.ivClearEmail
        etEmail = binding.etEmail
        ivEyePassword = binding.ivEyePassword
        ivEyeCheckPassword = binding.ivEyeCheckPassword
        etPassword = binding.etPassword
        etCheckPassword = binding.etCheckPassword
        btRegister = binding.btRegister
        etVerCode = binding.etVercode
        rlVerCode = binding.rlVercode
        rlPassword = binding.rlPassword
        rlCheckPassword = binding.rlCheckPassword
        cbUserAgreement = binding.cbUserAgreement
    }

    override fun initViews() {
        registerViewModel.timeLeft.observe(viewLifecycleOwner) {
            if(it.toInt() == 0){
                btVerCode.text = getString(R.string.send)
                btVerCode.isClickable = true
            } else {
                btVerCode.text = String.format(getString(R.string.wait_time), it.toString())
                btVerCode.isClickable = false
            }
        }
        registerViewModel.isShowPassword.observe(viewLifecycleOwner){
            if(it) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivEyePassword.isSelected = true
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivEyePassword.isSelected = false
            }
            etPassword.setSelection(etPassword.text.length)
        }
        registerViewModel.isShowCheckPassword.observe(viewLifecycleOwner){
            if(it){
                etCheckPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivEyeCheckPassword.isSelected = true
            } else {
                etCheckPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                ivEyeCheckPassword.isSelected = false
            }
            etCheckPassword.setSelection(etCheckPassword.text.length)
        }
        registerViewModel.isEmailError.observe(viewLifecycleOwner){
            if(it){
                val drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_error)
                drawable!!.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
                etEmail.setError(mContext.getString(R.string.email_error),drawable)
            } else etEmail.error = null
        }
        registerViewModel.isPasswordError.observe(viewLifecycleOwner){
            if(it){
                val drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_error)
                drawable!!.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
                etPassword.setError(mContext.getString(R.string.password_format_error),drawable)
            } else etPassword.error = null
        }
        registerViewModel.isCheckPasswordError.observe(viewLifecycleOwner){
            if(it){
                val drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_error)
                drawable!!.setBounds(0,0,drawable.intrinsicWidth,drawable.intrinsicHeight)
                etCheckPassword.setError(mContext.getString(R.string.entered_passwords_differ),drawable)
            } else etCheckPassword.error = null
        }
    }

    override fun initEvents() {
        ivClearEmail.setOnClickListener(this)
        btVerCode.setOnClickListener(this)
        ivEyePassword.setOnClickListener(this)
        ivEyeCheckPassword.setOnClickListener(this)
        btRegister.setOnClickListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun addListener() {
        etEmail.doOnTextChanged { charSequence, start, _, _ ->
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
            registerViewModel.isEmailError.value = !RegexUtils.isEmail(charSequence.toString())
            if (charSequence.isNullOrBlank()) registerViewModel.isEmailError.value = false
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
            registerViewModel.isPasswordError.value = !checkPassword(charSequence.toString())
            if (charSequence.isNullOrBlank()) registerViewModel.isPasswordError.value = false
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
            registerViewModel.isCheckPasswordError.value = etCheckPassword.text.toString() != etPassword.text.toString()
            if (charSequence.isNullOrBlank()) registerViewModel.isCheckPasswordError.value = false
        }
        cbUserAgreement.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if(!cbUserAgreement.isChecked) showUserAgreementDialog()
                    else v.performClick()
                }
            }
            true
        }
    }

    override fun onClick(view: View) {
        when(view){
            ivClearEmail -> {
                etEmail.setText(R.string.empty_string)
                etEmail.focusable = View.FOCUSABLE
                etEmail.isFocusableInTouchMode = true
                etEmail.requestFocus()
                val manager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(etEmail, 0)
            }
            btVerCode -> {
                if(!RegexUtils.isEmail(etEmail.text)){
                    val shake: Animation = AnimationUtils.loadAnimation(activity,R.anim.shake)
                    rlEmail.startAnimation(shake)
                } else {
                    registerViewModel.setTimer(60000, 1000)
                    mListener.onFragmentInteraction("getVerCode")
                    // todo
                }
            }
            ivEyePassword -> {
                isShowPassword = !isShowPassword
                registerViewModel.isShowPassword.value = isShowPassword
            }
            ivEyeCheckPassword -> {
                isShowCheckPassword = !isShowCheckPassword
                registerViewModel.isShowCheckPassword.value = isShowCheckPassword
            }
            btRegister -> {
                val shake: Animation = AnimationUtils.loadAnimation(activity,R.anim.shake)
                if (!cbUserAgreement.isChecked) {
                    showUserAgreementDialog()
                } else {
                    if (!RegexUtils.isEmail(etEmail.text)) {
                        rlEmail.startAnimation(shake)
                    } else {
                        if (etVerCode.text.isEmpty()) {
                            rlVerCode.startAnimation(shake)
                        } else {
                            if (!checkPassword(etPassword.text.toString())) {
                                rlPassword.startAnimation(shake)
                            } else {
                                if (etCheckPassword.text.toString() != etPassword.text.toString()) {
                                    rlCheckPassword.startAnimation(shake)
                                } else {
                                    mListener.onFragmentInteraction("onRegister")
                                    // todo
                                }
                            }
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
    private fun showUserAgreementDialog(){
        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.layout_user_agreement_dialog, null)
        val webView = dialogView.findViewById<WebView>(R.id.wv_user_agreement)
        webView.webViewClient = WebViewClient()
        webView.setBackgroundColor(mContext.getColor(R.color.colorThemeBackground))
        webView.loadUrl(
            when(LanguageUtils.getAppContextLanguage()){
                Locale.TRADITIONAL_CHINESE -> "file:///android_asset/zh_TW/UserAgreement.html"
                Locale.SIMPLIFIED_CHINESE -> "file:///android_asset/zh_CN/UserAgreement.html"
                else -> "file:///android_asset/en/UserAgreement.html"
            }
        )
        val dialog = AlertDialog.Builder(mContext)
            .setTitle(getString(R.string.user_agreement))
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton(mContext.getString(R.string.agree)){ _, _ -> cbUserAgreement.isChecked = true}
            .setNegativeButton(mContext.getString(R.string.disagree)){ _, _ -> cbUserAgreement.isChecked = false }
            .create()
        dialog.show()
        if(dialog.window != null){
            val screenHeight = ScreenUtils.getScreenHeight()
            val lp: WindowManager.LayoutParams = dialog.window!!.attributes
            lp.height = (0.7 * screenHeight).toInt()
            dialog.window!!.attributes = lp
        }
    }
}