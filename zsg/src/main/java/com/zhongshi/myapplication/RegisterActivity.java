package com.zhongshi.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (!checkEmailValid(email)) {
                Toast.makeText(this, getString(R.string.msg_invalid_email), Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_empty_password), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.msg_register_success), Toast.LENGTH_SHORT).show();
                finish(); // 返回登录页
            }
        });
    }

    /**
     * 严格邮箱验证：标准格式 + 必须以 .ac.nz 结尾
     */
    public boolean checkEmailValid(String email) {
        if (email == null) return false;
        String pattern = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.ac\\.nz$";
        return email.matches(pattern);
    }
}