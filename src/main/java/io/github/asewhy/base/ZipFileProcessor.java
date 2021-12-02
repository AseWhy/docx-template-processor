package io.github.asewhy.base;

import org.jetbrains.annotations.NotNull;
import io.github.asewhy.support.interfaces.iMacrosProcessor;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class ZipFileProcessor {
    protected ByteArrayOutputStream process(@NotNull Path path, @NotNull iMacrosProcessor processor) throws Exception {
        var output = new ByteArrayOutputStream();
        process(path, processor, output);
        return output;
    }

    protected ByteArrayOutputStream process(String path, @NotNull iMacrosProcessor processor) throws Exception {
        var output = new ByteArrayOutputStream();
        process(path, processor, output);
        return output;
    }

    protected ByteArrayOutputStream process(File source, @NotNull iMacrosProcessor processor) throws Exception {
        var output = new ByteArrayOutputStream();
        process(source, processor, output);
        return output;
    }

    protected void process(@NotNull Path path, @NotNull iMacrosProcessor processor, OutputStream output) throws Exception {
        process(path.toFile(), processor, output);
    }

    protected void process(@NotNull String path, @NotNull iMacrosProcessor processor, OutputStream output) throws Exception {
        process(new File(path), processor, output);
    }

    protected void process(File source, @NotNull iMacrosProcessor processor, OutputStream output) throws Exception {
        var filesToRestore = new HashMap<String, byte[]>();
        var zipFileReader = new ByteArrayOutputStream();
        var entry = (ZipEntry) null;

        try(var zipInputStream = new ZipInputStream(new FileInputStream(source))) {
            while ((entry = zipInputStream.getNextEntry()) != null) {
                var name = entry.getName();

                zipFileReader.reset();

                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    zipFileReader.write(c);
                }

                var buffer = zipFileReader.toByteArray();

                if (processor.canProcess(name, buffer)) {
                    zipFileReader.reset();

                    processor.doProcess(zipFileReader, buffer);

                    buffer = zipFileReader.toByteArray();
                }

                filesToRestore.put(name, buffer);
                zipInputStream.closeEntry();
            }
        }

        try(var zipOutputStream = new ZipOutputStream(output)) {
            for(var current: filesToRestore.entrySet()) {
                var currentFile = new ZipEntry(current.getKey());
                zipOutputStream.putNextEntry(currentFile);
                zipOutputStream.write(current.getValue());
                zipOutputStream.closeEntry();
            }
        }
    }
}
