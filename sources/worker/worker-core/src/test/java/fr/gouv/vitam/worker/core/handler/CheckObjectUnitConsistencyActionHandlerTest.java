/*******************************************************************************
 * Copyright French Prime minister Office/SGMAP/DINSIC/Vitam Program (2015-2019)
 *
 * contact.vitam@culture.gouv.fr
 *
 * This software is a computer program whose purpose is to implement a digital archiving back-office system managing
 * high volumetry securely and efficiently.
 *
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL 2.1 license as
 * circulated by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 *
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL 2.1 license and that you
 * accept its terms.
 *******************************************************************************/
package fr.gouv.vitam.worker.core.handler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import fr.gouv.vitam.common.PropertiesUtils;
import fr.gouv.vitam.common.exception.InvalidParseOperationException;
import fr.gouv.vitam.common.guid.GUIDFactory;
import fr.gouv.vitam.processing.common.exception.ProcessingException;
import fr.gouv.vitam.processing.common.model.EngineResponse;
import fr.gouv.vitam.processing.common.model.StatusCode;
import fr.gouv.vitam.processing.common.parameter.WorkerParameters;
import fr.gouv.vitam.processing.common.parameter.WorkerParametersFactory;
import fr.gouv.vitam.worker.core.api.HandlerIO;
import fr.gouv.vitam.workspace.api.exception.ContentAddressableStorageNotFoundException;
import fr.gouv.vitam.workspace.api.exception.ContentAddressableStorageServerException;
import fr.gouv.vitam.workspace.client.WorkspaceClient;
import fr.gouv.vitam.workspace.client.WorkspaceClientFactory;

@RunWith(PowerMockRunner.class)

@PrepareForTest({WorkspaceClientFactory.class})
public class CheckObjectUnitConsistencyActionHandlerTest {
    
    CheckObjectUnitConsistencyActionHandler handler;
    private static final String HANDLER_ID = "CheckObjectUnitConsistency";
    
    private static final String OBJECT_GROUP_ID_TO_GUID_MAP = "OBJECT_GROUP_ID_TO_GUID_MAP_obj.json";
    private static final String OG_AU = "OG_TO_ARCHIVE_ID_MAP_obj.json";
    
    private static final String EMPTY = "EMPTY_MAP.json";
    
    private WorkspaceClient workspaceClient;
    private static final String OBJ = "obj";

    private final WorkerParameters params = WorkerParametersFactory.newWorkerParameters().setWorkerGUID(GUIDFactory
        .newGUID()).setContainerName(OBJ).setUrlWorkspace(OBJ).setUrlMetadata(OBJ).setObjectName(OBJ)
        .setCurrentStep("TEST");
    private HandlerIO action;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(WorkspaceClientFactory.class);
        workspaceClient = mock(WorkspaceClient.class);
        action = new HandlerIO("");
    }

    @Test
    public void givenObjectUnitConsistencyCheckWhenNotFindBDOWithoutOGAndOGNonReferencedByArchiveUnitThenResponseOK() 
        throws ContentAddressableStorageNotFoundException, ContentAddressableStorageServerException, InvalidParseOperationException, IOException, ProcessingException {
        
        action.addInput(PropertiesUtils.getResourcesFile(EMPTY));
        action.addInput(PropertiesUtils.getResourcesFile(EMPTY));
        
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
                
        handler = new CheckObjectUnitConsistencyActionHandler();
        
        assertEquals(CheckObjectUnitConsistencyActionHandler.getId(), HANDLER_ID);
        final EngineResponse response = handler.execute(params, action);
        assertEquals(response.getStatus(), StatusCode.OK);
    }
    
    @Test
    public void givenObjectUnitConsistencyCheckWhenFindBDOWithoutOGAndOGNonReferencedByArchiveUnitThenResponseKO() 
        throws ContentAddressableStorageNotFoundException, ContentAddressableStorageServerException, InvalidParseOperationException, IOException, ProcessingException {
        
        action.addInput(PropertiesUtils.getResourcesFile(OG_AU));
        action.addInput(PropertiesUtils.getResourcesFile(OBJECT_GROUP_ID_TO_GUID_MAP));
        
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
                
        handler = new CheckObjectUnitConsistencyActionHandler();
        
        assertEquals(CheckObjectUnitConsistencyActionHandler.getId(), HANDLER_ID);
        final EngineResponse response = handler.execute(params, action);
        assertEquals(response.getStatus(), StatusCode.KO);
    }

}
