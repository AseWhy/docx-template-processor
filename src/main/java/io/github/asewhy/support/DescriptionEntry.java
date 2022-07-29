package io.github.asewhy.support;

import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DescriptionEntry {
    private final String type = "description";
    private final String text;
    private final Class<?> group;

    @Contract("_, _ -> new")
    public static @NotNull DescriptionEntry of(String text,  Class<?> group) {
        return new DescriptionEntry(text, group);
    }
}
