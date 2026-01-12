-- Routes base table
create table if not exists gw_routes (
    id varchar(100) primary key,
    uri varchar(512) not null,
    order_no int,
    enabled boolean not null default true,
    version int default 1,
    created_by varchar(255),
    updated_by varchar(255),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
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

-- Audit trail for all route changes
create table if not exists gw_route_audit (
    audit_id bigint primary key auto_increment,
    route_id varchar(100) not null,
    action varchar(20) not null,
    version int not null,
    created_by varchar(255),
    created_at timestamp default current_timestamp,
    old_value clob,
    new_value clob,
    description text,
    constraint fk_gw_audit_route foreign key (route_id) references gw_routes(id)
);
create index if not exists idx_gw_audit_route on gw_route_audit(route_id);
create index if not exists idx_gw_audit_created_at on gw_route_audit(created_at);
create index if not exists idx_gw_audit_created_by on gw_route_audit(created_by);

