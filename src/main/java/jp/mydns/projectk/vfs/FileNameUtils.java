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

import java.net.URI;
import java.util.Objects;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;

/**
 * Utilities for {@link FileName}.
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
public class FileNameUtils {

    private FileNameUtils() {
    }

    /**
     * Resolve {@code FileName} from {@link URI}.
     *
     * @param uri the {@code URI}
     * @param fsm the {@code FileSystemManager}
     * @return the {@code FileName}
     * @throws NullPointerException if {@code uri} is {@code null}
     * @throws IllegalArgumentException if {@code uri} is invalid as URI or if scheme of {@code URI} is not support.
     * @since 1.0.0
     */
    public static FileName fromUri(URI uri, FileSystemManager fsm) {

        Objects.requireNonNull(uri);
        Objects.requireNonNull(fsm);

        try {

            return fsm.resolveURI(uri.toString());

        } catch (FileSystemException ex) {

            throw new IllegalArgumentException("Illegal URI as FileName. [%s]".formatted(uri), ex);

        }
    }

    /**
     * Resolve a relative file name.
     *
     * @param fn the {@code FileName}
     * @param relative relative file name from {@code fn}
     * @param fsm the {@code FileSystemManager}
     * @return representing the resolved file name
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code relative} is invalid as relative file name
     * @since 1.0.0
     */
    public static FileName resolve(FileName fn, String relative, FileSystemManager fsm) {

        Objects.requireNonNull(fn);
        Objects.requireNonNull(relative);
        Objects.requireNonNull(fsm);

        try {

            return fsm.resolveName(fn, relative);

        } catch (FileSystemException ex) {

            throw new IllegalArgumentException("Can't resolve a relative file name.", ex);

        }
    }

    /**
     * Get parent directory.
     *
     * @param fn the {@code FileName}
     * @return representing a parent directory
     * @throws NullPointerException if {@code fn} is {@code null}
     * @throws IllegalArgumentException if {@code fn} is root directory
     * @since 1.0.0
     */
    public static FileName getParent(FileName fn) {

        Objects.requireNonNull(fn);

        FileName parent = fn.getParent();

        if (parent == null) {
            throw new IllegalArgumentException("Can't get parent directory. Because already root directory.");
        }

        return parent;
    }

    /**
     * Append suffix to file name.
     *
     * @param fn the {@code FileName}
     * @param suffix suffix to add
     * @param fsm the {@code FileSystemManager}
     * @return representing with append specified suffix
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if {@code suffix} is invalid as file name or if {@code fn} is root directory
     * @since 1.0.0
     */
    public static FileName appendSuffix(FileName fn, String suffix, FileSystemManager fsm) {

        Objects.requireNonNull(fn);
        Objects.requireNonNull(suffix);
        Objects.requireNonNull(fsm);

        return resolve(getParent(fn), fn.getBaseName() + suffix, fsm);
    }
}
