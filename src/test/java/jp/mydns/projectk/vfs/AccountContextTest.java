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
import org.apache.commons.vfs2.FileSystemOptions;
import static org.apache.commons.vfs2.UserAuthenticationData.DOMAIN;
import static org.apache.commons.vfs2.UserAuthenticationData.PASSWORD;
import org.apache.commons.vfs2.UserAuthenticationData.Type;
import static org.apache.commons.vfs2.UserAuthenticationData.USERNAME;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.Test;

/**
 * Test of class AccountContext.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
class AccountContextTest {

    /**
     * Test constructor. Arguments are {@code String}, {@code String}, {@code String}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_String_String_String() {

        JsonValue expectValue = Json.createObjectBuilder().add("id", "i").add("password", "p").add("domain", "d").build();

        var instance = new AccountContext("i", "p", "d");

        assertThat(instance).returns("account", AccountContext::getName)
                .returns(expectValue, AccountContext::getValue);
    }

    /**
     * Test constructor. Argument is {@code JsonValue}.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_JsonValue() {

        JsonValue expectValue = Json.createObjectBuilder().add("id", "i").add("password", "p").add("domain", "d").build();

        var instance = new AccountContext(expectValue);

        assertThat(instance).returns("account", AccountContext::getName)
                .returns(expectValue, AccountContext::getValue);
    }

    /**
     * Test constructor. Argument is {@code JsonValue}. If illegal argument.
     *
     * @since 1.0.0
     */
    @Test
    void testConstructor_JsonValue_IllegalArgument() {

        assertThatIllegalArgumentException().isThrownBy(() -> new AccountContext(JsonValue.TRUE));
    }

    /**
     * Test of getValue method. If minimum.
     *
     * @since 1.0.0
     */
    @Test
    void testGetValue_Minimum() {

        JsonValue result = new AccountContext(null, null, null).getValue();

        assertThat(result).isEqualTo(JsonValue.EMPTY_JSON_OBJECT);
    }

    /**
     * Test of getValue method. If maximum.
     *
     * @since 1.0.0
     */
    @Test
    void testGetValue_Maximum() {

        JsonValue expectValue = Json.createObjectBuilder().add("id", "i").add("password", "p").add("domain", "d").build();

        JsonValue result = new AccountContext("i", "p", "d").getValue();

        assertThat(result).isEqualTo(expectValue);
    }

    /**
     * Test of apply method.
     *
     * @since 1.0.0
     */
    @Test
    void testApply() throws Exception {

        final var accountCtx = new AccountContext("u", "p", "d");

        final var fsOpts = new FileSystemOptions();

        accountCtx.apply(fsOpts);

        var b = DefaultFileSystemConfigBuilder.getInstance();
        var ua = b.getUserAuthenticator(fsOpts);
        var uad = ua.requestAuthentication(new Type[]{DOMAIN, USERNAME, PASSWORD});

        assertThat(uad.getData(USERNAME)).containsExactly('u');
        assertThat(uad.getData(PASSWORD)).containsExactly('p');
        assertThat(uad.getData(DOMAIN)).containsExactly('d');
    }

    /**
     * Test {@code equals} method and {@code hashCode} method.
     *
     * @since 1.0.0
     */
    @Test
    void testEqualsHashCode() throws Exception {

        AccountContext account1 = new AccountContext("i", "p", "d");
        AccountContext account2 = new AccountContext("i", "p", "d");
        AccountContext account3 = new AccountContext("i", "p", null);

        assertThat(account1).hasSameHashCodeAs(account2).isEqualTo(account2)
                .doesNotHaveSameHashCodeAs(account3).isNotEqualTo(account3);
    }

    /**
     * Test of toString method.
     *
     * @since 1.0.0
     */
    @Test
    void testToString() {

        JsonValue val = Json.createObjectBuilder().add("id", "i").add("password", "p").add("domain", "d").build();

        String expect = Json.createObjectBuilder().add("account", val).build().toString();

        var instance = new AccountContext(val);

        var result = instance.toString();

        assertThat(result).isEqualTo(expect);
    }
}
