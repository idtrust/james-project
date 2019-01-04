/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package org.apache.james.rrt.api;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.james.core.Domain;
import org.apache.james.rrt.lib.Mapping;
import org.apache.james.rrt.lib.MappingSource;
import org.apache.james.rrt.lib.Mappings;

import com.github.steveash.guavate.Guavate;
import com.google.common.base.Preconditions;

/**
 * Interface which should be implemented of classes which map recipients.
 */
public interface RecipientRewriteTable {
    class ErrorMappingException extends Exception {
        private static final long serialVersionUID = 2348752938798L;

        public ErrorMappingException(String string) {
            super(string);
        }
    }

    class TooManyMappingException extends ErrorMappingException {
        public TooManyMappingException(String string) {
            super(string);
        }
    }

    EnumSet<Mapping.Type> listSourcesSupportedType = EnumSet.of(
        Mapping.Type.Group,
        Mapping.Type.Forward,
        Mapping.Type.Address);

    void addMapping(MappingSource source, Mapping mapping) throws RecipientRewriteTableException;

    void removeMapping(MappingSource source, Mapping mapping) throws RecipientRewriteTableException;

    void addRegexMapping(MappingSource source, String regex) throws RecipientRewriteTableException;

    void removeRegexMapping(MappingSource source, String regex) throws RecipientRewriteTableException;

    void addAddressMapping(MappingSource source, String address) throws RecipientRewriteTableException;

    void removeAddressMapping(MappingSource source, String address) throws RecipientRewriteTableException;

    void addErrorMapping(MappingSource source, String error) throws RecipientRewriteTableException;

    void removeErrorMapping(MappingSource source, String error) throws RecipientRewriteTableException;

    void addAliasDomainMapping(MappingSource source, Domain realDomain) throws RecipientRewriteTableException;

    void removeAliasDomainMapping(MappingSource source, Domain realDomain) throws RecipientRewriteTableException;

    void addForwardMapping(MappingSource source, String address) throws RecipientRewriteTableException;

    void removeForwardMapping(MappingSource source, String address) throws RecipientRewriteTableException;

    void addGroupMapping(MappingSource source, String address) throws RecipientRewriteTableException;

    void removeGroupMapping(MappingSource source, String address) throws RecipientRewriteTableException;

    /**
     * Return the Mappings for the given source. Return null if no
     * matched mapping was found
     *
     * @throws ErrorMappingException
     *             get thrown if an error mapping was found
     */
    Mappings getResolvedMappings(String user, Domain domain) throws ErrorMappingException, RecipientRewriteTableException;

    /**
     * Return the explicit mapping stored for the given user and domain. Return
     * null if no mapping was found
     * 
     * @return the collection which holds the mappings.
     * @throws RecipientRewriteTableException
     */
    Mappings getStoredMappings(MappingSource source) throws RecipientRewriteTableException;

    /**
     * Return a Map which holds all mappings. The key is the user@domain and the
     * value is a Collection which holds all mappings
     * 
     * @return Map which holds all mappings
     * @throws RecipientRewriteTableException
     */
    Map<MappingSource, Mappings> getAllMappings() throws RecipientRewriteTableException;

    default List<MappingSource> listSources(Mapping mapping) throws RecipientRewriteTableException {
        Preconditions.checkArgument(listSourcesSupportedType.contains(mapping.getType()),
            String.format("Not supported mapping of type %s", mapping.getType()));

        return getAllMappings().entrySet().stream()
            .filter(entry -> entry.getValue().contains(mapping))
            .map(Map.Entry::getKey)
            .collect(Guavate.toImmutableList());
    }
}
