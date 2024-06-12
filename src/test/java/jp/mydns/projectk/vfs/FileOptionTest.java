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

import jakarta.json.JsonValue;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test of class FileOption.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(FileSystemManagerParameterResolver.class)
class FileOptionTest {

    /**
     * Test of getName method of class FileOption.
     *
     * @since 1.0.0
     */
    @Test
    void testGetName() {

        var option = new AccountContext(null, null, null);

        String result = option.getName();

        assertThat(result).isEqualTo("account");

    }

    /**
     * Test of getName method of class FileOption. If no found.
     *
     * @since 1.0.0
     */
    @Test
    void testGetName_NoFound() {

        var option = new NoNameOption();

        assertThatIllegalStateException().isThrownBy(() -> option.getName())
                .withMessage("No found a file option name.");

    }

    /**
     * Test of getValue method of class FileOption.
     *
     * @since 1.0.0
     */
    @Test
    void testGetValue() {

        var option = new ValidOption(JsonValue.NULL);

        JsonValue result = option.getValue();

        assertThat(result).isEqualTo(JsonValue.NULL);

    }

    /**
     * Test of getName method of class FileOption.Resolver.
     *
     * @since 1.0.0
     */
    @Test
    void testGetName_Resolver() {

        var resolver = new AccountContext.Resolver();

        String result = resolver.getName();

        assertThat(result).isEqualTo("account");

    }

    /**
     * Test of getName method of class FileOption.Resolver. If no found.
     *
     * @since 1.0.0
     */
    @Test
    void testGetName_Resolver_NoFound() {

        var resolver = new NoNameOption.Resolver();

        assertThatIllegalStateException().isThrownBy(() -> resolver.getName())
                .withMessage("Cannot find the file option name.");
    }

    /**
     * Test of newInstance method of class FileOption.Resolver. If illegal argument.
     *
     * @since 1.0.0
     */
    @Test
    void testNewInstance_Resolver_IllegalArgument() {

        var resolver = new AccountContext.Resolver();

        assertThatIllegalArgumentException().isThrownBy(() -> resolver.newInstance(JsonValue.FALSE));
    }

    /**
     * Test of newInstance method of class FileOption.Resolver. If illegal state.
     *
     * @since 1.0.0
     */
    @Test
    void testNewInstance_Resolver_IllegalState() {

        var resolver = new NoConstructorOption.Resolver();

        assertThatIllegalStateException().isThrownBy(() -> resolver.newInstance(JsonValue.FALSE));
    }

    @FileOption.Name("valid")
    public static class ValidOption extends AbstractFileOption {

        private final JsonValue value;

        public ValidOption(JsonValue v) {
            value = v;
        }

        @Override
        public JsonValue getValue() {
            return value;
        }

        @Override
        public void apply(FileSystemOptions opts) throws FileSystemException {
        }

        public static class Resolver implements FileOption.Resolver {
        }
    }

    @FileOption.Name("noConstructor")
    public static class NoConstructorOption extends AbstractFileOption {

        @Override
        public JsonValue getValue() {
            return JsonValue.NULL;
        }

        @Override
        public void apply(FileSystemOptions opts) throws FileSystemException {
        }

        public static class Resolver implements FileOption.Resolver {
        }
    }

    public static class NoNameOption extends AbstractFileOption {

        @Override
        public JsonValue getValue() {
            return null;
        }

        @Override
        public void apply(FileSystemOptions opts) throws FileSystemException {
        }

        public static class Resolver implements FileOption.Resolver {
        }
    }
}
