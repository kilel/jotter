/*
 * Copyright 2015 Kislitsyn Ilya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kilel.jotter.ui;

import com.github.kilel.jotter.command.impl.UpdateCommand;
import com.github.kilel.jotter.common.Note;
import com.github.kilel.jotter.util.NoteUtils;

/**
 * Abstract args loader factory.
 */
public class ArgsLoaderFactory {

    public static UpdateCommand.ArgsLoader createUpdateArgsLoader(UICommandParams params) {
        if (params instanceof UICommandParams.Update) {
            return new UpdateArgsLoader((UICommandParams.Update) params);
        } else if (params instanceof UICommandParams.Add) {
            return new AddArgsLoader((UICommandParams.Add) params);
        } else if (params instanceof UICommandParams.Remove) {
            return new RemoveArgsLoader((UICommandParams.Remove) params);
        }

        throw new RuntimeException("Unsupported update operation for parameters " + params.getClass().toString());

    }

    /**
     * ADD args loader.
     */
    public static class AddArgsLoader implements UpdateCommand.ArgsLoader {
        private final UICommandParams.Add params;

        public AddArgsLoader(UICommandParams.Add params) {
            this.params = params;
        }

        @Override
        public Note getNoteToUpdate() {
            return null;
        }

        @Override
        public Note getResultNote() {
            final String value = params.getValue() != null ? params.getValue() : params.getEncValue();
            return NoteUtils.createNote(params.getCategory(), params.getName(), params.getEncryptor(), value);
        }
    }

    /**
     * REMOVE args loader.
     */
    public static class RemoveArgsLoader implements UpdateCommand.ArgsLoader {
        private final UICommandParams.Remove params;

        public RemoveArgsLoader(UICommandParams.Remove params) {
            this.params = params;
        }

        @Override
        public Note getNoteToUpdate() {
            return NoteUtils.createNote(params.getCategory(), params.getName());
        }

        @Override
        public Note getResultNote() {
            return null;
        }
    }

    /**
     * UPDATE args loader.
     */
    public static class UpdateArgsLoader implements UpdateCommand.ArgsLoader {
        private final UICommandParams.Update params;

        public UpdateArgsLoader(UICommandParams.Update params) {
            this.params = params;
        }

        @Override
        public Note getNoteToUpdate() {
            return NoteUtils.createNote(params.getCategory(), params.getName());
        }

        @Override
        public Note getResultNote() {
            final String value = params.getValue() != null ? params.getValue() : params.getEncValue();
            return NoteUtils.createNote(params.getTgtCategory(), params.getTgtName(), params.getEncryptor(), value);
        }

    }

}
