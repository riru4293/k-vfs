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
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.ServiceLoader;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;

/**
 * Optional configuration of {@link FileContext}. This is an interface that represents a single file system property.
 * Implementations must be prepared for each property.
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
public interface FileOption {

    /**
     * Get configuration name. It must be unique for each configuration item. Also, the characters that can be used are
     * limited to those that can be used in JSON element names.
     *
     * @return configuration name. It never {@code null}.
     * @throws IllegalStateException if name cannot be resolved
     * @since 1.0.0
     */
    String getName();

    /**
     * Get configuration value. The return value must be able to reproduce instance via the constructor.
     *
     * @return configuration value. It never {@code null}.
     * @throws IllegalStateException if value cannot be resolved
     * @since 1.0.0
     */
    JsonValue getValue();

    /**
     * Apply this configuration to the {@code FileSystemOptions}.
     *
     * @param opts the {@code FileSystemOptions}. This value will be modified.
     * @throws NullPointerException if {@code opts} is {@code null}
     * @throws FileSystemException if cannot apply this configuration
     * @since 1.0.0
     */
    void apply(FileSystemOptions opts) throws FileSystemException;

    /**
     * Indicates the name of the {@link FileOption}.
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @Documented
    @interface Name {

        /**
         * Returns a name of the {@link FileOption}.
         *
         * @return name of the {@code FileOption}
         * @since 1.0.0
         */
        String value();
    }

    /**
     * A {@link FileOption} instance resolver. An implementation of this interface is required for each implementation
     * of {@code FileOption}.
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
    interface Resolver {

        /**
         * Get name of {@link FileOption} that to be resolved by this resolver.
         * <p>
         * Implementation notes.
         * <ul>
         * <li>The default implementation assumes that this class is implemented as an inner class of the
         * {@code FileOption} class. If the assumptions are met, returns the value of the {@link FileOption.Name}
         * annotation attached to the FileOption class.</li>
         * </ul>
         *
         * @return name of {@code FileOption}. It never {@code null}.
         * @throws IllegalStateException if cannot find the file option name
         * @since 1.0.0
         */
        default String getName() {

            return Optional.ofNullable(this.getClass().getDeclaringClass())
                    .map(c -> c.getAnnotation(Name.class)).map(Name::value).orElseThrow(
                    () -> new IllegalStateException("Cannot find the file option name."));
        }

        /**
         * Construct the {@link FileOption} instance from JSON.
         * <p>
         * Implementation notes.
         * <ul>
         * <li>The default implementation assumes that this class is implemented as an inner class of the
         * {@code FileOption} class. And also the {@code FileOption} class must have a constructor that takes a
         * {@link JsonValue} as an argument.</li>
         * </ul>
         *
         * @param fileOpt JSON representing an {@code FileOption}
         * @return new {@code FileOption} instance
         * @throws IllegalArgumentException if could not be construct instance because {@code fileOpt} was invalid
         * @throws IllegalStateException if could not be construct instance due to incorrect implementation of
         * {@code FileOption}
         * @since 1.0.0
         */
        default FileOption newInstance(JsonValue fileOpt) {
            try {

                return FileOption.class.cast(getClass().getDeclaringClass().getConstructor(JsonValue.class)
                        .newInstance(fileOpt));

            } catch (InvocationTargetException ex) {
                throw new IllegalArgumentException(ex);

            } catch (ReflectiveOperationException | RuntimeException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
