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
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;

/**
 * Authentication account information.
 * <p>
 * "account" is that used when access to a file system that needs to be authenticated.
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This class and JSON can be converted bidirectionally.</li>
 * <li>Can reflect this class on the {@link FileSystemOptions}.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@FileOption.Name("account")
public class AccountContext extends AbstractFileOption {

    private final String id;
    private final String password;
    private final String domain;

    /**
     * Constructor.
     * <p>
     * <b>Example: In case of need not to set the domain name.</b>
     * <pre><code>
     * {
     *     "id"      : "account id"
     *    ,"password": "account password"
     * }
     * </code></pre>
     *
     * <b>Example: In case of need to set the domain name.</b>
     * <pre><code>
     * {
     *     "id"      : "account id"
     *    ,"password": "account password"
     *    ,"domain"  : "domain name"
     * }
     * </code></pre>
     *
     * @param accountCtx account information that representation of the JSON
     * @throws NullPointerException if {@code accountCtx} is {@code null}
     * @throws IllegalArgumentException if {@code accountCtx} is malformed
     * @since 1.0.0
     */
    public AccountContext(JsonValue accountCtx) {

        Objects.requireNonNull(accountCtx);

        final JsonObject values;

        try {

            values = accountCtx.asJsonObject();

        } catch (ClassCastException ex) {

            throw new IllegalArgumentException(ex);

        }

        this.id = values.getString("id", null);
        this.password = values.getString("password", null);
        this.domain = values.getString("domain", null);
    }

    /**
     * Constructor.
     *
     * @param id account id. if need not it when {@code null}.
     * @param password account password. if need not it when {@code null}.
     * @param domain domain name of account. if need not it when {@code null}.
     * @since 1.0.0
     */
    public AccountContext(String id, String password, String domain) {
        this.id = id;
        this.password = password;
        this.domain = domain;
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>JSON Format</b>
     * <br>Note: If the value is {@code null}, the value is not output including the key
     * <pre><code>
     * {
     *     "id"      : "account id"
     *    ,"password": "account password"
     *    ,"domain"  : "domain name"
     * }
     * </code></pre>
     *
     * @return value of JSON representation
     * @since 1.0.0
     */
    @Override
    public JsonObject getValue() {

        var builder = Json.createObjectBuilder();

        Optional.ofNullable(id).ifPresent(v -> builder.add("id", v));
        Optional.ofNullable(password).ifPresent(v -> builder.add("password", v));
        Optional.ofNullable(domain).ifPresent(v -> builder.add("domain", v));

        return builder.build();
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if {@code opts} is {@code null}
     * @since 1.0.0
     */
    @Override
    public void apply(FileSystemOptions opts) throws FileSystemException {

        Objects.requireNonNull(opts);

        var authenticator = new StaticUserAuthenticator(domain, id, password);

        DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, authenticator);
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue());
    }

    /**
     * Indicates that other object is equal to this one.
     *
     * @param other an any object
     * @return {@code true} if equals otherwise {@code false}
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof AccountContext o && Objects.equals(getName(), o.getName())
                && Objects.equals(getValue(), o.getValue());
    }

    /**
     * Returns a string representation of this.
     *
     * @return string representation
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return Json.createObjectBuilder().add(getName(), getValue()).build().toString();
    }

    /**
     * Resolver for {@link AccountContext} instance from JSON.
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class is immutable and thread-safe.</li>
     * <li>Implementations of this interface must be able to construct instances using {@link ServiceLoader}.</li>
     * <li>This class must be able to construct an instance of {@code FileOption} from the JSON representing
     * {@code FileOption}.</li>
     * </ul>
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class Resolver implements FileOption.Resolver {

        /**
         * Constructs a new instance.
         *
         * @since 1.0.0
         */
        public Resolver() {
            // Do nothing.
        }
    }
}
