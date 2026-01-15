-- Insert routes with JSON columns for predicates, filters, and metadata
insert into gw_routes (id, uri, order_no, enabled, version, predicates, filters, metadata, created_by, updated_by, created_at, updated_at) values
  ('employeesvc', 'http://localhost:8081/tla/emplSvc', 0, true, 1,
   '[{"name":"Path","args":"/tla/emplSvc/**"}]',
   '[]',
   '{"team":"employee-services","environment":"development"}',
   'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

  ('personsvc', 'http://localhost:8082/tla/personSvc', 0, true, 1,
   '[{"name":"Path","args":"/tla/personSvc/**"}]',
   '[]',
   '{"team":"person-services","environment":"development"}',
   'admin', 'admin', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sample audit entries
insert into gw_route_audit (route_id, action, version, created_by, created_at, old_value, new_value, description) values
  ('employeesvc', 'CREATE', 1, 'admin', CURRENT_TIMESTAMP, null,
   '{"id":"employeesvc","uri":"http://localhost:8081/tla/emplSvc","predicates":[{"name":"Path","args":"/tla/emplSvc/**"}],"filters":[],"metadata":{"team":"employee-services","environment":"development"}}',
   'Initial route creation'),

  ('personsvc', 'CREATE', 1, 'admin', CURRENT_TIMESTAMP, null,
   '{"id":"personsvc","uri":"http://localhost:8082/tla/personSvc","predicates":[{"name":"Path","args":"/tla/personSvc/**"}],"filters":[],"metadata":{"team":"person-services","environment":"development"}}',
   'Initial route creation');

