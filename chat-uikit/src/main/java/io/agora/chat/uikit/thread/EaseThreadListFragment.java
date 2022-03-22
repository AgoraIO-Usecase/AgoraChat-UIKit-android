package io.agora.chat.uikit.thread;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.Map;

import io.agora.chat.ChatClient;
import io.agora.chat.ChatMessage;
import io.agora.chat.ChatThread;
import io.agora.chat.CursorResult;
import io.agora.chat.Group;
import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseFragment;
import io.agora.chat.uikit.databinding.EaseFragmentThreadListBinding;
import io.agora.chat.uikit.interfaces.OnTitleBarFinishInflateListener;
import io.agora.chat.uikit.thread.interfaces.OnItemThreadClickListener;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.thread.adapter.EaseThreadListAdapter;
import io.agora.chat.uikit.thread.presenter.EaseThreadListPresenter;
import io.agora.chat.uikit.thread.presenter.EaseThreadListPresenterImpl;
import io.agora.chat.uikit.thread.presenter.IThreadListView;
import io.agora.chat.uikit.widget.EaseDragRecyclerView;
import io.agora.chat.uikit.widget.EaseTitleBar;

public class EaseThreadListFragment extends EaseBaseFragment implements IThreadListView {
    private EaseTitleBar.OnBackPressListener backPressListener;
    private EaseFragmentThreadListBinding binding;
    private String parentId;
    private EaseThreadListAdapter mAdapter;
    private EaseThreadListPresenter presenter;
    private int limit = 10;
    private String cursor = "";
    private boolean isGroupAdmin;
    private Group mGroup;
    private OnLoadResultListener resultListener;
    private OnItemThreadClickListener itemClickListener;
    private OnTitleBarFinishInflateListener inflateListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        binding = EaseFragmentThreadListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
        initData();
    }

    public void initArguments() {
        Bundle bundle = getArguments();
        if(bundle != null) {
            parentId = bundle.getString(Constant.KEY_PARENT_ID, "");
        }
    }

    public void initView() {
        if(presenter == null) {
            presenter = new EaseThreadListPresenterImpl();
        }
        if(mContext instanceof AppCompatActivity) {
            ((AppCompatActivity) mContext).getLifecycle().addObserver(presenter);
        }
        presenter.attachView(this);

        Bundle bundle = getArguments();
        if(bundle != null) {
            boolean useHeader = bundle.getBoolean(Constant.KEY_USE_TITLE, false);
            binding.titleBar.setVisibility(useHeader ? View.VISIBLE : View.GONE);
            if(useHeader) {
                String title = bundle.getString(Constant.KEY_SET_TITLE, "");
                if(!TextUtils.isEmpty(title)) {
                    binding.titleBar.setTitle(title);
                }

                String subTitle = bundle.getString(Constant.KEY_SET_SUB_TITLE, "");
                if(!TextUtils.isEmpty(subTitle)) {
                    binding.titleBar.setSubTitle(subTitle);
                    binding.titleBar.getSubTitle().setVisibility(View.VISIBLE);
                }

                boolean canBack = bundle.getBoolean(Constant.KEY_ENABLE_BACK, false);
                binding.titleBar.setDisplayHomeAsUpEnabled(canBack);

                binding.titleBar.setOnBackPressListener(backPressListener != null ? backPressListener : new EaseTitleBar.OnBackPressListener() {
                    @Override
                    public void onBackPress(View view) {
                        mContext.onBackPressed();
                    }
                });
            }
            limit = bundle.getInt(Constant.KEY_REQUEST_LIMIT, 10);
        }
        setListView();
        if(inflateListener != null) {
            inflateListener.onTitleBarFinishInflate(binding.titleBar);
        }
    }

    public void setListView() {
        binding.rvList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new EaseThreadListAdapter();
        binding.rvList.setAdapter(mAdapter);
    }

    public void initListener() {
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(itemClickListener != null) {
                    ChatThread item = mAdapter.getItem(position);
                    Map<String, ChatMessage> messageMap = mAdapter.getLatestMessages();
                    String messageId = null;
                    if(messageMap != null && messageMap.containsKey(item.getThreadId())) {
                        messageId = messageMap.get(item.getThreadId()).getMsgId();
                    }
                    itemClickListener.onItemClick(view, item, messageId);
                }
            }
        });
        binding.srlRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EaseThreadListFragment.this.onRefresh();
            }
        });
        binding.rvList.setOnLoadMoreListener(new EaseDragRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                EaseThreadListFragment.this.onLoadMore();
            }
        });
    }

    public void initData() {
        if(TextUtils.isEmpty(parentId)) {
            mContext.finish();
            return;
        }
        presenter.getThreadParent(parentId);
    }

    private boolean isTitleEmpty(TextView textView) {
        if(textView == null) {
            return true;
        }
        String content = textView.getText().toString().trim();
        return TextUtils.isEmpty(content);
    }

    public void onLoadMore() {
        if(isGroupAdmin) {
            presenter.getMoreThreadList(mGroup.getGroupId(), limit, cursor);
        }else {
            presenter.getMoreJoinedThreadList(mGroup.getGroupId(), limit, cursor);
        }
    }

    public void onRefresh() {
        cursor = "";
        if(isGroupAdmin) {
            presenter.getThreadList(mGroup.getGroupId(), limit, cursor);
        }else {
            presenter.getJoinedThreadList(mGroup.getGroupId(), limit, cursor);
        }
    }

    private void setHeaderBackPressListener(EaseTitleBar.OnBackPressListener backPressListener) {
        this.backPressListener = backPressListener;
    }

    private void setCustomPresenter(EaseThreadListPresenter presenter) {
        this.presenter = presenter;
    }

    private void finishRefresh() {
        mContext.runOnUiThread(() -> {
            if(binding.srlRefresh != null) {
                binding.srlRefresh.setEnabled(false);
            }
        });
    }

    private void finishLoadMore() {
        mContext.runOnUiThread(()-> {
            if(binding.rvList != null) {
                binding.rvList.enableLoadMore(false);
            }
        });
    }

    private void setOnLoadResultListener(OnLoadResultListener listener) {
        this.resultListener = listener;
    }

    private void setOnItemClickListener(OnItemThreadClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnTitleBarFinishInflateListener(OnTitleBarFinishInflateListener inflateListener) {
        this.inflateListener = inflateListener;
    }

    @Override
    public Context context() {
        return mContext;
    }

    @Override
    public void getJoinedThreadListSuccess(CursorResult<ChatThread> result) {
        finishRefresh();
        cursor = result.getCursor();
        mAdapter.setData(result.getData());
        if(resultListener != null) {
            resultListener.onLoadFinish();
        }
    }

    @Override
    public void getNoJoinedThreadListData() {
        finishRefresh();
        if(resultListener != null) {
            resultListener.onLoadFinish();
        }
    }

    @Override
    public void getJoinedThreadListFail(int code, String message) {
        finishRefresh();
    }

    @Override
    public void getMoreJoinedThreadListSuccess(CursorResult<ChatThread> result) {
        finishLoadMore();
        cursor = result.getCursor();
        mAdapter.addData(result.getData());
        if(resultListener != null) {
            resultListener.onLoadFinish();
        }
    }

    @Override
    public void getNoMoreJoinedThreadList() {
        finishLoadMore();
        if(resultListener != null) {
            resultListener.onLoadFinish();
        }
    }

    @Override
    public void getMoreJoinedThreadListFail(int code, String message) {
        finishLoadMore();
        if(resultListener != null) {
            resultListener.onLoadFail(code, message);
        }
    }

    @Override
    public void getThreadListSuccess(CursorResult<ChatThread> result) {
        finishRefresh();
        cursor = result.getCursor();
        mAdapter.setData(result.getData());
        if(resultListener != null) {
            resultListener.onLoadFinish();
        }
    }

    @Override
    public void getNoThreadListData() {
        finishRefresh();
        if(resultListener != null) {
            resultListener.onLoadFinish();
        }
    }

    @Override
    public void getThreadListFail(int code, String message) {
        finishRefresh();
        if(resultListener != null) {
            resultListener.onLoadFail(code, message);
        }
    }

    @Override
    public void getMoreThreadListSuccess(CursorResult<ChatThread> result) {
        finishLoadMore();
        cursor = result.getCursor();
        mAdapter.addData(result.getData());
        if(resultListener != null) {
            resultListener.onLoadFinish();
        }
    }

    @Override
    public void getNoMoreThreadList() {
        finishLoadMore();
        if(resultListener != null) {
            resultListener.onLoadFinish();
        }
    }

    @Override
    public void getMoreThreadListFail(int code, String message) {
        finishLoadMore();
        if(resultListener != null) {
            resultListener.onLoadFail(code, message);
        }
    }

    @Override
    public void getThreadIdList(List<String> threadIds) {
        presenter.getThreadLatestMessages(threadIds);
    }

    @Override
    public void getLatestThreadMessagesSuccess(Map<String, ChatMessage> latestMessageMap) {
        mAdapter.setLatestMessages(latestMessageMap);
    }

    @Override
    public void getNoDataLatestThreadMessages() {

    }

    @Override
    public void getLatestThreadMessagesFail(int code, String message) {
        if(resultListener != null) {
            resultListener.onLoadFail(code, message);
        }
    }

    @Override
    public void getThreadParentInfoSuccess(Group group) {
        mGroup = group;
        if(isTitleEmpty(binding.titleBar.getSubTitle())) {
            binding.titleBar.setSubTitle(getString(R.string.ease_thread_list_sub_title, group.getGroupName()));
        }
        String currentUser = ChatClient.getInstance().getCurrentUser();
        // If current user is group admin
        if(TextUtils.equals(group.getOwner(), currentUser) || group.getAdminList().contains(currentUser)) {
            isGroupAdmin = true;
            presenter.getThreadList(group.getGroupId(), limit, cursor);
        }else {
            isGroupAdmin = false;
            presenter.getJoinedThreadList(group.getGroupId(), limit, cursor);
        }
        if(isTitleEmpty(binding.titleBar.getTitle())) {
            binding.titleBar.setTitle(getString(isGroupAdmin ? R.string.ease_thread_list_admin_title : R.string.ease_thread_list_member_title));
        }
    }

    @Override
    public void getThreadParentInfoFail(int code, String message) {
        if(resultListener != null) {
            resultListener.onLoadFail(code, message);
        }
    }

    public static class Builder {
        private final Bundle bundle;
        private EaseTitleBar.OnBackPressListener backPressListener;
        private EaseThreadListFragment customFragment;
        private EaseThreadListPresenter presenter;
        private OnLoadResultListener resultListener;
        private OnItemThreadClickListener itemClickListener;
        private OnTitleBarFinishInflateListener inflateListener;

        public Builder(String parentId) {
            this.bundle = new Bundle();
            bundle.putString(Constant.KEY_PARENT_ID, parentId);
        }

        /**
         * Whether to use default titleBar which is {@link EaseTitleBar}
         * @param useTitle
         * @return
         */
        public Builder useHeader(boolean useTitle) {
            this.bundle.putBoolean(Constant.KEY_USE_TITLE, useTitle);
            return this;
        }

        /**
         * Set titleBar's title
         * @param title
         * @return
         */
        public Builder setHeaderTitle(String title) {
            this.bundle.putString(Constant.KEY_SET_TITLE, title);
            return this;
        }

        /**
         * Set titleBar's sub title
         * @param subTitle
         * @return
         */
        public Builder setHeaderSubTitle(String subTitle) {
            this.bundle.putString(Constant.KEY_SET_SUB_TITLE, subTitle);
            return this;
        }

        /**
         * Whether show back icon in titleBar
         * @param canBack
         * @return
         */
        public Builder enableHeaderPressBack(boolean canBack) {
            this.bundle.putBoolean(Constant.KEY_ENABLE_BACK, canBack);
            return this;
        }

        /**
         * Set request limit, default is 10
         * @param limit
         * @return
         */
        public Builder setRequestLimit(int limit) {
            this.bundle.putInt(Constant.KEY_REQUEST_LIMIT, limit);
            return this;
        }

        /**
         * If you have set {@link Builder#enableHeaderPressBack(boolean)}, you can set the listener
         * @param listener
         * @return
         */
        public Builder setHeaderBackPressListener(EaseTitleBar.OnBackPressListener listener) {
            this.backPressListener = listener;
            return this;
        }

        /**
         * Set request load result listener
         * @param listener
         * @return
         */
        public Builder setLoadResultListener(OnLoadResultListener listener) {
            this.resultListener = listener;
            return this;
        }

        /**
         * Set item click listener
         * @param listener
         * @return
         */
        public Builder setOnItemClickListener(OnItemThreadClickListener listener) {
            this.itemClickListener = listener;
            return this;
        }

        /**
         * Set title bar finish inflate listener
         * @param inflateListener
         * @return
         */
        public Builder setOnTitleBarFinishInflateListener(OnTitleBarFinishInflateListener inflateListener) {
            this.inflateListener = inflateListener;
            return this;
        }

        /**
         * Set custom presenter if you want to add your logic
         * @param presenter
         * @param <T>
         * @return
         */
        public <T extends EaseThreadListPresenter> Builder setCustomPresenter(T presenter) {
            this.presenter = presenter;
            return this;
        }

        /**
         * Set custom fragment which should extends EaseMessageFragment
         * @param fragment
         * @param <T>
         * @return
         */
        public <T extends EaseThreadListFragment> Builder setCustomFragment(T fragment) {
            this.customFragment = fragment;
            return this;
        }

        public EaseThreadListFragment build() {
            EaseThreadListFragment fragment = this.customFragment != null ? this.customFragment : new EaseThreadListFragment();
            fragment.setArguments(this.bundle);
            fragment.setHeaderBackPressListener(this.backPressListener);
            fragment.setOnLoadResultListener(this.resultListener);
            fragment.setCustomPresenter(this.presenter);
            fragment.setOnItemClickListener(this.itemClickListener);
            fragment.setOnTitleBarFinishInflateListener(this.inflateListener);
            return fragment;
        }
    }

    /**
     * Request result listener
     */
    public interface OnLoadResultListener {
        /**
         * load finish
         */
        void onLoadFinish();

        /**
         * load failed
         * @param errorCode
         * @param message
         */
        void onLoadFail(int errorCode, String message);
    }

    private static class Constant {
        public static final String KEY_PARENT_ID = "key_parent_id";
        public static final String KEY_USE_TITLE = "key_use_title";
        public static final String KEY_SET_TITLE = "key_set_title";
        public static final String KEY_SET_SUB_TITLE = "key_set_sub_title";
        public static final String KEY_ENABLE_BACK = "key_enable_back";
        public static final String KEY_REQUEST_LIMIT = "key_request_limit";
    }
}
