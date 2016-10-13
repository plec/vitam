package fr.gouv.vitam.worker.core.handler;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.gouv.vitam.common.VitamConfiguration;
import fr.gouv.vitam.common.format.identification.FormatIdentifierFactory;
import fr.gouv.vitam.common.format.identification.exception.FileFormatNotFoundException;
import fr.gouv.vitam.common.format.identification.exception.FormatIdentifierFactoryException;
import fr.gouv.vitam.common.format.identification.exception.FormatIdentifierNotFoundException;
import fr.gouv.vitam.common.format.identification.exception.FormatIdentifierTechnicalException;
import fr.gouv.vitam.common.format.identification.model.FormatIdentifierResponse;
import fr.gouv.vitam.common.format.identification.siegfried.FormatIdentifierSiegfried;
import fr.gouv.vitam.common.json.JsonHandler;
import fr.gouv.vitam.functional.administration.client.AdminManagementClient;
import fr.gouv.vitam.functional.administration.client.AdminManagementClientFactory;
import fr.gouv.vitam.functional.administration.common.exception.ReferentialException;
import fr.gouv.vitam.processing.common.model.EngineResponse;
import fr.gouv.vitam.processing.common.model.OutcomeMessage;
import fr.gouv.vitam.processing.common.model.StatusCode;
import fr.gouv.vitam.processing.common.parameter.DefaultWorkerParameters;
import fr.gouv.vitam.processing.common.parameter.WorkerParameters;
import fr.gouv.vitam.processing.common.parameter.WorkerParametersFactory;
import fr.gouv.vitam.worker.core.api.HandlerIO;
import fr.gouv.vitam.workspace.api.exception.ContentAddressableStorageNotFoundException;
import fr.gouv.vitam.workspace.client.WorkspaceClient;
import fr.gouv.vitam.workspace.client.WorkspaceClientFactory;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.net.ssl.*")
@PrepareForTest({FormatIdentifierFactory.class, WorkspaceClientFactory.class, AdminManagementClientFactory.class})
public class FormatIdentificationActionHandlerTest {

    FormatIdentificationActionHandler handler;
    private static final String HANDLER_ID = "FormatIdentification";

    private static final String OBJECT_GROUP = "storeObjectGroupHandler/aeaaaaaaaaaam7myaaaamakxfgivuryaaaaq.json";
    private static final String OBJECT_GROUP_2 = "storeObjectGroupHandler/afaaaaaaaaaam7myaaaamakxfgivuryaaaaq.json";
    private final InputStream objectGroup = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(OBJECT_GROUP);
    private final InputStream objectGroup2 = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(OBJECT_GROUP_2);

    @Before
    public void setUp() {
        PowerMockito.mockStatic(FormatIdentifierFactory.class);
        PowerMockito.mockStatic(WorkspaceClientFactory.class);
        PowerMockito.mockStatic(AdminManagementClientFactory.class);
        deleteFiles();
    }

    @After
    public void setDown() {
        deleteFiles();
    }

    @Test
    public void getFormatIdentifierNotFound() throws Exception {
        FormatIdentifierFactory identifierFactory = PowerMockito.mock(FormatIdentifierFactory.class);
        when(FormatIdentifierFactory.getInstance()).thenReturn(identifierFactory);
        when(identifierFactory.getFormatIdentifierFor(anyObject()))
            .thenThrow(new FormatIdentifierNotFoundException(""));
        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.FATAL, response.getStatus());
        assertEquals(OutcomeMessage.GETTING_FORMAT_IDENTIFIER_FATAL, response.getOutcomeMessages().get(HANDLER_ID));
    }

    @Test
    public void getFormatIdentifierFactoryError() throws Exception {
        FormatIdentifierFactory identifierFactory = PowerMockito.mock(FormatIdentifierFactory.class);
        when(FormatIdentifierFactory.getInstance()).thenReturn(identifierFactory);
        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        when(identifierFactory.getFormatIdentifierFor(anyObject())).thenThrow(new FormatIdentifierFactoryException(""));
        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.FATAL, response.getStatus());
        assertEquals(OutcomeMessage.GETTING_FORMAT_IDENTIFIER_FATAL, response.getOutcomeMessages().get(HANDLER_ID));
    }

    @Test
    public void getFormatIdentifierTechnicalError() throws Exception {
        FormatIdentifierFactory identifierFactory = PowerMockito.mock(FormatIdentifierFactory.class);
        when(FormatIdentifierFactory.getInstance()).thenReturn(identifierFactory);
        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        when(identifierFactory.getFormatIdentifierFor(anyObject()))
            .thenThrow(new FormatIdentifierTechnicalException(""));
        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.FATAL, response.getStatus());
        assertEquals(OutcomeMessage.GETTING_FORMAT_IDENTIFIER_FATAL, response.getOutcomeMessages().get(HANDLER_ID));
    }

    @Test
    public void gettingJsonFromWorkspaceError() throws Exception {
        FormatIdentifierSiegfried siegfried = getMockedFormatIdentifierSiegfried();

        WorkspaceClient workspaceClient = mock(WorkspaceClient.class);
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
        when(workspaceClient.getObject(anyObject(), anyObject()))
            .thenThrow(new ContentAddressableStorageNotFoundException(""));
        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.FATAL, response.getStatus());
        assertEquals(OutcomeMessage.FILE_FORMAT_TECHNICAL_ERROR, response.getOutcomeMessages().get(HANDLER_ID));
        deleteFiles();
    }

    @Test
    public void formatNotFoundInInternalReferential() throws Exception {
        FormatIdentifierSiegfried siegfried =
            getMockedFormatIdentifierSiegfried();

        when(siegfried.analysePath(anyObject())).thenReturn(getFormatIdentifierResponseList());

        WorkspaceClient workspaceClient = mock(WorkspaceClient.class);
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
        when(workspaceClient.getObject(anyObject(), anyObject())).thenReturn(objectGroup)
            .thenReturn(IOUtils.toInputStream("VitamTest"));

        AdminManagementClient adminManagementClient = getMockedAdminManagementClient();
        when(adminManagementClient.getFormats(anyObject())).thenReturn(getAdminManagementJson2Result());

        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.KO, response.getStatus());
        assertEquals(OutcomeMessage.FILE_FORMAT_PUID_NOT_FOUND, response.getOutcomeMessages().get(HANDLER_ID));
    }

    private AdminManagementClient getMockedAdminManagementClient() {
        AdminManagementClient adminManagementClient = mock(AdminManagementClient.class);
        AdminManagementClientFactory adminManagementClientFactory =
            PowerMockito.mock(AdminManagementClientFactory.class);
        when(AdminManagementClientFactory.getInstance()).thenReturn(adminManagementClientFactory);
        when(adminManagementClientFactory.getAdminManagementClient()).thenReturn(adminManagementClient);
        return adminManagementClient;
    }

    @Test
    public void formatIdentificationWarning() throws Exception {
        FormatIdentifierSiegfried siegfried = getMockedFormatIdentifierSiegfried();

        when(siegfried.analysePath(anyObject())).thenReturn(getFormatIdentifierResponseList());

        WorkspaceClient workspaceClient = mock(WorkspaceClient.class);
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
        when(workspaceClient.getObject(anyObject(), anyObject())).thenReturn(objectGroup)
            .thenReturn(IOUtils.toInputStream("VitamTest"));
        doNothing().when(workspaceClient).putObject(anyObject(), anyObject(), anyObject());

        AdminManagementClient adminManagementClient =
            getMockedAdminManagementClient();

        when(adminManagementClient.getFormats(anyObject())).thenReturn(getAdminManagementJson());

        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.WARNING, response.getStatus());
        assertEquals(OutcomeMessage.FILE_FORMAT_METADATA_UPDATE, response.getOutcomeMessages().get(HANDLER_ID));
    }

    @Test
    public void formatIdentificationWithoutFormat() throws Exception {
        FormatIdentifierSiegfried siegfried = getMockedFormatIdentifierSiegfried();

        when(siegfried.analysePath(anyObject())).thenReturn(getFormatIdentifierResponseList());

        WorkspaceClient workspaceClient = mock(WorkspaceClient.class);
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
        when(workspaceClient.getObject(anyObject(), anyObject())).thenReturn(objectGroup2)
            .thenReturn(IOUtils.toInputStream("VitamTest"));
        doNothing().when(workspaceClient).putObject(anyObject(), anyObject(), anyObject());

        AdminManagementClient adminManagementClient =
            getMockedAdminManagementClient();

        when(adminManagementClient.getFormats(anyObject())).thenReturn(getAdminManagementJson());

        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.WARNING, response.getStatus());
        assertEquals(OutcomeMessage.FILE_FORMAT_METADATA_UPDATE, response.getOutcomeMessages().get(HANDLER_ID));
    }

    @Test
    public void formatIdentificationNotFound() throws Exception {
        FormatIdentifierSiegfried siegfried =
            getMockedFormatIdentifierSiegfried();

        when(siegfried.analysePath(anyObject())).thenThrow(new FileFormatNotFoundException(""));

        WorkspaceClient workspaceClient = mock(WorkspaceClient.class);
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
        when(workspaceClient.getObject(anyObject(), anyObject())).thenReturn(objectGroup)
            .thenReturn(IOUtils.toInputStream("VitamTest"));

        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.KO, response.getStatus());
        assertEquals(OutcomeMessage.FILE_FORMAT_NOT_FOUND, response.getOutcomeMessages().get(HANDLER_ID));
    }

    @Test
    public void formatIdentificationReferentialException() throws Exception {
        FormatIdentifierSiegfried siegfried =
            getMockedFormatIdentifierSiegfried();

        when(siegfried.analysePath(anyObject())).thenReturn(getFormatIdentifierResponseList());

        AdminManagementClient adminManagementClient =
            getMockedAdminManagementClient();

        when(adminManagementClient.getFormats(anyObject())).thenThrow(new ReferentialException(""));

        WorkspaceClient workspaceClient = mock(WorkspaceClient.class);
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
        when(workspaceClient.getObject(anyObject(), anyObject())).thenReturn(objectGroup)
            .thenReturn(IOUtils.toInputStream("VitamTest"));

        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.FATAL, response.getStatus());
        assertEquals(OutcomeMessage.FILE_FORMAT_REFERENTIAL_ERROR, response.getOutcomeMessages().get(HANDLER_ID));
    }

    @Test
    public void formatIdentificationTechnicalException() throws Exception {
        FormatIdentifierSiegfried siegfried =
            getMockedFormatIdentifierSiegfried();

        when(siegfried.analysePath(anyObject())).thenThrow(new FormatIdentifierTechnicalException(""));

        WorkspaceClient workspaceClient = mock(WorkspaceClient.class);
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
        when(workspaceClient.getObject(anyObject(), anyObject())).thenReturn(objectGroup);

        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.FATAL, response.getStatus());
        assertEquals(OutcomeMessage.FILE_FORMAT_TECHNICAL_ERROR, response.getOutcomeMessages().get(HANDLER_ID));
    }

    @Test
    public void formatIdentificationFileIdentifierDoesNotRespond() throws Exception {
        FormatIdentifierSiegfried siegfried = getMockedFormatIdentifierSiegfried();

        when(siegfried.analysePath(anyObject())).thenThrow(new FormatIdentifierNotFoundException(""));

        WorkspaceClient workspaceClient = mock(WorkspaceClient.class);
        PowerMockito.when(WorkspaceClientFactory.create(Mockito.anyObject())).thenReturn(workspaceClient);
        when(workspaceClient.getObject(anyObject(), anyObject())).thenReturn(objectGroup)
            .thenReturn(IOUtils.toInputStream("VitamTest"));

        handler = new FormatIdentificationActionHandler();
        WorkerParameters params = getDefaultWorkerParameters();
        HandlerIO handlerIO = new HandlerIO("");

        EngineResponse response = handler.execute(params, handlerIO);
        assertEquals(StatusCode.FATAL, response.getStatus());
        assertEquals(OutcomeMessage.FILE_FORMAT_TOOL_DOES_NOT_ANSWER, response.getOutcomeMessages().get(HANDLER_ID));
    }

    private FormatIdentifierSiegfried getMockedFormatIdentifierSiegfried()
        throws FormatIdentifierNotFoundException, FormatIdentifierFactoryException, FormatIdentifierTechnicalException {
        FormatIdentifierSiegfried siegfried = mock(FormatIdentifierSiegfried.class);
        FormatIdentifierFactory identifierFactory = PowerMockito.mock(FormatIdentifierFactory.class);
        when(FormatIdentifierFactory.getInstance()).thenReturn(identifierFactory);
        when(identifierFactory.getFormatIdentifierFor(anyObject())).thenReturn(siegfried);
        return siegfried;
    }

    private DefaultWorkerParameters getDefaultWorkerParameters() {
        return WorkerParametersFactory.newWorkerParameters("pId", "stepId", "containerName",
            "currentStep", "objName", "metadataURL", "workspaceURL");
    }

    private List<FormatIdentifierResponse> getFormatIdentifierResponseList() {
        List<FormatIdentifierResponse> list = new ArrayList<>();
        list.add(new FormatIdentifierResponse("OpenDocument Presentation", "application/vnd.oasis.opendocument" +
            ".presentation",
            "fmt/293", "pronom"));
        return list;
    }

    private JsonNode getAdminManagementJson() {
        ObjectNode node = JsonHandler.createObjectNode();
        node.put("PUID", "fmt/293");
        node.put("Name", "OpenDocument Presentation");
        node.put("MIMEType", "application/vnd.oasis.opendocument");
        ArrayNode ret = JsonHandler.createArrayNode();
        ret.add(node);
        return ret;
    }

    private JsonNode getAdminManagementJson2Result() {
        ObjectNode node2 = JsonHandler.createObjectNode();
        return node2;
    }

    private void deleteFiles() {
        String fileName1 = "containerNameobjNameaeaaaaaaaaaam7myaaaamakxfgivurqaaaaq";
        String fileName2 = "containerNameobjNameaeaaaaaaaaaam7myaaaamakxfgivuuiaaaaq";
        String fileName3 = "containerNameobjNameaeaaaaaaaaaam7myaaaamakxfgivuuyaaaaq";
        String fileName4 = "containerNameobjNameaeaaaaaaaaaam7myaaaamakxfgivuvaaaaaq";
        File file = new File(VitamConfiguration.getVitamTmpFolder() + "/" + fileName1);
        if (file.exists()) {
            file.delete();
        }
        file = new File(VitamConfiguration.getVitamTmpFolder() + "/" + fileName2);
        if (file.exists()) {
            file.delete();
        }
        file = new File(VitamConfiguration.getVitamTmpFolder() + "/" + fileName3);
        if (file.exists()) {
            file.delete();
        }
        file = new File(VitamConfiguration.getVitamTmpFolder() + "/" + fileName4);
        if (file.exists()) {
            file.delete();
        }
    }
}
