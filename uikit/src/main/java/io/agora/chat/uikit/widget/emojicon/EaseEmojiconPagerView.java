package io.agora.chat.uikit.widget.emojicon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import java.util.ArrayList;
import java.util.List;

import io.agora.chat.uikit.R;
import io.agora.chat.uikit.chat.adapter.EmojiconGridAdapter;
import io.agora.chat.uikit.chat.adapter.EmojiconPagerAdapter;
import io.agora.chat.uikit.models.EaseEmojicon;
import io.agora.chat.uikit.models.EaseEmojicon.Type;
import io.agora.chat.uikit.models.EaseEmojiconGroupEntity;
import io.agora.chat.uikit.utils.EaseSmileUtils;
import io.agora.chat.uikit.utils.EaseUtils;

public class EaseEmojiconPagerView extends ViewPager {

    private Context context;
    private List<EaseEmojiconGroupEntity> groupEntities;

    private PagerAdapter pagerAdapter;
    
    private int emojiconRows = 3;
    private int emojiconColumns = 7;
    
    private int bigEmojiconRows = 2;
    private int bigEmojiconColumns = 4;
    
    private int firstGroupPageSize;
    
    private int maxPageCount;
    private int previousPagerPosition;
	private EaseEmojiconPagerViewListener pagerViewListener;
    private List<View> viewpages; 

    public EaseEmojiconPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public EaseEmojiconPagerView(Context context) {
        this(context, null);
    }
    
    
    public void init(List<EaseEmojiconGroupEntity> emojiconGroupList, int emijiconColumns, int bigEmojiconColumns){
        if(emojiconGroupList == null){
            throw new RuntimeException("emojiconGroupList is null");
        }
        
        this.groupEntities = emojiconGroupList;
        this.emojiconColumns = emijiconColumns;
        this.bigEmojiconColumns = bigEmojiconColumns;
        
        viewpages = new ArrayList<View>();
        for(int i = 0; i < groupEntities.size(); i++){
            EaseEmojiconGroupEntity group = groupEntities.get(i);
            List<EaseEmojicon> groupEmojicons = group.getEmojiconList();
            List<View> gridViews = getGroupGridViews(group);
            if(i == 0){
                firstGroupPageSize = gridViews.size();
            }
            maxPageCount = Math.max(gridViews.size(), maxPageCount);
            viewpages.addAll(gridViews);
        }
        
        pagerAdapter = new EmojiconPagerAdapter(viewpages);
        setAdapter(pagerAdapter);
        setOnPageChangeListener(new EmojiPagerChangeListener());
        
        if(pagerViewListener != null){
            pagerViewListener.onPagerViewInited(maxPageCount, firstGroupPageSize);
        }
    }
    
    public void setPagerViewListener(EaseEmojiconPagerViewListener pagerViewListener){
    	this.pagerViewListener = pagerViewListener;
    }
    
    
    /**
     * set emojicon group position
     * @param position
     */
    public void setGroupPostion(int position){
    	if (getAdapter() != null && position >= 0 && position < groupEntities.size()) {
            int count = 0;
            for (int i = 0; i < position; i++) {
                count += getPageSize(groupEntities.get(i));
            }
            setCurrentItem(count);
        }
    }
    
    /**
     * get emojicon group gridview list
     * @param groupEntity
     * @return
     */
    public List<View> getGroupGridViews(EaseEmojiconGroupEntity groupEntity){
        List<EaseEmojicon> emojiconList = groupEntity.getEmojiconList();
        Type emojiType = groupEntity.getType();
        List<View> views = new ArrayList<View>();

        // Set viewPager's item view
        View view = View.inflate(context, R.layout.ease_expression_gridview, null);
        GridView gv = (GridView) view.findViewById(R.id.gridview);
        ViewGroup ll_action = view.findViewById(R.id.ll_action);
        ImageView iv_emoji_delete = view.findViewById(R.id.iv_emoji_delete);
        ImageView iv_emoji_send = view.findViewById(R.id.iv_emoji_send);
        int columns = 0;
        if(emojiType == Type.BIG_EXPRESSION){
            columns = bigEmojiconColumns;
        }else{
            columns = emojiconColumns;
        }
        gv.setVerticalSpacing((int) EaseUtils.dip2px(getContext(), 20));
        gv.setNumColumns(columns);
        // To prevent the emoji from being obscured
        int addItems = emojiconList.size() % columns == 0 ? columns + 1 : (columns * 2 - emojiconList.size() % columns) + 1;
        List<EaseEmojicon> list = new ArrayList<>();
        list.addAll(emojiconList);
//        if(emojiType != Type.BIG_EXPRESSION){
//            EaseEmojicon deleteIcon = new EaseEmojicon();
//            deleteIcon.setEmojiText(EaseSmileUtils.DELETE_KEY);
//            list.add(deleteIcon);
//        }
        for(int i = 0; i < addItems; i++) {
            EaseEmojicon icon = new EaseEmojicon();
            icon.setEnableClick(false);
            list.add(icon);
        }
        final EmojiconGridAdapter gridAdapter = new EmojiconGridAdapter(context, 1, list, emojiType);
        gv.setAdapter(gridAdapter);
        iv_emoji_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerViewListener.onDeleteImageClicked();
            }
        });
        iv_emoji_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerViewListener.onSendIconClicked();
            }
        });
        if(emojiType == Type.BIG_EXPRESSION){
            ll_action.setVisibility(GONE);
        }else{
            ll_action.setVisibility(VISIBLE);
        }
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseEmojicon emojicon = gridAdapter.getItem(position);
                if(pagerViewListener != null){
                    String emojiText = emojicon.getEmojiText();
                    if(emojiText != null && emojiText.equals(EaseSmileUtils.DELETE_KEY)){
                        pagerViewListener.onDeleteImageClicked();
                    }else{
                        pagerViewListener.onExpressionClicked(emojicon);
                    }

                }

            }
        });

        views.add(view);
        return views;
    }
    

    /**
     * add emojicon group
     * @param groupEntity
     */
    public void addEmojiconGroup(EaseEmojiconGroupEntity groupEntity, boolean notifyDataChange) {
        int pageSize = getPageSize(groupEntity);
        if(pageSize > maxPageCount){
            maxPageCount = pageSize;
            if(pagerViewListener != null && pagerAdapter != null){
                pagerViewListener.onGroupMaxPageSizeChanged(maxPageCount);
            }
        }
        viewpages.addAll(getGroupGridViews(groupEntity));
        if(pagerAdapter != null && notifyDataChange){
            pagerAdapter.notifyDataSetChanged();
        }
    }
    
    /**
     * remove emojicon group
     * @param position
     */
    public void removeEmojiconGroup(int position){
        if(position > groupEntities.size() - 1){
            return;
        }
        if(pagerAdapter != null){
            pagerAdapter.notifyDataSetChanged();
        }
    }
    
    /**
     * get size of pages
     * @param groupEntity
     * @return
     */
    private int getPageSize(EaseEmojiconGroupEntity groupEntity) {
        List<EaseEmojicon> emojiconList = groupEntity.getEmojiconList();
        int itemSize = emojiconColumns * emojiconRows -1;
        int totalSize = emojiconList.size();
        Type emojiType = groupEntity.getType();
        if(emojiType == Type.BIG_EXPRESSION){
            itemSize = bigEmojiconColumns * bigEmojiconRows;
        }
        int pageSize = totalSize % itemSize == 0 ? totalSize/itemSize : totalSize/itemSize + 1;   
        return pageSize;
    }
    
    private class EmojiPagerChangeListener implements OnPageChangeListener{
        @Override
        public void onPageSelected(int position) {
        	int endSize = 0;
        	int groupPosition = 0;
            for(EaseEmojiconGroupEntity groupEntity : groupEntities){
            	int groupPageSize = getPageSize(groupEntity);
            	//if the position is in current group
            	if(endSize + groupPageSize > position){
            		//this is means user swipe to here from previous page
            		if(previousPagerPosition - endSize < 0){
            			if(pagerViewListener != null){
            				pagerViewListener.onGroupPositionChanged(groupPosition, groupPageSize);
            				pagerViewListener.onGroupPagePostionChangedTo(0);
            			}
            			break;
            		}
            		//this is means user swipe to here from back page
            		if(previousPagerPosition - endSize >= groupPageSize){
            			if(pagerViewListener != null){
            				pagerViewListener.onGroupPositionChanged(groupPosition, groupPageSize);
            				pagerViewListener.onGroupPagePostionChangedTo(position - endSize);
            			}
            			break;
            		}
            		
            		//page changed
            		if(pagerViewListener != null){
            			pagerViewListener.onGroupInnerPagePostionChanged(previousPagerPosition-endSize, position-endSize);
            		}
            		break;
            		
            	}
            	groupPosition++;
            	endSize += groupPageSize;
            }
            
            previousPagerPosition = position;
        }
        
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }
    
    
    
    public interface EaseEmojiconPagerViewListener{
        /**
         * pagerview initialized
         * @param groupMaxPageSize --max pages size
         * @param firstGroupPageSize-- size of first group pages
         */
        void onPagerViewInited(int groupMaxPageSize, int firstGroupPageSize);
        
    	/**
    	 * group position changed
    	 * @param groupPosition--group position
    	 * @param pagerSizeOfGroup--page size of group
    	 */
    	void onGroupPositionChanged(int groupPosition, int pagerSizeOfGroup);
    	/**
    	 * page position changed
    	 * @param oldPosition
    	 * @param newPosition
    	 */
    	void onGroupInnerPagePostionChanged(int oldPosition, int newPosition);
    	
    	/**
    	 * group page position changed
    	 * @param position
    	 */
    	void onGroupPagePostionChangedTo(int position);
    	
    	/**
    	 * max page size changed
    	 * @param maxCount
    	 */
    	void onGroupMaxPageSizeChanged(int maxCount);
    	
    	void onDeleteImageClicked();
    	void onExpressionClicked(EaseEmojicon emojicon);

        /**
         * Click send icon which you can send your emoji in editText
         */
    	void onSendIconClicked();
    	
    }

}
