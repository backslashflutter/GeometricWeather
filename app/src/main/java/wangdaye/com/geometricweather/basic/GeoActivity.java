package wangdaye.com.geometricweather.basic;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import java.util.HashSet;
import java.util.Set;

import wangdaye.com.geometricweather.GeometricWeather;
import wangdaye.com.geometricweather.settings.SettingsOptionManager;
import wangdaye.com.geometricweather.utils.DisplayUtils;
import wangdaye.com.geometricweather.utils.LanguageUtils;

/**
 * Geometric weather activity.
 * */

public abstract class GeoActivity extends AppCompatActivity {

    private Set<GeoDialog> mDialogSet;

    private boolean mStarted = false;
    private boolean mForeground = false;

    private @Nullable OnRequestPermissionsResultListener mPermissionsListener;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GeometricWeather.getInstance().addActivity(this);
        mDialogSet = new HashSet<>();

        LanguageUtils.setLanguage(
                this,
                SettingsOptionManager.getInstance(this).getLanguage().getLocale()
        );

        boolean darkMode = DisplayUtils.isDarkMode(this);
        DisplayUtils.setSystemBarStyle(this, getWindow(),
                false, false, true, !darkMode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStarted = true;
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        mForeground = true;
    }

    @CallSuper
    @Override
    protected void onPause() {
        super.onPause();
        mForeground = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStarted = false;
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        GeometricWeather.getInstance().removeActivity(this);
    }

    public abstract View getSnackbarContainer();

    public View provideSnackbarContainer(boolean[] fromActivity) {
        GeoDialog topDialog = getTopDialog();
        if (topDialog == null) {
            fromActivity[0] = true;
            return getSnackbarContainer();
        } else {
            fromActivity[0] = false;
            return topDialog.getSnackbarContainer();
        }
    }

    public boolean isStarted() {
        return mStarted;
    }

    public boolean isForeground() {
        return mForeground;
    }

    public void addDialog(GeoDialog d) {
        mDialogSet.add(d);
    }

    public void removeDialog(GeoDialog d) {
        mDialogSet.remove(d);
    }

    @Nullable
    private GeoDialog getTopDialog() {
        for (GeoDialog dialog : mDialogSet) {
            if (dialog.isForeground()) {
                return dialog;
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestPermissions(@NonNull String[] permissions, int requestCode,
                                   @Nullable OnRequestPermissionsResultListener l) {
        mPermissionsListener = l;
        requestPermissions(permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        if (mPermissionsListener != null) {
            mPermissionsListener.onRequestPermissionsResult(requestCode, permission, grantResult);
        }
    }

    public interface OnRequestPermissionsResultListener {
        void onRequestPermissionsResult(int requestCode,
                                        @NonNull String[] permission, @NonNull int[] grantResult);
    }
}
