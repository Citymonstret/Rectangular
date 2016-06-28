package com.intellectualsites.rectangular.util;

import com.intellectualsites.rectangular.player.RectangularPlayer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ChatUtil {

    public static void sendStacktrace(RectangularPlayer player, Throwable throwable) {
        player.sendMessage(throwable.toString());
        StackTraceElement[] trace = throwable.getStackTrace();
        for (StackTraceElement traceElement : trace) {
            player.sendMessage("  at " + traceElement.toString());
        }
        Throwable[] suppressed = throwable.getSuppressed();
        if (suppressed.length > 0) {
            player.sendMessage("Suppressed: ");
        }
        for (Throwable se : suppressed) {
            trace = se.getStackTrace();
            player.sendMessage(se.toString());
            for (StackTraceElement traceElement : trace) {
                player.sendMessage("  at " + traceElement.toString());
            }
        }
        Throwable cause = throwable.getCause();
        if (cause != null) {
            player.sendMessage("Cause: ");
            trace = cause.getStackTrace();
            for (StackTraceElement traceElement : trace) {
                player.sendMessage("  at " + traceElement.toString());
            }
        }
    }

}
