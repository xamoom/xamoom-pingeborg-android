package com.xamoom.android.xamoom_pingeborg_android.Notifications;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xamoom.android.pushnotifications.XamoomNotificationReceiver;
import com.xamoom.android.pushnotifications.XamoomPushActivity;
import com.xamoom.android.xamoom_pingeborg_android.ArtistDetailActivity;
import com.xamoom.android.xamoom_pingeborg_android.MainActivity;
import com.xamoom.android.xamoomsdk.Resource.Content;

public class NotificationHandler extends BroadcastReceiver {
  private static final String TAG = NotificationHandler.class.getSimpleName();

  @Override
  public void onReceive(Context context, Intent intent) {
    String contentId = intent.getExtras().getString(XamoomPushActivity.CONTENT_ID_NAME);

    PendingIntent pendingIntent = null;
    Intent openIntent = null;

    if (contentId != null) {
      Content content = new Content();
      content.setId(contentId);

      openIntent = new Intent(context, ArtistDetailActivity.class);
      openIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      openIntent.putExtra(ArtistDetailActivity.CONTENT, content);

      pendingIntent = TaskStackBuilder.create(context)
                      .addParentStack(MainActivity.class)
                      .addNextIntentWithParentStack(openIntent)
                      .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    } else {
      openIntent = new Intent(context, MainActivity.class);
    }

    if (pendingIntent != null) {
      try {
        pendingIntent.send();
      } catch (PendingIntent.CanceledException e) {
        Log.e(TAG, "PendingIntent Canceled");
      }
    } else {
      context.startActivity(openIntent);
    }
  }
}
