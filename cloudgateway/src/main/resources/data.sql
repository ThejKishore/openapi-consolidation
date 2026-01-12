insert into gw_routes (id, uri, order_no, enabled, version, created_by, updated_by, created_at, updated_at) values
  ('employeesvc', 'http://localhost:8081/tla/emplSvc', 0, true, 1, 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('personsvc',   'http://localhost:8082/tla/personSvc', 0, true, 1, 'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

insert into gw_route_predicates (route_id, ord, name, args) values
  ('employeesvc', 0, 'Path', '/tla/emplSvc/**'),
  ('personsvc',   0, 'Path', '/tla/personSvc/**');

-- Sample audit entries
insert into gw_route_audit (route_id, action, version, created_by, created_at, old_value, new_value, description) values
  ('employeesvc', 'CREATE', 1, 'admin', CURRENT_TIMESTAMP, null, '{"id":"employeesvc","uri":"http://localhost:8081/tla/emplSvc"}', 'Initial route creation'),
  ('personsvc', 'CREATE', 1, 'admin', CURRENT_TIMESTAMP, null, '{"id":"personsvc","uri":"http://localhost:8082/tla/personSvc"}', 'Initial route creation');

