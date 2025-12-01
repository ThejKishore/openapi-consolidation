-- Routes base table
create table if not exists gw_routes (
    id varchar(100) primary key,
    uri varchar(512) not null,
    order_no int,
    enabled boolean not null default true
);

-- Predicates associated with a route (ordered)
create table if not exists gw_route_predicates (
    route_id varchar(100) not null,
    ord int not null,
    name varchar(64) not null,
    args varchar(1024),
    constraint fk_gw_pred_route foreign key (route_id) references gw_routes(id)
);
create index if not exists idx_gw_pred_route on gw_route_predicates(route_id, ord);

-- Filters associated with a route (ordered)
create table if not exists gw_route_filters (
    route_id varchar(100) not null,
    ord int not null,
    name varchar(64) not null,
    args varchar(1024),
    constraint fk_gw_filt_route foreign key (route_id) references gw_routes(id)
);
create index if not exists idx_gw_filt_route on gw_route_filters(route_id, ord);

-- Metadata key-values for a route
create table if not exists gw_route_metadata (
    route_id varchar(100) not null,
    k varchar(128) not null,
    v varchar(1024),
    constraint fk_gw_meta_route foreign key (route_id) references gw_routes(id)
);
create index if not exists idx_gw_meta_route on gw_route_metadata(route_id);
