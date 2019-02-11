/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aelkz.blueprint.exception;

public enum BusinessExceptionEnum {

    // Business/Validation Exceptions
    DATE(1L, "INVALID_DATE", "Data inválida."),
    HANDLE(2L, "INVALID_HANDLE", "Handle inválido."),
    MAP_SIZE(3L, "INVALID_MAP_SIZE", "Tamanho do mapa inválido. É esperado apenas 1 registro a ser inserido no cache por operação.");

    private Long id;
    private String title;
    private String message;

    BusinessExceptionEnum(Long id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static BusinessExceptionEnum getException(String id) {
        String exID = null;
        for (BusinessExceptionEnum ex: BusinessExceptionEnum.values()) {

            exID = String.valueOf(ex.getId());

            if (exID.equalsIgnoreCase(id)) {
                return ex;
            }
        }
        exID = null;
        return null;
    }

}
