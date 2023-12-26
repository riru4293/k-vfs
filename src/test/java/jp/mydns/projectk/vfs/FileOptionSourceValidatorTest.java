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

import jakarta.json.Json;
import jakarta.json.JsonValue;
import java.math.BigDecimal;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import org.junit.jupiter.api.Test;

/**
 * Test of class FileOptionSourceValidator.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class FileOptionSourceValidatorTest {

    /**
     * Test of requireBoolean method. If valid.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireBoolean() {

        assertThat(FileOptionSourceValidator.requireBoolean(JsonValue.TRUE, "true")).isTrue();

        assertThat(FileOptionSourceValidator.requireBoolean(JsonValue.FALSE, "false")).isFalse();

    }

    /**
     * Test of requireBoolean method. If invalid type.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireBoolean_InvalidType() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireBoolean(JsonValue.NULL, "X"))
                .withMessage("FileOption value of [X] must be boolean.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireBoolean(Json.createValue(0), "X"))
                .withMessage("FileOption value of [X] must be boolean.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireBoolean(Json.createValue(""), "X"))
                .withMessage("FileOption value of [X] must be boolean.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireBoolean(JsonValue.EMPTY_JSON_ARRAY, "X"))
                .withMessage("FileOption value of [X] must be boolean.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireBoolean(JsonValue.EMPTY_JSON_OBJECT, "X"))
                .withMessage("FileOption value of [X] must be boolean.");

    }

    /**
     * Test of requireBoolean method. If null.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireBoolean_Null() {

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireBoolean(null, "X"));

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireBoolean(JsonValue.NULL, null));

    }

    /**
     * Test of requireInt method. If valid.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireInt() {

        assertThat(FileOptionSourceValidator.requireInt(Json.createValue(Integer.MIN_VALUE), "min"))
                .isEqualTo(Integer.MIN_VALUE);

        assertThat(FileOptionSourceValidator.requireInt(Json.createValue(Integer.MAX_VALUE), "max"))
                .isEqualTo(Integer.MAX_VALUE);

    }

    /**
     * Test of requireInt method. If invalid type.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireInt_InvalidType() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(JsonValue.NULL, "X"))
                .withMessage("FileOption value of [X] must be int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(JsonValue.TRUE, "X"))
                .withMessage("FileOption value of [X] must be int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(JsonValue.FALSE, "X"))
                .withMessage("FileOption value of [X] must be int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(Json.createValue(""), "X"))
                .withMessage("FileOption value of [X] must be int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(JsonValue.EMPTY_JSON_ARRAY, "X"))
                .withMessage("FileOption value of [X] must be int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(JsonValue.EMPTY_JSON_OBJECT, "X"))
                .withMessage("FileOption value of [X] must be int.");

    }

    /**
     * Test of requireInt method. If too many.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireInt_TooMany() {

        JsonValue tooMany = Json.createValue(BigDecimal.valueOf(Integer.MAX_VALUE).add(BigDecimal.ONE));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(tooMany, "X"))
                .withMessage("FileOption value of [X] must be int.");

    }

    /**
     * Test of requireInt method. If too few.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireInt_TooFew() {

        JsonValue tooFew = Json.createValue(BigDecimal.valueOf(Integer.MIN_VALUE).subtract(BigDecimal.ONE));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(tooFew, "X"))
                .withMessage("FileOption value of [X] must be int.");

    }

    /**
     * Test of requireInt method. If null.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireInt_Null() {

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(null, "X"));

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireInt(JsonValue.NULL, null));

    }

    /**
     * Test of requireLong method. If valid.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireLong() {

        assertThat(FileOptionSourceValidator.requireLong(Json.createValue(Long.MIN_VALUE), "min"))
                .isEqualTo(Long.MIN_VALUE);

        assertThat(FileOptionSourceValidator.requireLong(Json.createValue(Long.MAX_VALUE), "max"))
                .isEqualTo(Long.MAX_VALUE);

    }

    /**
     * Test of requireLong method. If invalid type.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireLong_InvalidType() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(JsonValue.NULL, "X"))
                .withMessage("FileOption value of [X] must be long.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(JsonValue.TRUE, "X"))
                .withMessage("FileOption value of [X] must be long.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(JsonValue.FALSE, "X"))
                .withMessage("FileOption value of [X] must be long.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(Json.createValue(""), "X"))
                .withMessage("FileOption value of [X] must be long.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(JsonValue.EMPTY_JSON_ARRAY, "X"))
                .withMessage("FileOption value of [X] must be long.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(JsonValue.EMPTY_JSON_OBJECT, "X"))
                .withMessage("FileOption value of [X] must be long.");

    }

    /**
     * Test of requireLong method. If too many.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireLong_TooMany() {

        JsonValue tooMany = Json.createValue(BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.ONE));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(tooMany, "X"))
                .withMessage("FileOption value of [X] must be long.");

    }

    /**
     * Test of requireLong method. If too few.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireLong_TooFew() {

        JsonValue tooFew = Json.createValue(BigDecimal.valueOf(Long.MIN_VALUE).subtract(BigDecimal.ONE));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(tooFew, "X"))
                .withMessage("FileOption value of [X] must be long.");

    }

    /**
     * Test of requireLong method. If null.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireLong_Null() {

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(null, "X"));

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireLong(JsonValue.NULL, null));

    }

    /**
     * Test of requireString method. If valid.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireString() {

        assertThat(FileOptionSourceValidator.requireString(Json.createValue(""), "E")).isEmpty();

        assertThat(FileOptionSourceValidator.requireString(Json.createValue("Hello World"), "H"))
                .isEqualTo("Hello World");

    }

    /**
     * Test of requireString method. If invalid type.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireString_InvalidType() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireString(JsonValue.NULL, "X"))
                .withMessage("FileOption value of [X] must be string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireString(JsonValue.TRUE, "X"))
                .withMessage("FileOption value of [X] must be string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireString(JsonValue.FALSE, "X"))
                .withMessage("FileOption value of [X] must be string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireString(Json.createValue(0), "X"))
                .withMessage("FileOption value of [X] must be string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireString(JsonValue.EMPTY_JSON_ARRAY, "X"))
                .withMessage("FileOption value of [X] must be string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireString(JsonValue.EMPTY_JSON_OBJECT, "X"))
                .withMessage("FileOption value of [X] must be string.");

    }

    /**
     * Test of requireString method. If null.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireString_Null() {

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireString(null, "X"));

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireString(JsonValue.NULL, null));

    }

    /**
     * Test of requireDuration method. If valid.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireDuration() {

        assertThat(FileOptionSourceValidator.requireDuration(Json.createValue("PT3S"), "3s"))
                .isEqualTo(Duration.ofSeconds(3));

    }

    /**
     * Test of requireDuration method. If invalid type.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireDuration_InvalidType() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(JsonValue.NULL, "X"))
                .withMessage("FileOption value of [X] must be duration.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(JsonValue.TRUE, "X"))
                .withMessage("FileOption value of [X] must be duration.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(JsonValue.FALSE, "X"))
                .withMessage("FileOption value of [X] must be duration.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(Json.createValue(0), "X"))
                .withMessage("FileOption value of [X] must be duration.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(JsonValue.EMPTY_JSON_ARRAY, "X"))
                .withMessage("FileOption value of [X] must be duration.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(JsonValue.EMPTY_JSON_OBJECT, "X"))
                .withMessage("FileOption value of [X] must be duration.");

    }

    /**
     * Test of requireDuration method. If invalid as duration.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireDuration_InvalidAsDuration() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(Json.createValue("P3Y"), "3sec"))
                .withMessage("FileOption value of [3sec] must be duration.");

    }

    /**
     * Test of requireDuration method. If null.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireDuration_Null() {

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(null, "X"));

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(JsonValue.NULL, null));

    }

    /**
     * Test of requireDuration method. If negative.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireDuration_Negative() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireDuration(Json.createValue("-PT1S"), "negative"))
                .withMessage("FileOption value of [negative] must be duration.")
                .withStackTraceContaining("Must not be negative.");

    }

    /**
     * Test of requireStringList method. If valid.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireStringList() {

        assertThat(FileOptionSourceValidator.requireStringList(JsonValue.EMPTY_JSON_ARRAY, "E")).isEmpty();

        assertThat(FileOptionSourceValidator.requireStringList(Json.createArrayBuilder().add("H").add("W").build(), "H"))
                .containsExactly("H", "W");

    }

    /**
     * Test of requireStringList method. If invalid type.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireStringList_InvalidType() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(JsonValue.NULL, "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(JsonValue.TRUE, "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(JsonValue.FALSE, "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(Json.createValue(0), "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(Json.createValue(""), "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(JsonValue.EMPTY_JSON_OBJECT, "X"))
                .withMessage("FileOption value of [X] must be list of string.");

    }

    /**
     * Test of requireStringList method. If element is invalid type.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireStringList_ElementIsInvalidType() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(
                Json.createArrayBuilder().add(JsonValue.NULL).build(), "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(
                Json.createArrayBuilder().add(JsonValue.TRUE).build(), "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(
                Json.createArrayBuilder().add(JsonValue.FALSE).build(), "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(
                Json.createArrayBuilder().add(Json.createValue(0)).build(), "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(
                Json.createArrayBuilder().add(JsonValue.EMPTY_JSON_ARRAY).build(), "X"))
                .withMessage("FileOption value of [X] must be list of string.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(
                Json.createArrayBuilder().add(JsonValue.EMPTY_JSON_OBJECT).build(), "X"))
                .withMessage("FileOption value of [X] must be list of string.");

    }

    /**
     * Test of requireStringList method. If null.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireStringList_Null() {

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(null, "X"));

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireStringList(JsonValue.NULL, null));

    }

    /**
     * Test of requireIntList method. If valid.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireIntList() {

        assertThat(FileOptionSourceValidator.requireIntList(
                Json.createArrayBuilder().add(Json.createValue(Integer.MIN_VALUE)).build(), "min"))
                .containsExactly(Integer.MIN_VALUE);

    }

    /**
     * Test of requireIntList method. If invalid type.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireIntList_InvalidType() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(JsonValue.NULL, "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(JsonValue.TRUE, "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(JsonValue.FALSE, "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(Json.createValue(0), "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(Json.createValue(""), "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(JsonValue.EMPTY_JSON_OBJECT, "X"))
                .withMessage("FileOption value of [X] must be list of int.");

    }

    /**
     * Test of requireIntList method. If element is invalid type.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireIntList_ElementIsInvalidType() {

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(
                Json.createArrayBuilder().add(JsonValue.NULL).build(), "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(
                Json.createArrayBuilder().add(JsonValue.TRUE).build(), "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(
                Json.createArrayBuilder().add(JsonValue.FALSE).build(), "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(
                Json.createArrayBuilder().add(Json.createValue("")).build(), "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(
                Json.createArrayBuilder().add(JsonValue.EMPTY_JSON_ARRAY).build(), "X"))
                .withMessage("FileOption value of [X] must be list of int.");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(
                Json.createArrayBuilder().add(JsonValue.EMPTY_JSON_OBJECT).build(), "X"))
                .withMessage("FileOption value of [X] must be list of int.");

    }

    /**
     * Test of requireIntList method. If too many.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireIntList_TooMany() {

        JsonValue tooMany = Json.createArrayBuilder().add(
                Json.createValue(BigDecimal.valueOf(Integer.MAX_VALUE).add(BigDecimal.ONE))
        ).build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(tooMany, "X"))
                .withMessage("FileOption value of [X] must be list of int.");

    }

    /**
     * Test of requireIntList method. If too few.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireIntList_TooFew() {

        JsonValue tooFew = Json.createArrayBuilder().add(
                Json.createValue(BigDecimal.valueOf(Integer.MIN_VALUE).subtract(BigDecimal.ONE))
        ).build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(tooFew, "X"))
                .withMessage("FileOption value of [X] must be list of int.");

    }

    /**
     * Test of requireIntList method. If null.
     *
     * @since 1.0.0
     */
    @Test
    void testRequireIntList_Null() {

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(null, "X"));

        assertThatNullPointerException()
                .isThrownBy(() -> FileOptionSourceValidator.requireIntList(JsonValue.NULL, null));

    }

}
