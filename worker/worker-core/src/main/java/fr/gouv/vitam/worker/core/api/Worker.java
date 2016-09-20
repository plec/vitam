/*******************************************************************************
 * This file is part of Vitam Project.
 *
 * Copyright Vitam (2012, 2016)
 *
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL license as circulated
 * by CEA, CNRS and INRIA at the following URL "http://www.cecill.info".
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
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL license and that you
 * accept its terms.
 *******************************************************************************/
package fr.gouv.vitam.worker.core.api;

import java.util.List;

import fr.gouv.vitam.processing.common.exception.HandlerNotFoundException;
import fr.gouv.vitam.processing.common.exception.ProcessingException;
import fr.gouv.vitam.processing.common.model.EngineResponse;
import fr.gouv.vitam.processing.common.model.Step;
import fr.gouv.vitam.processing.common.parameter.WorkerParameters;
import fr.gouv.vitam.worker.core.handler.ActionHandler;

/**
 * Worker Interface.
 */
// TODO REVIEW improve comment form
public interface Worker {

    /**
     * Worker execute the step's actions
     *
     * @param step {@link Step} null not allowed
     * @param workParams {@link WorkerParameters} (one and only workItem will be in workParams)
     * @return List EngineResponse {@link EngineResponse} : list of action response {OK,KO,FATAL...}
     *
     * @throws IllegalArgumentException throws when arguments are null
     * @throws HandlerNotFoundException throws when handler not found
     * @throws ProcessingException throws when error in execution
     */
    List<EngineResponse> run(WorkerParameters workParams, Step step)
        throws IllegalArgumentException, HandlerNotFoundException, ProcessingException;


    /**
     * Constructor for test.
     * 
     * @param actionName action name
     * @param actionHandler action handler
     * @return the worker instance
     */
    Worker addActionHandler(String actionName, ActionHandler actionHandler);


    /**
     * get Worker Id
     * 
     * @return id
     */
    public String getWorkerId();
}