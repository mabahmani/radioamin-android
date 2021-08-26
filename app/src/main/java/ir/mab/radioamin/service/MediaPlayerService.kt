package ir.mab.radioamin.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mab.radioamin.R
import ir.mab.radioamin.util.AppConstants
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlayerService: Service() {
    @Inject
    lateinit var player: SimpleExoPlayer

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand")
        val playerNotificationManager = PlayerNotificationManager.Builder(this,
            AppConstants.Notifications.PLAYER_NOTIFICATION_ID,
            AppConstants.Notifications.PLAYER_NOTIFICATION_CHANNEL_ID)
            .setChannelNameResourceId(R.string.app_name)
            .setChannelDescriptionResourceId(R.string.app_name)
            .setSmallIconResourceId(R.drawable.ic_small_logo)
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener{
                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    stopSelf()
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        startForeground(notificationId, notification);
                    } else {
                        stopForeground(false);
                    }
                }
            })
            .build()

        playerNotificationManager.setPlayer(player)

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}