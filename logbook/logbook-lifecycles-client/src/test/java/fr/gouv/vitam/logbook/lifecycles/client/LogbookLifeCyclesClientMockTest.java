/*******************************************************************************
 * This file is part of Vitam Project.
 * <p>
 * Copyright Vitam (2012, 2015)
 * <p>
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL license as circulated
 * by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
 * <p>
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 * <p>
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 * <p>
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL license and that you
 * accept its terms.
 *******************************************************************************/
package fr.gouv.vitam.logbook.lifecycles.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import fr.gouv.vitam.common.LocalDateUtil;
import fr.gouv.vitam.common.exception.InvalidParseOperationException;
import fr.gouv.vitam.logbook.common.exception.LogbookClientException;
import fr.gouv.vitam.logbook.common.parameters.LogbookParameterName;
import fr.gouv.vitam.logbook.common.parameters.LogbookParameters;
import fr.gouv.vitam.logbook.common.parameters.LogbookParametersFactory;

public class LogbookLifeCyclesClientMockTest {

    private static final String request = "{ $query: {} }, $projection: {}, $filter: {} }";

    @Test
    public void createTest() {
        LogbookLifeCyclesClientFactory
            .setConfiguration(LogbookLifeCyclesClientFactory.LogbookClientType.MOCK_LIFECYCLES, null, 0);

        final LogbookLifeCycleClient client =
            LogbookLifeCyclesClientFactory.getInstance().getLogbookLifeCyclesClient();
        assertNotNull(client);

        final LogbookParameters logbookParameters = LogbookParametersFactory.newLogbookOperationParameters();
        assertNotNull(logbookParameters);

        final Set<LogbookParameterName> mandatory = logbookParameters.getMandatoriesParameters();
        assertNotNull(mandatory);

        boolean catchException = false;
        try {
            mandatory.add(LogbookParameterName.objectIdentifier);
        } catch (final UnsupportedOperationException uoe) {
            catchException = true;
        }
        assertTrue(catchException);

        fillLogbookParamaters(logbookParameters);

        catchException = false;
        try {
            client.create(logbookParameters);
        } catch (final LogbookClientException lce) {
            catchException = true;
        }
        assertFalse(catchException);
    }

    @Test
    public void updateTest() {
        LogbookLifeCyclesClientFactory
            .setConfiguration(LogbookLifeCyclesClientFactory.LogbookClientType.MOCK_LIFECYCLES, null, 0);

        final LogbookLifeCycleClient client =
            LogbookLifeCyclesClientFactory.getInstance().getLogbookLifeCyclesClient();
        assertNotNull(client);

        final LogbookParameters logbookParameters = LogbookParametersFactory.newLogbookOperationParameters();
        assertNotNull(logbookParameters);

        final Set<LogbookParameterName> mandatory = logbookParameters.getMandatoriesParameters();

        boolean catchException = false;
        try {
            mandatory.add(LogbookParameterName.objectIdentifier);
        } catch (final UnsupportedOperationException uoe) {
            catchException = true;
        }
        assertTrue(catchException);
        assertNotNull(logbookParameters);

        fillLogbookParamaters(logbookParameters);

        catchException = false;
        try {
            client.update(logbookParameters);
        } catch (final LogbookClientException lce) {
            catchException = true;
        }
        assertFalse(catchException);
    }

    @Test
    public void commitTest() {
        LogbookLifeCyclesClientFactory
            .setConfiguration(LogbookLifeCyclesClientFactory.LogbookClientType.MOCK_LIFECYCLES, null, 0);

        final LogbookLifeCycleClient client =
            LogbookLifeCyclesClientFactory.getInstance().getLogbookLifeCyclesClient();
        assertNotNull(client);

        final LogbookParameters logbookParameters = LogbookParametersFactory.newLogbookOperationParameters();
        assertNotNull(logbookParameters);

        final Set<LogbookParameterName> mandatory = logbookParameters.getMandatoriesParameters();

        boolean catchException = false;
        try {
            mandatory.add(LogbookParameterName.objectIdentifier);
        } catch (final UnsupportedOperationException uoe) {
            catchException = true;
        }
        assertTrue(catchException);
        assertNotNull(logbookParameters);

        fillLogbookParamaters(logbookParameters);

        catchException = false;
        try {
            client.commit(logbookParameters);
        } catch (final LogbookClientException lce) {
            catchException = true;
        }
        assertFalse(catchException);
    }

    @Test
    public void rollbackTest() {
        LogbookLifeCyclesClientFactory
            .setConfiguration(LogbookLifeCyclesClientFactory.LogbookClientType.MOCK_LIFECYCLES, null, 0);

        final LogbookLifeCycleClient client =
            LogbookLifeCyclesClientFactory.getInstance().getLogbookLifeCyclesClient();
        assertNotNull(client);

        final LogbookParameters logbookParameters = LogbookParametersFactory.newLogbookOperationParameters();
        assertNotNull(logbookParameters);

        final Set<LogbookParameterName> mandatory = logbookParameters.getMandatoriesParameters();

        boolean catchException = false;
        try {
            mandatory.add(LogbookParameterName.objectIdentifier);
        } catch (final UnsupportedOperationException uoe) {
            catchException = true;
        }
        assertTrue(catchException);
        assertNotNull(logbookParameters);

        fillLogbookParamaters(logbookParameters);

        catchException = false;
        try {
            client.rollback(logbookParameters);
        } catch (final LogbookClientException lce) {
            catchException = true;
        }
        assertFalse(catchException);
    }


    @Test
    public void statusTest() throws LogbookClientException {
        LogbookLifeCyclesClientFactory
            .setConfiguration(LogbookLifeCyclesClientFactory.LogbookClientType.MOCK_LIFECYCLES, null, 0);

        final LogbookLifeCycleClient client =
            LogbookLifeCyclesClientFactory.getInstance().getLogbookLifeCyclesClient();
        assertNotNull(client);
        assertNotNull(client.status());
    }

    private void fillLogbookParamaters(LogbookParameters logbookParamaters) {
        logbookParamaters.putParameterValue(LogbookParameterName.eventIdentifier,
            LogbookParameterName.eventIdentifier.name());
        logbookParamaters
            .putParameterValue(LogbookParameterName.eventType, LogbookParameterName.eventType.name());
        logbookParamaters.putParameterValue(LogbookParameterName.eventDateTime,
            LocalDateUtil.now().toString());
        logbookParamaters.putParameterValue(LogbookParameterName.eventIdentifierProcess,
            LogbookParameterName.eventIdentifierProcess.name());
        logbookParamaters.putParameterValue(LogbookParameterName.eventTypeProcess,
            LogbookParameterName.eventTypeProcess.name());
        logbookParamaters.putParameterValue(LogbookParameterName.outcome, LogbookParameterName.outcome.name());
        logbookParamaters
            .putParameterValue(LogbookParameterName.outcomeDetail, LogbookParameterName.outcomeDetail.name());
        logbookParamaters.putParameterValue(LogbookParameterName.outcomeDetailMessage,
            LogbookParameterName.outcomeDetailMessage.name());
        logbookParamaters.putParameterValue(LogbookParameterName.agentIdentifier,
            LogbookParameterName.agentIdentifier.name());
        logbookParamaters.putParameterValue(LogbookParameterName.agentIdentifierApplicationSession,
            LogbookParameterName.agentIdentifierApplicationSession.name());
        logbookParamaters.putParameterValue(LogbookParameterName.eventIdentifierRequest,
            LogbookParameterName.eventIdentifierRequest.name());
        logbookParamaters.putParameterValue(LogbookParameterName.agentIdentifierSubmission,
            LogbookParameterName.agentIdentifierSubmission.name());
        logbookParamaters.putParameterValue(LogbookParameterName.agentIdentifierOriginating,
            LogbookParameterName.agentIdentifierOriginating.name());
        logbookParamaters.putParameterValue(LogbookParameterName.objectIdentifier,
            LogbookParameterName.objectIdentifier.name());
        logbookParamaters.putParameterValue(LogbookParameterName.objectIdentifierRequest,
            LogbookParameterName.objectIdentifierRequest.name());
        logbookParamaters.putParameterValue(LogbookParameterName.objectIdentifierIncome,
            LogbookParameterName.objectIdentifierIncome.name());
    }

    @Test
    public void selectTest() throws LogbookClientException, InvalidParseOperationException {
        LogbookLifeCyclesClientFactory
            .setConfiguration(LogbookLifeCyclesClientFactory.LogbookClientType.MOCK_LIFECYCLES, null, 0);

        final LogbookLifeCycleClient client =
            LogbookLifeCyclesClientFactory.getInstance().getLogbookLifeCyclesClient();
        assertEquals("aedqaaaaacaam7mxaaaamakvhiv4rsiaaaaz",
            client.selectLifeCycles(request).get("result").get(1).get("_id").asText());
        assertEquals("aedqaaaaacaam7mxaaaamakvhiv4rsiaaaaq",
            client.selectLifeCyclesById("eventIdentifier").get("result").get("_id").asText());
    }
}