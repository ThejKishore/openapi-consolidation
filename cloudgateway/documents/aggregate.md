```bash
(base) thejkaruneegar@Thejs-MacBook-Pro-2:~/open-api-workspace|main⚡ ⇒  http :8081/tla/emplSvc/actuator/health
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/vnd.spring-boot.actuator.v3+json
Date: Tue, 12 Aug 2025 03:00:52 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "status": "UP"
}


(base) thejkaruneegar@Thejs-MacBook-Pro-2:~/open-api-workspace|main⚡ ⇒  http :8082/tla/personSvc/actuator/health
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/vnd.spring-boot.actuator.v3+json
Date: Tue, 12 Aug 2025 03:01:35 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "status": "UP"
}


(base) thejkaruneegar@Thejs-MacBook-Pro-2:~/open-api-workspace|main⚡ ⇒  http :9000/health/aggregate                              
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/json
Date: Tue, 12 Aug 2025 03:02:45 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "employeesvc": {
        "aggregatedStatus": "UP",
        "instances": [
            {
                "details": {
                    "status": "UP"
                },
                "instance": "http://localhost:8081",
                "status": "UP",
                "url": "http://localhost:8081/tla/emplSvc/actuator/health"
            }
        ]
    },
    "personsvc": {
        "aggregatedStatus": "UP",
        "instances": [
            {
                "details": {
                    "status": "UP"
                },
                "instance": "http://localhost:8082",
                "status": "UP",
                "url": "http://localhost:8082/tla/personSvc/actuator/health"
            }
        ]
    }
}

```