# Smart-MES 智能制造执行系统

面向离散制造场景的 Java 17 / Spring Boot 3 多模块后端。项目实现了生产订单状态机、工艺路线、设备数据采集、智能门禁和 ERP/SRM 模拟集成；前端不在本仓库范围内。

## Architecture

```text
PLC Simulator --TCP JSON lines/HTTP--> Collector --RabbitMQ--> Device service
                                                        |       |-- MySQL history/alarm
                                                        |       |-- Redis latest state/idempotency
                                                        |       '-- WebSocket /ws/device-status
ERP --> Integration --> Production order      SRM mock <-- Integration
JWT/RBAC --> Auth/each protected service      Control --> protocol adapters --> simulator/TCP
```

## Modules

| Module | Responsibility | Port |
|---|---|---:|
| smart-mes-auth | JWT login, Security 6 filter chain, RBAC | 9001 |
| smart-mes-production | production orders, status machine, optimistic lock | 9002 |
| smart-mes-process | process routes and steps | 9003 |
| smart-mes-device | device registry, Redis cache, MQ consumer, WebSocket | 9004 |
| smart-mes-control | door/device command audit and protocol adapters | 9005 |
| smart-mes-collector | HTTP and TCP:9009 telemetry ingress | 9006 |
| smart-mes-integration | ERP idempotent import, SRM client/mock | 9007 |
| smart-mes-plc-simulator | CNC-001/CNC-002/ROBOT-001 telemetry simulator | 9010 |

## Run

1. In the repository root run `docker compose up -d`; use `docker compose ps` to await healthy MySQL, Redis and RabbitMQ.
2. Start services in this order, each in a terminal: auth, production, process, device, control, integration, collector, plc-simulator. Example: `mvn -pl smart-mes-device spring-boot:run`.
3. Login with `POST http://localhost:9001/api/auth/login` and pass `Authorization: Bearer <token>` to protected endpoints. RabbitMQ management is http://localhost:15672 (guest/guest).

Default database credentials are root/123456. Default application accounts (all password `123456`) are: admin, manager, operator and engineer. Passwords in `sql/02_data.sql` are BCrypt hashes.

Swagger UI is exposed by the authenticated applications at `http://localhost:{port}/swagger-ui/index.html`. The device browser client connects to `ws://localhost:9004/ws/device-status`; every telemetry update is a JSON status message.

## Important design decisions

- Production transitions are only permitted as CREATED→PLANNED→RELEASED→RUNNING→PAUSED/RUNNING→COMPLETED, with CREATED/PLANNED→CANCELLED. A MyBatis-Plus `version` field prevents two concurrent updates from silently overwriting one another.
- The collector validates ingress and publishes to `mes.device.exchange`; telemetry uses `device.telemetry` / `mes.device.telemetry.queue`, alarms use `device.alarm`, and failed telemetry reaches `mes.device.telemetry.dlq`. Manual acknowledgment avoids acknowledging before processing succeeds.
- Device history and alarms are durable MySQL records. The latest state follows cache-aside at `smart-mes:device:{id}:status` (5-minute TTL); consumer idempotency uses `smart-mes:mq:processed:{messageId}` (24 hours).
- Maintenance claiming uses Redis SET NX EX with `smart-mes:lock:maintenance:{deviceId}`; release compares the request ID in Lua so a caller cannot delete a replacement lock.
- Door opens and device START/STOP/RESET commands are recorded in audit tables. Controls use `ControlProtocolClient` adapters: simulator is runnable, TCP sends a line protocol, and Modbus/OPC UA are safe adapters intended to be replaced with modbus4j/Eclipse Milo in a plant.

## First 10 API checks

1. `POST /api/auth/login`
2. `GET /api/devices`
3. `GET /api/devices/1/status`
4. `POST /api/production/orders`
5. `POST /api/production/orders/{id}/plan`
6. `POST /api/production/orders/{id}/release`
7. `POST /api/process/routes`
8. `POST /api/control/doors/1/open`
9. `POST /api/control/devices/1/start`
10. `POST /api/integration/erp/orders`

For HTTP PLC mode start the simulator with `--smart-mes.simulator.mode=HTTP`; TCP is the default and reconnects on the next scheduled emission. The HTTP collector endpoint is `POST /api/collector/telemetry`.

## Verification

```text
mvn clean package -DskipTests   # passed
mvn test                         # passed: JWT, order legal/illegal flow, ERP duplicate idempotency
```

## Interview points

1. RabbitMQ decouples frequent equipment IO from transient database failures and permits downstream fan-out.
2. Redis is for low-latency current state; MySQL is the auditable history.
3. At-least-once MQ delivery demands the messageId SETNX idempotency guard.
4. The order state machine and optimistic lock protect real scheduling consistency.
5. TCP is efficient for PLC streaming; HTTP is kept as a diagnostic/integration mode.
6. WebSocket is server push for live equipment dashboards.
7. JWT filter chain authenticates identity; RBAC authorities enforce endpoint actions.
8. Audit logs provide accountability for safety-sensitive industrial controls.
9. Adapter boundaries stop MES business code from depending on OPC UA/Modbus vendors.
10. ERP unique erp_order_no makes retries safely idempotent; SRM uses RestClient plus explicit failure reporting.

## Resume Description

Smart-MES 智能制造执行系统：基于 Spring Boot、Spring Security、MyBatis-Plus、Redis 与 RabbitMQ 构建离散制造 MES 后端，实现生产订单状态机与乐观锁、工艺路线、设备管理、门禁与设备控制审计；设计 TCP Socket + RabbitMQ PLC 采集链路，使用 Redis 缓存最新设备状态并通过 WebSocket 推送；实现 ERP 幂等订单接入、SRM 库存模拟查询、JWT/RBAC、MQ 幂等及 Redis 维修任务锁。
