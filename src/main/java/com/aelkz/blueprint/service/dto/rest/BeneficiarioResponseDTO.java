package com.aelkz.blueprint.service.dto.rest;

import com.aelkz.blueprint.service.dto.BeneficiarioDTO;

import java.util.List;

public class BeneficiarioResponseDTO extends ResponseBaseDTO {

    private List<BeneficiarioDTO> items;

    public List<BeneficiarioDTO> getItems() {
        return items;
    }

    public void setItems(List<BeneficiarioDTO> items) {
        this.items = items;
    }
}
