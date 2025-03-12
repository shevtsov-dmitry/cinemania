package ru.storage.utils;

import ru.storage.exceptions.ParseIdException;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class with helper methods for parsing custom predefined project standards.
 *
 */
public class ProjectStandardUtils {

    private ProjectStandardUtils() {

    }

    /**
     * Parse request ids into a set of unique strings.
     *
     * @param ids ids to parse
     * @return list of unique strings
     * @throws ParseIdException when ids are invalid or empty
     */
    public static List<String> parseIdsFromString(String ids) {
        List<String> parsedIds =
                Arrays.stream(ids.split(","))
                        .map(String::trim)
                        .distinct()
                        .toList();
        if (parsedIds.isEmpty()) {
            throw new ParseIdException();
        }
        return parsedIds;
    }
}
