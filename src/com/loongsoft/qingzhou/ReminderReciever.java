/* 
 *  Copyright 2012 Loong H
 * 
 *  Qingzhou is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Qingzhou is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.loongsoft.qingzhou;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ReminderReciever extends BroadcastReceiver {
	public final static int NOTIFICATION_ID = 1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, SplashActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationManager nm=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);  

        Notification notif=new Notification();  
        notif.tickerText = context.getResources().getString(R.string.status_bar_notification_ticker_text);
        notif.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), 
        		context.getResources().getString(R.string.status_bar_notification_text), null);

        // This is who should be launched if the user selects our notification.
        notif.contentIntent = contentIntent;

        // the icon for the status bar
        notif.icon = R.drawable.ic_launcher;

        // Post a notification to be shown in the status bar  
        nm.notify(NOTIFICATION_ID, notif);
	}

}
