/**
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
 */
package fr.gouv.vitam.functional.administration.common;

import org.bson.Document;

import fr.gouv.vitam.common.database.server.mongodb.VitamDocument;

/**
 * FileRules create the template of FileRules from VitamDocument
 */
public class FileRules extends VitamDocument<FileRules> {


    private static final long serialVersionUID = 2471943065920459435L;

    public static final String RULEID = "RuleId";

    private static final String RULETYPE = "RuleType";
    private static final String RULEVALUE = "RuleValue";
    private static final String RULEDESCRIPTION = "RuleDescription";
    private static final String RULEDURATION = "RuleDuration";
    private static final String RULEMEASUREMENT = "RuleMeasurement";

    /**
     * Constructor
     */

    public FileRules() {

    }

    /**
     * Constructor
     * 
     * @param document
     */
    public FileRules(Document document) {
        super(document);

    }

    /**
     * setRuleId
     * 
     * @param ruleId
     * @return FileRules
     */
    public FileRules setRuleId(String ruleId) {
        this.append(RULEID, ruleId);
        return this;
    }

    /**
     * setRuleType
     * 
     * @param ruleType
     * @return FileRules
     */
    public FileRules setRuleType(String ruleType) {
        this.append(RULETYPE, ruleType);
        return this;
    }

    /**
     * setRuleValue
     * 
     * @param ruleValue
     * @return FileRules
     */
    public FileRules setRuleValue(String ruleValue) {
        this.append(RULEVALUE, ruleValue);
        return this;
    }

    /**
     * setRuleDescription
     * 
     * @param ruleDescription
     * @return FileRules
     */

    public FileRules setRuleDescription(String ruleDescription) {
        this.append(RULEDESCRIPTION, ruleDescription);
        return this;
    }

    /**
     * setRuleDuration
     * 
     * @param ruleDuration
     * @return FileRules
     */
    public FileRules setRuleDuration(String ruleDuration) {
        this.append(RULEDURATION, ruleDuration);
        return this;
    }

    /**
     * setRuleMeasurement
     * 
     * @param ruleMeasurement
     * @return FileRules
     */
    public FileRules setRuleMeasurement(String ruleMeasurement) {
        this.append(RULEMEASUREMENT, ruleMeasurement);
        return this;
    }
}