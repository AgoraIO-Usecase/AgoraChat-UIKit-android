package io.agora.chat.uikit.interfaces;

import java.util.List;

import io.agora.MultiDeviceListener;

public abstract class EaseMultiDeviceListener implements MultiDeviceListener {
    @Override
    public void onContactEvent(int event, String target, String ext) {
        switch (event) {
            case CONTACT_REMOVE:
                onContactRemove(target, ext);
                break;
            case CONTACT_ACCEPT:
                onContactAccept(target, ext);
                break;
            case CONTACT_BAN:
                onContactBan(target, ext);
                break;
            case CONTACT_ALLOW:
                onContactAllow(target, ext);
                break;
        }
    }

    protected abstract void onContactAllow(String target, String ext);

    protected abstract void onContactBan(String target, String ext);

    protected abstract void onContactAccept(String target, String ext);

    protected abstract void onContactRemove(String target, String ext);

    @Override
    public void onGroupEvent(int event, String target, List<String> usernames) {
        switch (event) {
            case GROUP_DESTROY:
                onGroupDestroy(target, usernames);
                break;
            case GROUP_LEAVE:
                onGroupLeave(target, usernames);
                break;
        }
    }

    protected abstract void onGroupLeave(String target, List<String> usernames);

    protected abstract void onGroupDestroy(String target, List<String> usernames);
    
}
