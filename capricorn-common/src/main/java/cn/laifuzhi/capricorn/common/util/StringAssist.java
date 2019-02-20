package cn.laifuzhi.capricorn.common.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class StringAssist {
    private static final Splitter SPLITTER_COMMA = Splitter.on(",");
    private static final Splitter SPLITTER_UNDERLINE = Splitter.on("_");
    private static final Joiner JOINER_COMMA = Joiner.on(",");
    private static final Joiner JOINER_UNDERLINE = Joiner.on("_");

    public static @Nonnull List<String> splitComma(@Nullable String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        return SPLITTER_COMMA.trimResults().omitEmptyStrings().splitToList(text);
    }

    public static @Nonnull List<String> splitUnderline(@Nullable String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        return SPLITTER_UNDERLINE.trimResults().omitEmptyStrings().splitToList(text);
    }

    public static @Nonnull String joinComma(String...params) {
        if (ArrayUtils.isEmpty(params)) {
            return StringUtils.EMPTY;
        }
        return JOINER_COMMA.join(params);
    }

    public static @Nonnull String joinComma(@Nullable Collection<String> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return StringUtils.EMPTY;
        }
        return JOINER_COMMA.join(collection);
    }

    public static @Nonnull String joinUnderline(String...params) {
        if (ArrayUtils.isEmpty(params)) {
            return StringUtils.EMPTY;
        }
        return JOINER_UNDERLINE.join(params);
    }

    public static @Nonnull String joinUnderline(@Nullable Collection<String> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return StringUtils.EMPTY;
        }
        return JOINER_UNDERLINE.join(collection);
    }

}
