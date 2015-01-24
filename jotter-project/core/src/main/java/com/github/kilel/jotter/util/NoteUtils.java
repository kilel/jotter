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

import com.github.kilel.jotter.common.EncryptedNote;
import com.github.kilel.jotter.common.Note;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * Utility functions.
 */
public class NoteUtils {

    public static Note copy(Note source) {
        final Note dest = new Note();
        update(source, dest);
        return dest;
    }

    public static void update(Note source, Note dest) {
        dest.setName(source.getName());
        dest.setCategory(source.getCategory());
        dest.setId(source.getId());
        dest.setSynchId(source.getSynchId());
        dest.setValue(source.getValue());
    }

    public static boolean checkEquals(Note a, Note b) {
        return a.getCategory().equalsIgnoreCase(b.getCategory()) //
                && a.getName().equalsIgnoreCase(b.getName());
    }

    public static void update(EncryptedNote source, EncryptedNote dest) {
        dest.setId(source.getId());
        dest.setSynchId(source.getSynchId());
        dest.setValue(source.getValue());
        dest.setEncryptorId(source.getEncryptorId());
    }


    public static String toString(Note note) {
        return String.format("Note(category = %s, name = %s, id = %s, synchId = %s, value = %s)", //
                             note.getCategory(), note.getName(), note.getId(), note.getSynchId(), note.getValue());
    }


    public static class BigIntegerComparator implements Comparator<BigInteger> {
        @Override
        public int compare(BigInteger a, BigInteger b) {
            return a.compareTo(b);
        }
    }

}
