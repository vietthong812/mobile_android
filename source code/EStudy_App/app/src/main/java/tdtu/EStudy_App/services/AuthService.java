package tdtu.EStudy_App.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthService extends Service {
    public AuthService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}