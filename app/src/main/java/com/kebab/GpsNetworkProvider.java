package com.kebab;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import com.kebab.Llama.Logging;

public class GpsNetworkProvider {
    static String TAG = "GpsNetworkProvider";
    int UPDATE_DISTANCE = 10;
    Context _Context;
    Boolean _GpsAvailable;
    LocationListener _GpsListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (!GpsNetworkProvider.this._LocationFoundWithGps) {
                GpsNetworkProvider.this._LocationFoundWithGps = true;
                GpsNetworkProvider.this.CancelTimeWithoutGpsRunnable();
                GpsNetworkProvider.this._Listener.LocationAvailabilityChanged(true, true);
            }
            GpsNetworkProvider.this.LocationChanged(true, location);
        }

        public void onProviderDisabled(String provider) {
            GpsNetworkProvider.this._LocationFoundWithGps = false;
            Logging.Report(GpsNetworkProvider.TAG, "Android sez GPS now disabled (" + provider + ")", GpsNetworkProvider.this._Context);
            GpsNetworkProvider.this.EnableNetworkProviderIfNotEnabled();
        }

        public void onProviderEnabled(String provider) {
            GpsNetworkProvider.this._LocationFoundWithGps = false;
            Logging.Report(GpsNetworkProvider.TAG, "Android sez GPS now enabled (" + provider + ")", GpsNetworkProvider.this._Context);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == 2) {
                Logging.Report(GpsNetworkProvider.TAG, "Android sez GPS now available (" + provider + ")", GpsNetworkProvider.this._Context);
                GpsNetworkProvider.this.DisableNetworkProvider();
            } else if (status == 1) {
                Logging.Report(GpsNetworkProvider.TAG, "Android sez GPS now tempunavailable (" + provider + ")", GpsNetworkProvider.this._Context);
                GpsNetworkProvider.this.StartTimeWithoutGpsRunnable();
            } else if (status == 0) {
                Logging.Report(GpsNetworkProvider.TAG, "Android sez GPS out of service (" + provider + ")", GpsNetworkProvider.this._Context);
                GpsNetworkProvider.this._LocationFoundWithGps = false;
                GpsNetworkProvider.this.EnableNetworkProviderIfNotEnabled();
            }
        }
    };
    String _GpsProvider;
    boolean _GpsUpdates;
    CombinedLocationListener _Listener;
    boolean _LocationFoundWithGps;
    boolean _LocationFoundWithNetwork;
    LocationManager _LocationManager;
    LocationListener _NetworkListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (!GpsNetworkProvider.this._LocationFoundWithGps) {
                GpsNetworkProvider.this.LocationChanged(false, location);
            }
            if (!GpsNetworkProvider.this._LocationFoundWithNetwork) {
                GpsNetworkProvider.this._Listener.LocationAvailabilityChanged(true, false);
                GpsNetworkProvider.this._LocationFoundWithNetwork = true;
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    String _NetworkProvider;
    boolean _NetworkUpdates;
    Handler _TimeWithoutGps = new Handler();
    Runnable _TimeWithoutGpsRunnable = new Runnable() {
        public void run() {
            GpsNetworkProvider.this._LocationFoundWithGps = false;
            GpsNetworkProvider.this.EnableNetworkProviderIfNotEnabled();
        }
    };
    int _UpdateMillis;

    public interface CombinedLocationListener {
        void LocationAvailabilityChanged(boolean z, boolean z2);

        void LocationChanged(boolean z, Location location);
    }

    public GpsNetworkProvider(LocationManager locationManager, CombinedLocationListener listener, Context context) {
        this._LocationManager = locationManager;
        this._Context = context.getApplicationContext();
        Criteria c = new Criteria();
        c.setAccuracy(1);
        this._GpsProvider = this._LocationManager.getBestProvider(c, false);
        c = new Criteria();
        c.setAccuracy(2);
        c.setPowerRequirement(1);
        this._NetworkProvider = this._LocationManager.getBestProvider(c, false);
        Logging.Report(TAG, "GPS=" + this._GpsProvider + ", Network=" + this._NetworkProvider, this._Context);
        this._Listener = listener;
    }

    /* Access modifiers changed, original: 0000 */
    public void StartTimeWithoutGpsRunnable() {
        CancelTimeWithoutGpsRunnable();
        this._TimeWithoutGps.postDelayed(this._TimeWithoutGpsRunnable, (long) this._UpdateMillis);
    }

    /* Access modifiers changed, original: 0000 */
    public void CancelTimeWithoutGpsRunnable() {
        this._TimeWithoutGps.removeCallbacks(this._TimeWithoutGpsRunnable);
    }

    /* Access modifiers changed, original: protected */
    public void LocationChanged(boolean isGps, Location location) {
        this._Listener.LocationChanged(isGps, location);
    }

    public void StartTracking(boolean enableGps, int updateMillis) {
        if (enableGps) {
            EnableGpsProviderIfNotEnabled();
            Logging.Report(TAG, "Starting tracking with GPS... ensuring GPSListener enabled", this._Context);
        } else {
            DisableGpsProvider();
            Logging.Report(TAG, "Starting tracking without GPS... ensuring GPSListener disabled", this._Context);
        }
        EnableNetworkProviderIfNotEnabled();
        Logging.Report(TAG, "...also making sure NetworkListener is enabled", this._Context);
    }

    /* Access modifiers changed, original: 0000 */
    public void EnableGpsProviderIfNotEnabled() {
        if (!this._GpsUpdates && this._GpsProvider != null) {
            this._LocationManager.requestLocationUpdates(this._GpsProvider, (long) this._UpdateMillis, (float) this.UPDATE_DISTANCE, this._GpsListener);
            this._GpsUpdates = true;
            Logging.Report(TAG, "GPSListener now enabled", this._Context);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void DisableGpsProvider() {
        this._LocationManager.removeUpdates(this._GpsListener);
        this._GpsUpdates = false;
        Logging.Report(TAG, "GPSListener disabled", this._Context);
    }

    /* Access modifiers changed, original: 0000 */
    public void EnableNetworkProviderIfNotEnabled() {
        if (!this._NetworkUpdates && this._NetworkProvider != null) {
            this._LocationManager.requestLocationUpdates(this._NetworkProvider, (long) this._UpdateMillis, (float) this.UPDATE_DISTANCE, this._NetworkListener);
            this._NetworkUpdates = true;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void DisableNetworkProvider() {
        this._LocationManager.removeUpdates(this._NetworkListener);
        this._NetworkUpdates = false;
    }

    public void StopTracking() {
        DisableGpsProvider();
        DisableNetworkProvider();
    }
}
