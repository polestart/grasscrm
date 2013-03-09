/**
 * Copyright (C) 2012, Grass CRM Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gcrm.action;

import com.gcrm.domain.DocumentStatus;

/**
 * Manages the Document Status dropdown list
 * 
 */
public class DocumentStatusAction extends OptionAction<DocumentStatus> {

    private static final long serialVersionUID = -2404576552417042445L;

    @Override
    protected Class<DocumentStatus> getEntityClass() {
        return DocumentStatus.class;
    }

}
