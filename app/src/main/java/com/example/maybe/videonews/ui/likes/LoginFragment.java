package com.example.maybe.videonews.ui.likes;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import com.example.maybe.videonews.R;
import com.example.maybe.videonews.bombapi.BombClient;
import com.example.maybe.videonews.bombapi.UserApi;
import com.example.maybe.videonews.bombapi.result.ErrorResult;
import com.example.maybe.videonews.bombapi.result.UserResult;
import com.example.maybe.videonews.commons.ToastUtils;
import com.google.gson.Gson;

import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends DialogFragment {
    private Unbinder mUnbinder;

    @BindView(R.id.etUsername)
    TextInputEditText mEtUsername;
    @BindView(R.id.etPassword)
    TextInputEditText mEtPassword;
    @BindView(R.id.btnLogin)
    Button mBtnLogin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //取消标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_login, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btnLogin)
    public void onClick(){
        Log.e("okhttp","点击了！");
        final String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        //用户名和密码不能为空
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            ToastUtils.showShort(R.string.username_or_password_can_not_be_null);
            return;
        }

        //显示进度条
        mBtnLogin.setVisibility(View.GONE);

        //登录网络请求
        UserApi userApi = BombClient.getInstance().getUserApi();
        retrofit2.Call<UserResult> call = userApi.login(username,password);
        call.enqueue(new retrofit2.Callback<UserResult>() {
            @Override
            public void onResponse(retrofit2.Call<UserResult> call, retrofit2.Response<UserResult> response) {
                //隐藏进度条
                mBtnLogin.setVisibility(View.VISIBLE);
                //登录未成功
                if (!response.isSuccessful()){
                    try {
                        String error = response.errorBody().string();
                        ErrorResult errorResult = new Gson().fromJson(error,ErrorResult.class);
                        ToastUtils.showShort(errorResult.getError());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                //登录成功
                UserResult result = response.body();
                listener.loginSuccess(username,result.getObjectId());
                ToastUtils.showShort(R.string.login_success);
            }

            @Override
            public void onFailure(retrofit2.Call<UserResult> call, Throwable t) {
                //隐藏进度条
                mBtnLogin.setVisibility(View.VISIBLE);
                ToastUtils.showShort(t.getMessage());
            }
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }



    //当登录成功之后触发的方法
    public interface OnLoginSuccessListener {
        /**
         * 当登录成功时，将来调用
         */
        void loginSuccess(String username, String objectId);
    }

    private OnLoginSuccessListener listener;

    public void setListener(@NonNull OnLoginSuccessListener listener) {
        this.listener = listener;
    }
}
