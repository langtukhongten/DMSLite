package CommonLib;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.LinkedList;

/**
 * Created by My PC on 26/11/2015.
 */
public class EventPool {
    static Context context;
    private static EventPool instance_control = null, instance_view = null;

    private EventPool() {
    }

    public void InitContext(Context context) {
        if (this.context == null)
            this.context = context;
    }

    public synchronized static EventPool control() {
        if (instance_control == null) {
            instance_control = new EventPool();
        }
        return instance_control;
    }

    public synchronized static EventPool view() {
        if (instance_view == null) {
            instance_view = new EventPool();
        }
        return instance_view;
    }

    private LinkedList<EventType.EventBase> events = new LinkedList<EventType.EventBase>();

    public void enQueue(EventType.EventBase event) {
        synchronized (this) {
            events.add(event);
            if (this.context != null) {
                if (instance_control != null) {
                    if (instance_control.events.size() > 0)
                        sendBroadcast(this.context);
                }
                if (instance_view != null) {
                    if (instance_view.events.size() > 0)
                        sendBroadcastView(this.context);
                }
            }

        }
    }

    public static String BROADCAST_ACTION_CVIEW = "Forms.OnEvent.C2View";

    public void sendBroadcastView(Context context) {
        Intent broadcast = new Intent("OnFire");
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
    }

    public static String BROADCAST_ACTION = "Forms.OnEvent";

    public void sendBroadcast(Context context) {
        Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION);
        context.sendBroadcast(broadcast);
    }

    public EventType.EventBase deQueue() {
        synchronized (this) {
            return events.poll();
        }
    }
}
