package nest.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class TraceTree extends Timber.HollowTree {

    private final DebugTree debugTree;

    public TraceTree(boolean useTraces) {
        debugTree = new DebugTree(useTraces);
    }

    @Override
    public void v(String message, Object... args) {
        debugTree.v(message, args);
    }

    @Override
    public void v(Throwable t, String message, Object... args) {
        debugTree.v(t, message, args);
    }

    @Override
    public void d(String message, Object... args) {
        debugTree.d(message, args);
    }

    @Override
    public void d(Throwable t, String message, Object... args) {
        debugTree.d(t, message, args);
    }

    @Override
    public void i(String message, Object... args) {
        debugTree.i(message, args);
    }

    @Override
    public void i(Throwable t, String message, Object... args) {
        debugTree.i(t, message, args);
    }

    @Override
    public void w(String message, Object... args) {
        debugTree.w(message, args);
    }

    @Override
    public void w(Throwable t, String message, Object... args) {
        debugTree.w(t, message, args);
    }

    @Override
    public void e(String message, Object... args) {
        debugTree.e(message, args);
    }

    @Override
    public void e(Throwable t, String message, Object... args) {
        debugTree.e(t, message, args);
    }

    private static class DebugTree extends Timber.DebugTree {

        private static final Pattern ANONYMOUS_CLASS = Pattern.compile("\\$\\d+$");
        private static final int STACK_POSITION = 6;

        private final boolean useTraces;
        private StackTraceElement lastTrace;

        private DebugTree(boolean useTraces) {
            this.useTraces = useTraces;
        }

        @Override
        protected String createTag() {
            String tag;
            if (!useTraces) {
                tag = nextTag();
                if (tag != null) {
                    return tag;
                }
            }

            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            if (stackTrace.length < STACK_POSITION) {
                return "---";
            }
            if (useTraces) {
                lastTrace = stackTrace[STACK_POSITION];
            }
            tag = stackTrace[STACK_POSITION].getClassName();
            Matcher m = ANONYMOUS_CLASS.matcher(tag);
            if (m.find()) {
                tag = m.replaceAll("");
            }
            return tag.substring(tag.lastIndexOf('.') + 1);
        }

        @Override
        protected void logMessage(int priority, String tag, String message) {
            if (lastTrace != null) {
                message = (TextUtils.isEmpty(message) ? "" : message +" ") + "at "+ lastTrace;
                lastTrace = null;
            }
            super.logMessage(priority, tag, message);
        }
    }
}
