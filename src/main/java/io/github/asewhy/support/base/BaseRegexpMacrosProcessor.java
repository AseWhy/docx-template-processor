package io.github.asewhy.support.base;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public abstract class BaseRegexpMacrosProcessor extends BaseDocxProcessor {
    private final Pattern regex;

    public BaseRegexpMacrosProcessor(String regex) {
        this.regex = Pattern.compile(regex);
    }

    public BaseRegexpMacrosProcessor(Pattern regex) {
        this.regex = regex;
    }

    protected abstract String getMacros(String key) throws IllegalAccessException, InvocationTargetException;

    protected abstract int getCaptureGroup();

    protected String matcher(Matcher matcher) throws Exception {
        return getMacros(matcher.group(getCaptureGroup()));
    }

    @Override
    public void doProcessLoggable(OutputStream output, byte[] bytes) throws Exception {
        var input = new String(bytes);
        var result = new StringBuilder();

        if(input.length() > 0){
            var matcher = regex.matcher(input);

            while (matcher.find()) {
                matcher.appendReplacement(result, this.matcher(matcher));
            }

            matcher.appendTail(result);
        }

        output.write(result.toString().getBytes());
    }
}
