/*
 * Copyright 2024-2025 NetCracker Technology Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qubership.integration.platform.runtime.catalog.service.exportimport.migrations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

public interface ImportFileMigration {
    String IMPORT_VERSION_FIELD_OLD = "fileVersion";
    String IMPORT_MIGRATIONS_FIELD = "migrations";
    String IMPORT_CONTENT_FIELD = "content";

    int getVersion();

    ObjectNode makeMigration(ObjectNode fileNode) throws JsonProcessingException;
}
