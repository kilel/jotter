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

package com.github.kilel.jotter.encryptor;

import com.github.kilel.jotter.common.EncryptedNote;
import com.github.kilel.jotter.common.Note;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Encryptor abstraction
 */
public abstract class AbstractEncryptor implements Encryptor {
    private final JAXBContext context;
    private final String uniqueId;

    /**
     * Creates Encryptor.
     *
     * @param uniqueId unique id.
     */
    public AbstractEncryptor(String uniqueId) {
        try {
            this.uniqueId = uniqueId;
            context = JAXBContext.newInstance(com.github.kilel.jotter.msg.ObjectFactory.class,
                                              com.github.kilel.jotter.common.ObjectFactory.class);
        } catch (Exception e) {
            throw new RuntimeException("Can't create JAXB context for jotter", e);
        }
    }

    @Override
    public EncryptedNote encrypt(Note source) {
        try {
            final Marshaller marshaller;
            synchronized (context) {
                marshaller = context.createMarshaller();
            }

            // Marshal note to string
            final StringWriter writer = new StringWriter();
            marshaller.marshal(source, writer);

            // Encrypt string
            final EncryptedNote note = new EncryptedNote();
            note.setId(source.getId());
            note.setSynchId(source.getSynchId());
            note.setValue(doEncrypt(writer.toString()));
            note.setEncryptorId(getUniqueId());
            return note;
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt note", e);
        }
    }

    @Override
    public Note decrypt(EncryptedNote source) {
        try {
            final Unmarshaller unmarshaller;
            synchronized (context) {
                unmarshaller = context.createUnmarshaller();
            }

            // Unmarshal note to string
            final StringReader reader = new StringReader(doDecrypt(source.getValue()));
            final Note note = (Note) unmarshaller.unmarshal(reader);
            note.setId(source.getId());
            note.setSynchId(source.getSynchId());
            return note;
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt note", e);
        }
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    protected abstract byte[] doEncrypt(String source);

    protected abstract String doDecrypt(byte[] source);
}
