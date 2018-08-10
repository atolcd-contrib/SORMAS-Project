package de.symeda.sormas.app.core.notification;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.ViewDataBinding;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.core.NotificationContext;

public class NotificationHelper {

    public static void showNotification(ViewDataBinding binding, NotificationType type, int messageResId) {
        showNotification(binding, NotificationPosition.TOP, type, messageResId);
    }

    public static void showNotification(ViewDataBinding binding, NotificationType type, String message) {
        showNotification(binding, NotificationPosition.TOP, type, message);
    }

    public static void showNotification(NotificationContext communicator, NotificationType type, String message) {
        showNotification(communicator, NotificationPosition.TOP, type, message);
    }

    public static void showNotification(NotificationContext communicator, NotificationType type, int messageResId) {
        showNotification(communicator, NotificationPosition.TOP, type, messageResId);
    }

    public static void hideNotification(View notificationRoot) {
        View view = notificationRoot;

        if (notificationRoot.getId() != R.id.notificationFrame)
            view = (LinearLayout) notificationRoot.findViewById(R.id.notificationFrame);

        if (view == null)
            return;

        view.setVisibility(View.GONE);
    }

    public static void hideNotification(NotificationContext communicator) {
        View rootView = communicator.getRootView();

        if (rootView == null)
            return;

        View view = rootView;

        if (rootView.getId() != R.id.notificationFrame)
            view = (LinearLayout) rootView.findViewById(R.id.notificationFrame);

        if (view == null)
            return;

        view.setVisibility(View.GONE);
    }

    public static void showNotification(View notificationFrame, NotificationType type, String message) {
        showNotification(notificationFrame, NotificationPosition.TOP, type, message);
    }

    private static void showNotification(View notificationFrame, TextView tvNotificationMessage, NotificationPosition position, NotificationType type, String message) {
        if (notificationFrame == null)
            return;

        if (tvNotificationMessage == null)
            return;

        Resources resources = notificationFrame.getResources();

        int backgroundColor = resources.getColor(type.getBackgroundColor());
        int textColor = resources.getColor(type.getTextColor());

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) notificationFrame.getLayoutParams();

        if (position.equals(NotificationPosition.TOP))
            layoutParams.gravity = Gravity.TOP;

        if (position.equals(NotificationPosition.BOTTOM))
            layoutParams.gravity = Gravity.BOTTOM;

        notificationFrame.setOnClickListener(new View.OnClickListener() {
            private WeakReference<View> nf;

            @Override
            public void onClick(View v) {
                if (nf.get() != null) hideNotification(nf.get());
            }

            private View.OnClickListener init(View nf) {
                this.nf = new WeakReference<>(nf);
                return this;
            }
        }.init(notificationFrame));

        notificationFrame.setBackgroundColor(backgroundColor);
        tvNotificationMessage.setTextColor(textColor);
        tvNotificationMessage.setText(Html.fromHtml(message));

        notificationFrame.setVisibility(View.VISIBLE);

        if (type == NotificationType.INFO || type == NotificationType.SUCCESS) {
            notificationFrame.postDelayed(new Runnable() {
                private WeakReference<View> nf;

                public void run() {
                    if (nf.get() != null) hideNotification(nf.get());
                }

                private Runnable init(View nf) {
                    this.nf = new WeakReference<>(nf);
                    return this;
                }
            }.init(notificationFrame), 3000);
        }
    }

    private static void showNotification(View notificationRoot, NotificationPosition position, NotificationType type, String message) {
        LinearLayout notificationFrame = (LinearLayout) notificationRoot.findViewById(R.id.notificationFrame);
        TextView tvNotificationMessage = (TextView) notificationRoot.findViewById(R.id.tvNotificationMessage);
        showNotification(notificationFrame, tvNotificationMessage, position, type, message);
    }

    public static void showNotification(NotificationContext communicator, NotificationPosition position, NotificationType type, int messageResId) {
        showNotification(communicator.getRootView(), position, type, communicator.getRootView().getResources().getString(messageResId));
    }

    public static void showNotification(NotificationContext communicator, NotificationPosition position, NotificationType type, String message) {
        showNotification(communicator.getRootView(), position, type, message);
    }

    public static void showNotification(ViewDataBinding binding, NotificationPosition position, NotificationType type, int messageResId) {
        showNotification(binding.getRoot(), position, type, binding.getRoot().getResources().getString(messageResId));
    }

    public static void showNotification(ViewDataBinding binding, NotificationPosition position, NotificationType type, String message) {
        showNotification(binding.getRoot(), position, type, message);
    }

    //Second Set
    public static void showNotification(View notificationFrame, TextView tvNotificationMessage, NotificationPosition position, NotificationType type, int messageResId) {
        if (messageResId <= 0)
            return;

        if (notificationFrame == null)
            return;

        Resources resources = notificationFrame.getResources();

        showNotification(notificationFrame, tvNotificationMessage, position, type, resources.getString(messageResId));
    }

    public static void showNotification(View notificationFrame, TextView tvNotificationMessage, NotificationType type, int messageResId) {
        showNotification(notificationFrame, tvNotificationMessage, NotificationPosition.TOP, type, messageResId);
    }

    private static void showDialogNotification(View notificationFrame, TextView tvNotificationMessage, NotificationPosition position, NotificationType type, String message) {
        if (notificationFrame == null)
            return;

        if (tvNotificationMessage == null)
            return;

        Resources resources = notificationFrame.getResources();

        int backgroundColor = resources.getColor(type.getInverseBackgroundColor());
        int textColor = resources.getColor(type.getInverseTextColor());

        notificationFrame.setOnClickListener(new View.OnClickListener() {
            private WeakReference<View> nf;

            @Override
            public void onClick(View v) {
                if (nf.get() != null) hideNotification(nf.get());
            }

            private View.OnClickListener init(View nf) {
                this.nf = new WeakReference<>(nf);
                return this;
            }
        }.init(notificationFrame));

        LayerDrawable drawable = (LayerDrawable) resources.getDrawable(R.drawable.background_full_width_border);
        Drawable backgroundLayer = drawable.findDrawableByLayerId(R.id.backgroundLayer);
        backgroundLayer.setColorFilter(backgroundColor, PorterDuff.Mode.SRC_OVER);

        tvNotificationMessage.setTextColor(textColor);
        tvNotificationMessage.setText(Html.fromHtml(message));

        notificationFrame.setBackground(drawable);
        notificationFrame.setVisibility(View.VISIBLE);

        if (type == NotificationType.INFO || type == NotificationType.SUCCESS) {
            notificationFrame.postDelayed(new Runnable() {
                private WeakReference<View> nf;

                public void run() {
                    if (nf.get() != null) hideNotification(nf.get());
                }

                private Runnable init(View nf) {
                    this.nf = new WeakReference<>(nf);
                    return this;
                }
            }.init(notificationFrame), 3000);
        }
    }

    private static void showDialogNotification(View notificationRoot, NotificationPosition position, NotificationType type, String message) {
        LinearLayout notificationFrame = (LinearLayout) notificationRoot.findViewById(R.id.notificationFrame);
        TextView tvNotificationMessage = (TextView) notificationRoot.findViewById(R.id.tvNotificationMessage);

        if (notificationFrame == null)
            return;

        if (tvNotificationMessage == null)
            return;

        showDialogNotification(notificationFrame, tvNotificationMessage, position, type, message);
    }

    public static void showDialogNotification(NotificationContext communicator, NotificationPosition position, NotificationType type, int messageResId) {
        View rootView = communicator.getRootView();

        if (rootView == null)
            return;

        Context context = rootView.getContext();

        if (context == null)
            return;

        Resources resources = context.getResources();

        if (resources == null)
            return;

        showDialogNotification(communicator.getRootView(), position, type, resources.getString(messageResId));
    }

    public static void showDialogNotification(NotificationContext communicator, NotificationPosition position, NotificationType type, String message) {
        View rootView = communicator.getRootView();

        if (rootView == null)
            return;

        showDialogNotification(rootView, position, type, message);
    }

    public static void showDialogNotification(ViewDataBinding binding, NotificationPosition position, NotificationType type, int messageResId) {
        if (messageResId <= 0)
            return;

        if (binding == null)
            return;

        View notificationRoot = binding.getRoot();

        if (notificationRoot == null)
            return;

        Resources resources = notificationRoot.getResources();
        String message = resources.getString(messageResId);

        showDialogNotification(binding.getRoot(), position, type, message);
    }

    public static void showDialogNotification(ViewDataBinding binding, NotificationPosition position, NotificationType type, String message) {
        if (binding == null)
            return;

        showDialogNotification(binding.getRoot(), position, type, message);
    }


    public static void showDialogNotification(View notificationFrame, TextView tvNotificationMessage, NotificationPosition position, NotificationType type, int messageResId) {
        if (messageResId <= 0)
            return;

        if (notificationFrame == null)
            return;

        Resources resources = notificationFrame.getResources();

        showDialogNotification(notificationFrame, tvNotificationMessage, position, type, resources.getString(messageResId));
    }

    public static void showDialogNotification(View notificationFrame, TextView tvNotificationMessage, NotificationType type, int messageResId) {
        showDialogNotification(notificationFrame, tvNotificationMessage, NotificationPosition.TOP, type, messageResId);
    }


    //Mainly called from outside
    public static void showDialogNotification(ViewDataBinding binding, NotificationType type, int messageResId) {
        showDialogNotification(binding, NotificationPosition.TOP, type, messageResId);
    }

    //Mainly called from outside
    public static void showDialogNotification(ViewDataBinding binding, NotificationType type, String message) {
        showDialogNotification(binding, NotificationPosition.TOP, type, message);
    }

    //Mainly called from outside
    public static void hideDialogNotification(ViewDataBinding binding) {
        if (binding == null)
            return;

        hideDialogNotification(binding.getRoot());
    }

    public static void hideDialogNotification(View notificationRoot) {
        View view = notificationRoot;

        if (notificationRoot.getId() != R.id.notificationFrame)
            view = (LinearLayout) notificationRoot.findViewById(R.id.notificationFrame);

        if (view == null)
            return;

        view.setVisibility(View.GONE);
    }

    //Mainly called from outside
    public static void showDialogNotification(NotificationContext communicator, NotificationType type, String message) {
        showDialogNotification(communicator, NotificationPosition.TOP, type, message);
    }

    //Mainly called from outside
    public static void showDialogNotification(NotificationContext communicator, NotificationType type, int messageResId) {
        showDialogNotification(communicator, NotificationPosition.TOP, type, messageResId);
    }

    //Mainly called from outside
    public static void hideDialogNotification(NotificationContext communicator) {
        View rootView = communicator.getRootView();

        if (rootView == null)
            return;

        View view = rootView;

        if (rootView.getId() != R.id.notificationFrame)
            view = (LinearLayout) rootView.findViewById(R.id.notificationFrame);

        if (view == null)
            return;

        view.setVisibility(View.GONE);
    }
}