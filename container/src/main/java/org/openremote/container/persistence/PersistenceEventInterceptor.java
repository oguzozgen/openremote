/*
 * Copyright 2016, OpenRemote Inc.
 *
 * See the CONTRIBUTORS.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.container.persistence;

import org.apache.camel.ExchangePattern;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;
import org.openremote.container.message.MessageBrokerService;

import javax.transaction.Status;
import javax.transaction.Synchronization;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Intercept Hibernate lifecycle events and publish a message.
 */
public class PersistenceEventInterceptor extends EmptyInterceptor {

    private static final Logger LOG = Logger.getLogger(PersistenceEventInterceptor.class.getName());

    protected MessageBrokerService messageBrokerService;
    protected Set<PersistenceEvent> persistenceEvents = new HashSet<>();

    public MessageBrokerService getMessageBrokerService() {
        if (messageBrokerService == null)
            throw new IllegalStateException("MessageBrokerService must be set on interceptor before use");
        return messageBrokerService;
    }

    public void setMessageBrokerService(MessageBrokerService messageBrokerService) {
        this.messageBrokerService = messageBrokerService;
    }

    @Override
    public boolean onSave(Object entity, Serializable id,
                          Object[] state, String[] propertyNames, Type[] types)
        throws CallbackException {
        //noinspection unchecked
        persistenceEvents.add(new PersistenceEvent(
            PersistenceEvent.Cause.INSERT,
            entity,
            propertyNames,
            state
        ));
        return false;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id,
                                Object[] currentState, Object[] previousState,
                                String[] propertyNames, Type[] types)
        throws CallbackException {
        //noinspection unchecked
        persistenceEvents.add(new PersistenceEvent(
            PersistenceEvent.Cause.UPDATE,
            entity,
            propertyNames,
            currentState,
            previousState
        ));
        return false;
    }

    @Override
    public void onDelete(Object entity, Serializable id,
                         Object[] state,
                         String[] propertyNames,
                         Type[] types) {
        //noinspection unchecked
        persistenceEvents.add(new PersistenceEvent(
            PersistenceEvent.Cause.DELETE,
            entity,
            propertyNames,
            state
        ));
    }

    @Override
    public void afterTransactionBegin(Transaction tx) {
        tx.registerSynchronization(new Synchronization() {
            @Override
            public void beforeCompletion() {
            }

            @Override
            public void afterCompletion(int status) {
                try {
                    if (status != Status.STATUS_COMMITTED)
                        return;

                    for (PersistenceEvent persistenceEvent : persistenceEvents) {
                        messageBrokerService.getProducerTemplate().sendBodyAndHeader(
                            PersistenceEvent.PERSISTENCE_EVENT_TOPIC,
                            ExchangePattern.InOnly,
                            persistenceEvent,
                            PersistenceEvent.HEADER_ENTITY_TYPE,
                            persistenceEvent.getEntity().getClass()
                        );
                    }
                } finally {
                    persistenceEvents.clear();
                }
            }
        });
    }

    @Override
    public void afterTransactionCompletion(Transaction tx) {
    }
}