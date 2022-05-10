package io.agora.chat.uikit.widget.video;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.base.EaseBaseFragment;
import io.agora.chat.uikit.interfaces.OnItemClickListener;
import io.agora.chat.uikit.models.EaseVideoEntity;
import io.agora.chat.uikit.utils.EaseCompat;
import io.agora.chat.uikit.utils.EaseDateUtils;
import io.agora.chat.uikit.utils.EaseUtils;
import io.agora.chat.uikit.widget.EaseTitleBar;
import io.agora.util.TextFormater;

public class EaseImageGridFragment extends EaseBaseFragment implements OnItemClickListener {

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private EaseImageResizer mImageResizer;
    private File videoFile;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public EaseImageGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageThumbSize = getResources().getDimensionPixelSize(
                R.dimen.ease_image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(
                R.dimen.ease_image_thumbnail_spacing);
        mAdapter = new ImageAdapter();

        EaseImageCache.ImageCacheParams cacheParams = new EaseImageCache.ImageCacheParams();

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
        // app memory

        // The ImageFetcher takes care of loading images into our ImageView
        // children asynchronously
        mImageResizer = new EaseImageResizer(getActivity(), mImageThumbSize);
        mImageResizer.setLoadingImage(R.drawable.ease_empty_photo);
        mImageResizer.addImageCache(getActivity().getSupportFragmentManager(),
                cacheParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.ease_image_grid_fragment,
                container, false);
        EaseTitleBar title_bar = v.findViewById(R.id.title_bar);
        title_bar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                mContext.onBackPressed();
            }
        });
        final RecyclerView rvVideoGrid = v.findViewById(R.id.rv_video_grid);
        rvVideoGrid.setAdapter(mAdapter);
        EaseDividerGridItemDecoration itemDecoration = new EaseDividerGridItemDecoration(mContext, R.drawable.ease_divider_video_list, false);
        rvVideoGrid.addItemDecoration(itemDecoration);
        mAdapter.setOnItemClickListener(this);
        rvVideoGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // Pause fetcher to ensure smoother scrolling when flinging
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // Before Honeycomb pause image loading on scroll to help
                    // with performance
                    if (!EaseUtils.hasHoneycomb()) {
                        mImageResizer.setPauseWork(true);
                    }
                } else {
                    mImageResizer.setPauseWork(false);
                }
            }
        });

        // This listener is used to get the final width of the GridView and then
        // calculate the
        // number of columns and the width of each column. The width of each
        // column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used
        // to set the height
        // of each view so we get nice square thumbnails.
        rvVideoGrid.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Log.e("TAG", "current Thread  = " + Thread.currentThread().getName());
                        final int numColumns = (int) Math.floor(rvVideoGrid
                                .getWidth()
                                / (mImageThumbSize + mImageThumbSpacing));
                        if (numColumns > 0) {
                            final int columnWidth = (rvVideoGrid.getWidth() / numColumns)
                                    - mImageThumbSpacing;
                            mAdapter.setItemHeight(columnWidth);
                            rvVideoGrid.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                    }
                });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getVideoFile();
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageResizer.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mContext.isFinishing()) {
            mImageResizer.closeCache();
            mImageResizer.clearCache();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(View view, int position) {
        mImageResizer.setPauseWork(true);

        if (position == 0) {
            videoFile = EaseCompat.takeVideo(this, 100);
        } else {
            EaseVideoEntity vEntty = mAdapter.getData().get(position - 1);
            Intent intent;
            if (vEntty == null || (TextUtils.isEmpty(vEntty.filePath) && vEntty.uri == null)) {
                return;
            }
            if (vEntty.uri != null) {
                intent = getActivity().getIntent().putExtra("uri", vEntty.uri.toString()).putExtra("dur", vEntty.duration);
            } else {
                intent = getActivity().getIntent().putExtra("path", vEntty.filePath).putExtra("dur", vEntty.duration);
            }
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private int mItemHeight;
        private ViewGroup.LayoutParams mImageViewLayoutParams;
        private List<EaseVideoEntity> mData;
        private OnItemClickListener mListener;

        public ImageAdapter() {
            mImageViewLayoutParams = new ViewGroup.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ease_choose_griditem, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(v, position);
                    }
                }
            });
            // Check the height matches our calculated column width
            if (holder.itemView.getLayoutParams().height != mItemHeight) {
                holder.itemView.setLayoutParams(mImageViewLayoutParams);
            }
            if (position == 0) {
                holder.icon.setVisibility(View.GONE);
                holder.tvDur.setVisibility(View.GONE);
                holder.llTakeVideo.setVisibility(View.VISIBLE);
                holder.imageView.setImageDrawable(null);
                holder.imageView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ease_bg_take_video));
                holder.videoDataArea.setVisibility(View.GONE);
                return;
            }
            EaseVideoEntity entity = mData.get(position - 1);
            holder.icon.setVisibility(View.VISIBLE);
            holder.llTakeVideo.setVisibility(View.GONE);
            holder.videoDataArea.setVisibility(View.VISIBLE);
            holder.tvDur.setVisibility(View.VISIBLE);

            holder.tvDur.setText(EaseDateUtils.toTime(entity.duration));
            holder.tvSize.setText(TextFormater.getDataSize(entity.size));
            holder.imageView.setBackground(null);
            holder.imageView.setImageResource(R.drawable.ease_empty_photo);
            mImageResizer.loadImage(entity.filePath, holder.imageView);
        }

        @Override
        public int getItemCount() {
            return (mData == null || mData.isEmpty()) ? 1 : mData.size() + 1;
        }

        public void setData(List<EaseVideoEntity> data) {
            this.mData = data;
            notifyDataSetChanged();
        }

        public List<EaseVideoEntity> getData() {
            return mData;
        }

        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, mItemHeight);
            mImageResizer.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mListener = listener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout llTakeVideo;
            LinearLayout videoDataArea;
            EaseRecyclingImageView imageView;
            ImageView icon;
            TextView tvDur;
            TextView tvSize;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setLayoutParams(mImageViewLayoutParams);
                imageView = itemView.findViewById(R.id.imageView);
                icon = itemView.findViewById(R.id.video_icon);
                llTakeVideo = itemView.findViewById(R.id.ll_take_video);
                videoDataArea = itemView.findViewById(R.id.video_data_area);
                tvDur = itemView.findViewById(R.id.chatting_length_iv);
                tvSize = itemView.findViewById(R.id.chatting_size_iv);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

    private void getVideoFile() {
        EaseVideoListViewModel viewModel = new ViewModelProvider(this).get(EaseVideoListViewModel.class);
        viewModel.getVideoListObservable().observe(getViewLifecycleOwner(), new Observer<List<EaseVideoEntity>>() {
            @Override
            public void onChanged(List<EaseVideoEntity> easeVideoEntities) {
                mAdapter.setData(easeVideoEntities);
                mImageResizer.setPauseWork(false);
            }
        });
        viewModel.getVideoList(mContext);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                if (videoFile != null && videoFile.exists()) {
                    int duration = 0;
                    MediaPlayer player = new MediaPlayer();
                    try {
                        player.setDataSource(videoFile.getPath());
                        player.prepare();
                        duration = player.getDuration();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent();
                    intent.putExtra("path", videoFile.getAbsolutePath());
                    intent.putExtra("dur", duration);
                    mContext.setResult(Activity.RESULT_OK, intent);
                }
                mContext.finish();
            }
        }
    }
}
