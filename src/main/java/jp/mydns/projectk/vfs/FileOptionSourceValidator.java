/*
 * Copyright (c) 2023, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jp.mydns.projectk.vfs;

import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import static jakarta.json.JsonValue.ValueType.FALSE;
import static jakarta.json.JsonValue.ValueType.TRUE;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

/**
 * Source value validator for {@link FileOption}.
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class has not variable field member and it has all method is static.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileOptionSourceValidator {

    private static final String MSG_TEMPLATE = "FileOption value of [%s] must be %s.";

    private FileOptionSourceValidator() {
    }

    /**
     * Validate if source value for {@link FileOption} can be converted to {@code boolean}.
     *
     * @param booleanValue boolean as JSON
     * @param optionName name of {@code FileOption}. Used in message if occurs {@code IllegalArgumentException}
     * @return {@code booleanValue} as {@code boolean}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code booleanValue} is not convertible to {@code boolean}
     * @since 1.0.0
     */
    public static boolean requireBoolean(JsonValue booleanValue, String optionName) {

        Objects.requireNonNull(booleanValue);
        Objects.requireNonNull(optionName);

        return switch (booleanValue.getValueType()) {

            case TRUE ->
                true;

            case FALSE ->
                false;

            default ->
                throw new IllegalArgumentException(MSG_TEMPLATE.formatted(optionName, "boolean"));

        };

    }

    /**
     * Validate if source value for {@link FileOption} can be converted to {@code int}.
     *
     * @param intValue int as JSON
     * @param optionName name of {@code FileOption}. Used in message if occurs {@code IllegalArgumentException}
     * @return {@code intValue} as {@code int}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code intValue} is not convertible to {@code int}
     * @since 1.0.0
     */
    public static int requireInt(JsonValue intValue, String optionName) {

        Objects.requireNonNull(intValue);
        Objects.requireNonNull(optionName);

        try {

            return JsonNumber.class.cast(intValue).intValueExact();

        } catch (ClassCastException | ArithmeticException ex) {

            throw new IllegalArgumentException(MSG_TEMPLATE.formatted(optionName, "int"), ex);

        }

    }

    /**
     * Validate if source value for {@link FileOption} can be converted to {@code long}.
     *
     * @param longValue long as JSON
     * @param optionName name of {@code FileOption}. Used in message if occurs {@code IllegalArgumentException}
     * @return {@code longValue} as {@code long}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code longValue} is not convertible to {@code long}
     * @since 1.0.0
     */
    public static long requireLong(JsonValue longValue, String optionName) {

        Objects.requireNonNull(longValue);
        Objects.requireNonNull(optionName);

        try {

            return JsonNumber.class.cast(longValue).longValueExact();

        } catch (ClassCastException | ArithmeticException ex) {

            throw new IllegalArgumentException(MSG_TEMPLATE.formatted(optionName, "long"), ex);

        }

    }

    /**
     * Validate if source value for {@link FileOption} can be converted to {@code String}.
     *
     * @param stringValue string as JSON
     * @param optionName name of {@code FileOption}. Used in message if occurs {@code IllegalArgumentException}
     * @return {@code stringValue} as {@code String}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code stringValue} is not convertible to {@code String}
     * @since 1.0.0
     */
    public static String requireString(JsonValue stringValue, String optionName) {

        Objects.requireNonNull(stringValue);
        Objects.requireNonNull(optionName);

        try {

            return JsonString.class.cast(stringValue).getString();

        } catch (ClassCastException ex) {

            throw new IllegalArgumentException(MSG_TEMPLATE.formatted(optionName, "string"), ex);

        }

    }

    /**
     * Validate if source value for {@link FileOption} can be converted to {@code Duration}.
     *
     * @param durationValue duration as JSON
     * @param optionName name of {@code FileOption}. Used in message if occurs {@code IllegalArgumentException}
     * @return {@code durationValue} as {@code Duration}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code durationValue} is not convertible to {@code Duration}
     * @since 1.0.0
     */
    public static Duration requireDuration(JsonValue durationValue, String optionName) {

        Objects.requireNonNull(durationValue);
        Objects.requireNonNull(optionName);

        try {

            return Duration.parse(JsonString.class.cast(durationValue).getString());

        } catch (ClassCastException | DateTimeParseException ex) {

            throw new IllegalArgumentException(MSG_TEMPLATE.formatted(optionName, "duration"), ex);

        }

    }

    /**
     * Validate if source value for {@link FileOption} can be converted to {@code List<String>}.
     *
     * @param stringValues list of string as JSON
     * @param optionName name of {@code FileOption}. Used in message if occurs {@code IllegalArgumentException}
     * @return {@code stringValues} as {@code List<String>}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code stringValues} is not convertible to {@code List<String>}
     * @since 1.0.0
     */
    public static List<String> requireStringList(JsonValue stringValues, String optionName) {

        Objects.requireNonNull(stringValues);
        Objects.requireNonNull(optionName);

        try {

            return stringValues.asJsonArray().stream().map(JsonString.class::cast).map(JsonString::getString).toList();

        } catch (ClassCastException | IllegalArgumentException | NullPointerException ex) {

            throw new IllegalArgumentException(MSG_TEMPLATE.formatted(optionName, "list of string"), ex);

        }

    }

    /**
     * Validate if source value for {@link FileOption} can be converted to {@code List<Integer>}.
     *
     * @param intValues list of int as JSON
     * @param optionName name of {@code FileOption}. Used in message if occurs {@code IllegalArgumentException}
     * @return {@code intValues} as {@code List<Integer>}
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code intValues} is not convertible to {@code List<Integer>}
     * @since 1.0.0
     */
    public static List<Integer> requireIntList(JsonValue intValues, String optionName) {

        Objects.requireNonNull(intValues);
        Objects.requireNonNull(optionName);

        try {

            return intValues.asJsonArray().stream().map(JsonNumber.class::cast).map(JsonNumber::intValueExact).toList();

        } catch (ClassCastException | IllegalArgumentException | NullPointerException | ArithmeticException ex) {

            throw new IllegalArgumentException(MSG_TEMPLATE.formatted(optionName, "list of int"), ex);

        }

    }

}
