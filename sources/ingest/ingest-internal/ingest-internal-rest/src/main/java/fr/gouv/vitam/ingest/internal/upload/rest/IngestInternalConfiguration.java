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
package fr.gouv.vitam.ingest.internal.upload.rest;

import fr.gouv.vitam.common.ParametersChecker;

/**
 * Ingest Internal Configuration
 */
public class IngestInternalConfiguration {
	
	private static final String IS_A_MANDATORY_PARAMETER = " is a mandatory parameter";

    private String workspaceUrl;
    private String processingUrl;
	private String jettyConfig;


    /**
     * IngestInternalConfiguration empty constructor for YAMLFactory
     */
    public IngestInternalConfiguration() {
        // empty
    }
    
    /**
     * Must return the value of a 'workspaceUrl' attribute
     *
     * @return the workspaceUrl value
     */
	public String getWorkspaceUrl() {
		return workspaceUrl;
	}
	

    /**
     * @param workspaceUrl the workspace Url to set
     * @return this
     * @throws IllegalArgumentException if workspaceUrl is null or empty
     */
	public IngestInternalConfiguration setWorkspaceUrl(String workspaceUrl) {
		 ParametersChecker.checkParameter("workspaceUrl" +IS_A_MANDATORY_PARAMETER,
				 workspaceUrl);
		this.workspaceUrl = workspaceUrl;
		 return this;
	}
	
	/**
     * Must return the value of a 'workspaceUrl' attribute
     *
     * @return the workspaceUrl value
     */
	public String getProcessingUrl() {
		return processingUrl;
	}
	

    /**
     * @param processingUrl the processing Url to set
     * @return this
     * @throws IllegalArgumentException if processingUrl is null or empty
     */
	public IngestInternalConfiguration setProcessingUrl(String processingUrl) {
		 ParametersChecker.checkParameter("processingUrl" + IS_A_MANDATORY_PARAMETER ,
				 processingUrl);
		this.processingUrl = processingUrl;
		 return this;
	}

	/**
	 * getter jettyConfig
	 * @return
     */
	public String getJettyConfig() {
		return jettyConfig;
	}

	/**
	 * setter jettyConfig
	 * @param jettyConfig the jetty config
     */
	public IngestInternalConfiguration setJettyConfig(String jettyConfig) {
		ParametersChecker.checkParameter("jettyConfig" + IS_A_MANDATORY_PARAMETER ,
			jettyConfig);
		this.jettyConfig = jettyConfig;
		return this;
	}
}
