package io.agora.chat.uikit.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import java.util.HashMap;
import java.util.Map;

import io.agora.chat.uikit.interfaces.IUIKitInterface;
import io.agora.util.EMLog;

/**
 * This is an internal management class, which is used to manage some interfaces inside uikit,
 * which is convenient for interface setting and transfer.
 * Do not call it externally.
 */
public class EaseChatInterfaceManager {
    private static final String TAG = EaseChatInterfaceManager.class.getSimpleName();
    private static EaseChatInterfaceManager mInstance;
    private Map<String, IUIKitInterface> interfaceMap;

    private EaseChatInterfaceManager(){
        interfaceMap = new HashMap<>();
    }

    public static EaseChatInterfaceManager getInstance() {
        if(mInstance == null) {
            synchronized (EaseChatInterfaceManager.class) {
                if(mInstance == null) {
                    mInstance = new EaseChatInterfaceManager();
                }
            }
        }
        return mInstance;
    }

    public void setInterface(Context context, String tag, IUIKitInterface iuiKitInterface) {
        if(TextUtils.isEmpty(tag)) {
            EMLog.e(TAG, "tag should not be null");
            return;
        }
        interfaceMap.put(tag+context.hashCode(), iuiKitInterface);
    }

    public IUIKitInterface getInterface(Context context, String tag) {
        if(!interfaceMap.containsKey(tag+context.hashCode())) {
            EMLog.e(TAG, "Do not have interface with tag: "+tag);
            return null;
        }
        return interfaceMap.get(tag+context.hashCode());
    }

    public boolean removeInterface(Context context, String tag) {
        if(TextUtils.isEmpty(tag)) {
            return false;
        }
        return interfaceMap.remove(tag+context.hashCode()) != null;
    }

    public void clear() {
        interfaceMap.clear();
    }
}
