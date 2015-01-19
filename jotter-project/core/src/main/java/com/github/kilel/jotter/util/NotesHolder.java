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

package com.github.kilel.jotter.util;

import com.github.kilel.jotter.common.Note;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Holds notes.
 */
public interface NotesHolder {
    /**
     * Provides access to the note.
     *
     * @param category Note category.
     * @param name     Note name.
     * @return Note.
     */
    Note get(String category, String name);

    /**
     * Provides access to the note.
     *
     * @param id Note unique id.
     * @return Note.
     */
    Note get(Integer id);

    /**
     * Creates or updates note in the holder.
     *
     * @param note Note to create.
     */
    void create(Note note);

    /**
     * Updates or creates note in the holder.
     *
     * @param source Note to update to dest.
     * @param dest   Note to update in.
     */
    void update(Note source, Note dest);

    /**
     * Removes note from the holder.
     *
     * @param note Note.
     */
    void remove(Note note);

    /**
     * Lists categories.
     *
     * @return Categories.
     */
    Collection<String> getCategories();

    /**
     * Lists notes names in the category
     *
     * @param category Category.
     * @return Notes names.
     */
    Collection<String> getNames(String category);


    /**
     * Adds listener.
     *
     * @param listener Listener.
     */
    void addListener(Listener listener);

    /**
     * Removes listener.
     *
     * @param listener
     */
    void removeListener(Listener listener);

    /**
     * Notes holder listener.
     */
    public static interface Listener extends Consumer<Event> {
    }

    public static class Event {
        private final Note source;
        private final Type type;

        public Event(Note source, Type type) {
            this.source = source;
            this.type = type;
        }

        public Note getSource() {
            return source;
        }

        public Type getType() {
            return type;
        }

        public static enum Type {
            CREATE,
            REMOVE,
            UPDATE
        }
    }

}
