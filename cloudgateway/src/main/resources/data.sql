insert into gw_routes (id, uri, order_no, enabled) values
  ('employeesvc', 'http://localhost:8081/tla/emplSvc', 0, true),
  ('personsvc',   'http://localhost:8082/tla/personSvc', 0, true);

insert into gw_route_predicates (route_id, ord, name, args) values
  ('employeesvc', 0, 'Path', '/tla/emplSvc/**'),
  ('personsvc',   0, 'Path', '/tla/personSvc/**');
