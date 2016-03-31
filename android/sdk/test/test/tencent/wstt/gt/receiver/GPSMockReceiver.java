package test.tencent.wstt.gt.receiver;

import com.tencent.wstt.gt.client.GT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSMockReceiver extends BroadcastReceiver {
	public static final String ACTION_GPS_MOCK = "com.tencent.wstt.gt.ACTION_GPS_MOCK";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String type = intent.getStringExtra("type");
		if (! ACTION_GPS_MOCK.equals(action))
		{
			return;
		}
		if (null != type && "start".equals(type))
		{
			GT.logD("gpsmock", "start...");
		}
		else if (null != type && "end".equals(type))
		{
			GT.logD("gpsmock", "end...");
		}
		else if (null != type && "stop".equals(type))
		{
			GT.logD("gpsmock", "stop...");
		}
	}
}
