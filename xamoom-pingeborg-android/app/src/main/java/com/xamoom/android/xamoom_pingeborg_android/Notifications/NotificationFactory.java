package com.xamoom.android.xamoom_pingeborg_android.Notifications;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;

import com.pushwoosh.notification.PushData;
import com.xamoom.android.pushnotifications.XamoomNotificationFactory;
import com.xamoom.android.xamoom_pingeborg_android.R;

public class NotificationFactory extends XamoomNotificationFactory {

  @Override
  public Notification onGenerateNotification(PushData pushData) {
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext());
    notificationBuilder.setContentTitle(getContentFromHtml(pushData.getHeader()));
    notificationBuilder.setContentText(getContentFromHtml(pushData.getMessage()));
    notificationBuilder.setSmallIcon(R.drawable.ic_x);
    notificationBuilder.setTicker(getContentFromHtml(pushData.getTicker()));
    notificationBuilder.setWhen(System.currentTimeMillis());

    Notification notification = notificationBuilder.build();

    addSound(notification, pushData.getSound());
    addVibration(notification, pushData.getVibration());
    addCancel(notification);

    return notification;
  }
}
