package com.xamoom.android.xamoom_pingeborg_android.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xamoom.android.pushnotifications.XamoomPushActivity;
import com.xamoom.android.xamoom_pingeborg_android.ArtistDetailActivity;
import com.xamoom.android.xamoom_pingeborg_android.MainActivity;
import com.xamoom.android.xamoomsdk.Resource.Content;

public class NotificationHandler extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    String contentId = intent.getExtras().getString(XamoomPushActivity.CONTENT_ID_NAME);

    Intent openIntent = null;

    if (contentId != null) {
      Content content = new Content();
      content.setId(contentId);

      openIntent = new Intent(context, ArtistDetailActivity.class);
      openIntent.putExtra(ArtistDetailActivity.CONTENT, content);
    } else {
      openIntent = new Intent(context, MainActivity.class);
    }

    context.startActivity(openIntent);
  }
}
