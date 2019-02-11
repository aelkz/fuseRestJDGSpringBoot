package com.aelkz.blueprint.route;

import org.springframework.web.bind.annotation.RequestMethod;

public enum RestResourcesEnum {

    // REST resources
    GET_BENEFICIARIOS(RequestMethod.GET, "/beneficiarios", "beneficiarios-api", "Listar beneficiários"),
    GET_BENEFICIARIO(RequestMethod.GET, "/beneficiario/{handle}", "beneficiario-api", "Buscar beneficiário pelo handle"),
    GET_BENEFICIARIO_CACHE(RequestMethod.GET, "/beneficiario-cache/{handle}", "beneficiario-cache-api", "Buscar beneficiário pelo handle (c/ cache)"),
    POST_BENEFICIARIOS(RequestMethod.POST, "/beneficiarios", "beneficiarios-filtered-api", "Buscar beneficiários com parâmetros");

    private RequestMethod httpMethod;
    private String resourcePath;
    private String routeId;
    private String description;

    RestResourcesEnum(RequestMethod httpMethod, String resourcePath, String routeId, String description) {
        this.httpMethod = httpMethod;
        this.resourcePath = resourcePath;
        this.routeId = routeId;
        this.description = description;
    }

    public RequestMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(RequestMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static RequestMethod getHttpMethodByResource(String resourcePath) {
        String path = null;
        for (RestResourcesEnum e: RestResourcesEnum.values()) {

            // will not ignore case. Case is sensitive to assert resource path is right.
            if (e.getResourcePath().equals(resourcePath)) {
                return e.getHttpMethod();
            }
        }
        return null;
    }

}
