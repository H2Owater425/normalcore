package kr.dhmo.normalcore.utilities;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

final public class FormatUtility {
    @Contract(pure = true)
    public static @NotNull String pluralize(long count, String singular) {
        return count + " " + (-1 <= count && count <= 1 ? singular : singular + "s");
    }

//    @Contract(pure = true)
//    public static @NotNull String pluralize(long count, String singular, String plural) {
//        return count + " " + (-1 <= count && count <= 1 ? singular : plural);
//    }

    public static @NotNull String approximateTime(long time) {
        time /= 1000;

        long unit = time / 86400;

        if(unit == 0) {
            unit = time / 3600;

            if(unit == 0) {
                unit = time / 60;

                if(unit == 0) {
                    return FormatUtility.pluralize(time, "second");
                } else {
                    return FormatUtility.pluralize(unit, "minute");
                }
            } else {
                return FormatUtility.pluralize(unit, "hour");
            }
        } else {
            return FormatUtility.pluralize(unit, "day");
        }
    }
}
