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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hash map implementation of notes holder.
 */
public class NotesHolderImpl implements NotesHolder {

    private final Set<Listener> listeners;
    private final Map<String, Map<String, Note>> categories;

    public NotesHolderImpl() {
        listeners = new HashSet<>();
        categories = new ConcurrentHashMap<>();
    }

    private Map<String, Note> getCategory(String category) {
        if (!categories.containsKey(category)) {
            return null;
        }
        return categories.get(category);
    }

    private Map<String, Note> getOrCreateCategory(String category) {
        Map<String, Note> result = getCategory(category);
        if (result == null) {
            result = new ConcurrentHashMap<>();
            categories.put(category, result);
        }
        return result;
    }

    private Note getNote(String categoryName, String name) {
        final Map<String, Note> category = getCategory(categoryName);
        if (category == null || !category.containsKey(name)) {
            return null;
        }

        return category.get(name);
    }

    private Note getOrCreateNote(String categoryName, String name) {
        final Map<String, Note> category = getOrCreateCategory(categoryName);
        Note result = getNote(categoryName, name);

        if (result == null) {
            result = new Note();
            result.setCategory(categoryName);
            result.setName(name);
            category.put(name, result);
        }

        return result;
    }

    @Override
    public Note get(String category, String name) {
        return getNote(category, name);
    }

    @Override
    public Note get(Integer id) {
        for (Map<String, Note> category : categories.values()) {
            for (Note note : category.values()) {
                if (note.getId().equals(id)) {
                    return note;
                }
            }
        }
        return null;
    }

    @Override
    public void create(Note note) throws IllegalArgumentException {
        // Ensure, that there is no such note
        if (get(note.getCategory(), note.getName()) != null) {
            throw new IllegalArgumentException("Note already exists!");
        }

        // Create new note and update with target data.
        Note target = getOrCreateNote(note.getCategory(), note.getName());
        NoteUtils.update(note, target);
        fire(note, Event.Type.CREATE);
    }

    @Override
    public void update(Note source, Note dest) {
        // Check, that they have equal category-name
        if(NoteUtils.checkEquals(source, dest)) {
            Note noteToUpdate = get(source.getCategory(), source.getName());
            NoteUtils.update(dest, noteToUpdate);
            fire(noteToUpdate, Event.Type.UPDATE);
            return;
        }

        //if they are different, then need to delete old one and create new.
        remove(source);
        create(dest);
    }

    @Override
    public void remove(Note note) {
        Map<String, Note> category = getCategory(note.getCategory());
        if (getNote(note.getCategory(), note.getName()) != null) {
            category.remove(note.getName());
            fire(note, Event.Type.REMOVE);

            if (category.size() == 0) {
                categories.remove(note.getCategory());
            }
        }
    }

    @Override
    public Collection<String> getCategories() {
        return categories.keySet();
    }

    @Override
    public Collection<String> getNames(String category) {
        if (getCategory(category) != null) {
            return getCategory(category).keySet();
        }
        return null;
    }

    @Override
    public void addListener(Listener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(Listener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void fire(Note source, NotesHolder.Event.Type type) {
        synchronized (listeners) {
            listeners.stream().forEach((x) -> x.accept(new NotesHolder.Event(source, type)));
        }
    }
}
