package com.example.serviceproviders_service.dto.Admin;

import com.example.serviceproviders_service.entity.serviceProvider.ServiceProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AllServiceproviderResponse {
    private List<Providerdto> providers;

}
