package io.agora.chat.uikit.widget.video;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * Use LiveData when setting up and listening to a single data source
 * It is convenient to automatically cancel the monitoring of the previous data source when the data source needs to be switched.
 *
 * @param <T> Listening data source type
 */
public class EaseSingleSourceLiveData<T> extends MutableLiveData<T> {
    private LiveData<T> lastSource;
    private T lastData;
    private final Observer<T> observer = new Observer<T>() {
        @Override
        public void onChanged(T t) {
            if (t != null && t == lastData) {
                return;
            }

            lastData = t;
            setValue(t);
        }
    };

    /**
     * Set the data source, when there is a data source that has been set, the monitoring of the data source will be canceled
     *
     * @param source
     */
    public void setSource(LiveData<T> source) {
        if (lastSource == source) {
            return;
        }

        if (lastSource != null) {
            lastSource.removeObserver(observer);
        }
        lastSource = source;

        if (hasActiveObservers()) {
            lastSource.observeForever(observer);
        }
    }

    @Override
    protected void onActive() {
        super.onActive();

        if (lastSource != null) {
            lastSource.observeForever(observer);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        if (lastSource != null) {
            lastSource.removeObserver(observer);
        }
    }
}
