package com.tk.learn.cloudgateway.repository;

import com.tk.learn.cloudgateway.dynamic.DbRouteModels;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RouteRepository extends CrudRepository<DbRouteModels.DbRoute, String> {
    List<DbRouteModels.DbRoute> findByEnabled(Boolean enabled);
}

