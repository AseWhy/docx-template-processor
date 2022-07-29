package io.github.asewhy.support;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SubspaceEntry {
    private final String type = "subspace";
    private final DescriptionEntry description;
    private final Map<String, DescriptionEntry> tags;

    @Contract("_, _ -> new")
    public static @NotNull SubspaceEntry of(DescriptionEntry description, Map<String, DescriptionEntry> tags) {
        return new SubspaceEntry(description, tags);
    }
}
