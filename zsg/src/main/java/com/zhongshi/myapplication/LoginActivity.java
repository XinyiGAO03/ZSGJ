package com.zhongshi.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;
    TextView tvGuestLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        tvGuestLogin = findViewById(R.id.tv_guest_login);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (!checkEmailValid(email)) {
                showToast(getString(R.string.msg_invalid_email));
            } else if (password.isEmpty()) {
                showToast(getString(R.string.msg_empty_password));
            } else {
                showToast(getString(R.string.msg_login_success));
                goToMainActivity();
            }
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        tvGuestLogin.setOnClickListener(v -> {
            showToast("以游客身份进入");
            goToMainActivity();
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 🔄 替换为严格的正则验证：标准邮箱格式 + 必须以 .ac.nz 结尾
     */
    public boolean checkEmailValid(String email) {
        if (email == null) return false;
        // 正则：允许字母数字和常见符号，必须包含 @ 且域名以 .ac.nz 结尾
        String pattern = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.ac\\.nz$";
        return email.matches(pattern);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

