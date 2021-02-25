package com.skyd.imomoe.util.dlna.dmc;

import android.text.TextUtils;
import android.util.Log;

import com.skyd.imomoe.BuildConfig;

public interface ILogger {
    String PREFIX_TAG = "DLNACast_";

    void v(String msg);

    void d(String msg);

    void i(String msg);

    void w(String msg);

    void e(String msg);

    class DefaultLoggerImpl implements ILogger {
        private final String TAG;
        private final boolean DEBUG;

        public DefaultLoggerImpl(Object object) {
            this(object, BuildConfig.DEBUG);
        }

        public DefaultLoggerImpl(Object object, boolean debug) {
            String className = object.getClass().getSimpleName();
            if (TextUtils.isEmpty(className)) {
                if (object.getClass().getSuperclass() != null) {
                    className = object.getClass().getSuperclass().getSimpleName();
                } else {
                    className = "$1";
                }
            }
            TAG = PREFIX_TAG + className;
            DEBUG = debug;
        }

        @Override
        public void v(String msg) {
            if (DEBUG) {
                Log.v(TAG, msg);
            }
        }

        @Override
        public void d(String msg) {
            if (DEBUG) {
                Log.d(TAG, msg);
            }
        }

        @Override
        public void i(String msg) {
            if (DEBUG) {
                Log.i(TAG, msg);
            }
        }

        @Override
        public void w(String msg) {
            if (DEBUG) {
                Log.w(TAG, msg);
            }
        }

        @Override
        public void e(String msg) {
            if (DEBUG) {
                Log.e(TAG, msg);
            }
        }
    }
}
