package io.agora.chat.uikit.conversation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import java.util.List;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseFragment;
import io.agora.chat.uikit.conversation.interfaces.OnConversationChangeListener;
import io.agora.chat.uikit.conversation.interfaces.OnConversationLoadListener;
import io.agora.chat.uikit.conversation.model.EaseConversationInfo;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.menu.EasePopupMenuHelper;
import io.agora.chat.uikit.menu.OnPopupMenuItemClickListener;
import io.agora.chat.uikit.menu.OnPopupMenuPreShowListener;
import io.agora.util.EMLog;

public class EaseConversationListFragment extends EaseBaseFragment implements OnItemClickListener, OnPopupMenuItemClickListener, OnPopupMenuPreShowListener, SwipeRefreshLayout.OnRefreshListener, OnConversationLoadListener, OnConversationChangeListener {
    private static final String TAG = EaseConversationListFragment.class.getSimpleName();
    public LinearLayout llRoot;
    public EaseConversationListLayout conversationListLayout;
    public SwipeRefreshLayout srlRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public int getLayoutId() {
        return R.layout.ease_fragment_conversations;
    }

    public void initView(Bundle savedInstanceState) {
        llRoot = findViewById(R.id.ll_root);
        srlRefresh = findViewById(R.id.srl_refresh);
        conversationListLayout = findViewById(R.id.list_conversation);
        conversationListLayout.init();
    }

    public void initListener() {
        conversationListLayout.setOnItemClickListener(this);
        conversationListLayout.setOnPopupMenuItemClickListener(this);
        conversationListLayout.setOnPopupMenuPreShowListener(this);
        conversationListLayout.setOnConversationLoadListener(this);
        conversationListLayout.setOnConversationChangeListener(this);
        srlRefresh.setOnRefreshListener(this);
    }

    public void initData() {
        conversationListLayout.loadDefaultData();
    }

    /**
     * 会话条目点击事件
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {

    }

    /**
     * 会话长按菜单条目点击事件
     * @param item
     * @param position
     */
    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        EMLog.i(TAG, "click menu position = "+position);
        return false;
    }

    /**
     * 会话长按菜单显示前的监听事件，可以对PopupMenu增加条目{@link EaseConversationListLayout#addItemMenu(int, int, int, String)}，
     * 隐藏或者显示条目{@link EaseConversationListLayout#findItemVisible(int, boolean)}
     * @param menuHelper
     * @param position
     */
    @Override
    public void onMenuPreShow(EasePopupMenuHelper menuHelper, int position) {

    }

    @Override
    public void onRefresh() {
        conversationListLayout.loadDefaultData();
    }

    @Override
    public void loadDataFinish(List<EaseConversationInfo> data) {
        finishRefresh();
    }

    @Override
    public void loadDataFail(String message) {
        finishRefresh();
    }

    /**
     * 停止刷新
     */
    public void finishRefresh() {
        if(!mContext.isFinishing() && srlRefresh != null) {
            runOnUiThread(()->srlRefresh.setRefreshing(false));
        }
    }

    @Override
    public void notifyItemChange(int position) {

    }

    @Override
    public void notifyAllChange() {

    }

    @Override
    public void notifyItemRemove(int position) {

    }
}

