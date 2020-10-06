public class LocationService extends Service {

    //notifications
    public static PendingIntent pendingIntent;
    public static PendingIntent pendingCloseIntent;

    private static final int NOTIFICATION_ID = 100;

    Notification notification;
    NotificationManager notificationManager;

    private static final String CHANNEL_ID = "location_notification_channel_id";
    private static final String CHANNEL_NAME = "Location Notification Service";

    Context context;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = getApplicationContext();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //open main activity when clicked
        pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);

        //action when notification button clicked
        Intent intentAction = new Intent(context, ActionReceiver.class);
        intentAction.putExtra("location_service","service_notification");
        pendingCloseIntent = PendingIntent.getBroadcast(context,0, intentAction, PendingIntent.FLAG_UPDATE_CURRENT);

        setNotification();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //create foreground service
            startForeground(NOTIFICATION_ID, notification);
            pushLocation(intent);
        } else {
            notificationManager.notify(NOTIFICATION_ID, notification);
            pushLocation(intent);
        }

        return LocationService.START_STICKY;
    }

    private void setNotification() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Running")
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("Running")
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .setOngoing(true)
                    .build();

        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N | Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Running")
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("Running")
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .setOngoing(true)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setImportance(NotificationManager.IMPORTANCE_NONE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Running")
                    .setPriority(PRIORITY_MIN)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker("Running")
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .setOngoing(true)
                    .build();
        }
    }
}
