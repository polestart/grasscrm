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

import com.gcrm.domain.DocumentSubCategory;

/**
 * Manages the Document SubCategory dropdown list
 * 
 */
public class DocumentSubCategoryAction extends
        OptionAction<DocumentSubCategory> {

    private static final long serialVersionUID = -2404576552417042445L;

    @Override
    protected Class<DocumentSubCategory> getEntityClass() {
        return DocumentSubCategory.class;
    }

}
