-- Routes base table (simplified - predicates, filters, and metadata as JSON columns)
create table if not exists gw_routes (
    id varchar(100) primary key,
    uri varchar(512) not null,
    order_no int,
    enabled boolean not null default true,
    version int default 1,
    predicates longtext,           -- JSON array of predicate objects: [{name, args}, ...]
    filters longtext,              -- JSON array of filter objects: [{name, args}, ...]
    metadata longtext,             -- JSON object for key-value pairs: {key: value, ...}
    created_by varchar(255),
    updated_by varchar(255),
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);
create index if not exists idx_gw_routes_order on gw_routes(order_no);
create index if not exists idx_gw_routes_enabled on gw_routes(enabled);

-- Audit trail for all route changes (no FK constraint - soft reference via route_id)
create table if not exists gw_route_audit (
    audit_id bigint primary key auto_increment,
    route_id varchar(100) not null,
    action varchar(20) not null,
    version int not null,
    created_by varchar(255),
    created_at timestamp default current_timestamp,
    old_value longtext,
    new_value longtext,
    description text
);
create index if not exists idx_gw_audit_route on gw_route_audit(route_id);
create index if not exists idx_gw_audit_created_at on gw_route_audit(created_at);
create index if not exists idx_gw_audit_created_by on gw_route_audit(created_by);

