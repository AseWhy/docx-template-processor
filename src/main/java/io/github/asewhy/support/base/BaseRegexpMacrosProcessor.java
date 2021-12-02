package io.github.asewhy.support.base;

import io.github.asewhy.support.exceptions.ProcessorException;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
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

    protected String supply(Matcher matcher) {
        return matcher.group(getCaptureGroup());
    }

    protected String matcher(Matcher matcher) throws Exception {
        return getMacros(supply(matcher));
    }

    @Override
    public void doProcessLoggable(OutputStream output, byte[] bytes) throws ProcessorException {
        var input = new String(bytes);
        var result = new StringBuilder();

        try {
            if(input.length() > 0){
                var matcher = regex.matcher(input);

                while (matcher.find()) {
                    matcher.appendReplacement(result, this.matcher(matcher));
                }

                matcher.appendTail(result);
            }

            output.write(result.toString().getBytes());
        } catch (Exception e) {
            throw new ProcessorException(e);
        }
    }

    @Override
    public List<String> doValidateLoggable(byte[] bytes) throws ProcessorException {
        var tags = new ArrayList<String>();
        var input = new String(bytes);
        var result = new StringBuilder();

        try {
            if(input.length() > 0){
                var matcher = regex.matcher(input);

                while (matcher.find()) {
                    tags.add(this.supply(matcher));
                }
            }
        } catch (Exception e) {
            throw new ProcessorException(e);
        }

        return tags;
    }
}
