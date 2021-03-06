package com.example.maybe.videonews.ui.likes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maybe.videonews.R;
import com.example.maybe.videonews.activity.UserManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class LikesFragment extends Fragment implements RegisterFragment.OnRegisterSuccessListener, LoginFragment.OnLoginSuccessListener {

    @BindView(R.id.tvUsername)
    TextView mTvUsername;
    @BindView(R.id.btnRegister)
    Button mBtnRegister;
    @BindView(R.id.btnLogin)
    Button mBtnLogin;
    @BindView(R.id.btnLogout)
    Button mBtnLogout;
    @BindView(R.id.divider)
    View mDivider;
    @BindView(R.id.likesListView)
    LikesListView likesListView;
    private View view;

    private LoginFragment mLoginfragment;
    private RegisterFragment mRegisterfragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null){
            view = inflater.inflate(R.layout.fragment_likes,container,false);
            ButterKnife.bind(this,view);
            //判断用户登录状态，更新UI
            UserManager userManager=UserManager.getInstance();
            if (!userManager.isOffline()){
                userOnLine(userManager.getUsername(),userManager.getObjectId());
            }
        }
        return view;
    }

    @OnClick({R.id.btnRegister,R.id.btnLogin,R.id.btnLogout})
    public void onClick(View view){
        switch (view.getId()){
            //注册
            case R.id.btnRegister:
                if (mRegisterfragment == null){
                    mRegisterfragment = new RegisterFragment();
                    //添加注册成功的监听
                    mRegisterfragment.setListener(this);
                }
                mRegisterfragment.show(getChildFragmentManager(),"Register Dialog");
                break;
            //登录
            case R.id.btnLogin:
                if (mLoginfragment == null){
                    mLoginfragment = new LoginFragment();
                    //添加登录成功的监听
                    mLoginfragment.setListener(this);

                }
                mLoginfragment.show(getChildFragmentManager(),"Login Dialog");
                break;
            //退出登录
            case R.id.btnLogout:
                //用户下线
                userOffline();
                break;
        }
    }



    //用户上线
    private void userOnLine(String username, String objectId) {
        //更新UI
        mBtnLogin.setVisibility(View.INVISIBLE);
        mBtnRegister.setVisibility(View.INVISIBLE);
        mBtnLogout.setVisibility(View.VISIBLE);
        mDivider.setVisibility(View.INVISIBLE);
        mTvUsername.setText(username);
        // 存储用户信息
        UserManager.getInstance().setUsername(username);
        UserManager.getInstance().setObjectId(objectId);
        //刷新收藏列表
        likesListView.autoRefresh();
    }

    //用户下线
    private void userOffline() {
        //清除用户相关信息
        UserManager.getInstance().clear();
        //更新UI
        mBtnLogin.setVisibility(View.VISIBLE);
        mBtnRegister.setVisibility(View.VISIBLE);
        mBtnLogout.setVisibility(View.INVISIBLE);
        mDivider.setVisibility(View.VISIBLE);
        mTvUsername.setText(R.string.tourist);
        //清空收藏列表
        likesListView.clear();
    }

    //添加注册成功的监听
    @Override
    public void registerSuccess(String username, String objectId) {
        //关闭注册的对话框
        mRegisterfragment.dismiss();
        //用户上线
        userOnLine(username, objectId);
    }


    //登录成功
    @Override
    public void loginSuccess(String username, String objectId) {
        mLoginfragment.dismiss();
        //用户上线
        userOnLine(username, objectId);
    }
}
