USE smart_mes;

-- ============================================
-- 1. 角色初始化
-- ============================================

INSERT INTO sys_role (id, role_code, role_name)
VALUES
    (1, 'ADMIN', '管理员'),
    (2, 'PRODUCTION_MANAGER', '生产经理'),
    (3, 'OPERATOR', '操作员'),
    (4, 'DEVICE_ENGINEER', '设备工程师'),
    (5, 'SECURITY_GUARD', '保安');


-- ============================================
-- 2. 权限初始化
-- ============================================

INSERT INTO sys_permission (id, permission_code, permission_name)
VALUES
    (1, 'USER_MANAGE', '用户管理'),
    (2, 'PRODUCTION_READ', '生产读取'),
    (3, 'PRODUCTION_CREATE', '生产创建'),
    (4, 'PRODUCTION_EXECUTE', '生产执行'),
    (5, 'DEVICE_READ', '设备读取'),
    (6, 'DEVICE_CONTROL', '设备控制'),
    (7, 'DOOR_OPEN', '门禁开门'),
    (8, 'PROCESS_MANAGE', '工艺管理');


-- ============================================
-- 3. 角色与权限关联
-- ============================================

-- 管理员拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id
FROM sys_permission;

-- 其他角色权限
INSERT INTO sys_role_permission (role_id, permission_id)
VALUES
    (2, 2),
    (2, 3),
    (2, 4),
    (3, 2),
    (3, 4),
    (4, 5),
    (4, 6),
    (5, 7);


-- ============================================
-- 4. 用户初始化
-- ============================================

INSERT INTO sys_user (
    id,
    username,
    password,
    employee_no,
    real_name,
    card_no,
    status
)
VALUES
    (
        1,
        'admin',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'E001',
        '系统管理员',
        'CARD-001',
        'ACTIVE'
    ),
    (
        2,
        'manager',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'E002',
        '生产经理',
        'CARD-002',
        'ACTIVE'
    ),
    (
        3,
        'operator',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'E003',
        '操作员',
        'CARD-003',
        'ACTIVE'
    ),
    (
        4,
        'engineer',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'E004',
        '设备工程师',
        'CARD-004',
        'ACTIVE'
    );


-- ============================================
-- 5. 用户与角色关联
-- ============================================

INSERT INTO sys_user_role (user_id, role_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4);


-- ============================================
-- 6. 车间初始化
-- ============================================

INSERT INTO mes_workshop (
    id,
    workshop_code,
    workshop_name,
    status
)
VALUES
    (1, 'WS-A', '机加车间', 'ACTIVE'),
    (2, 'WS-B', '装配车间', 'ACTIVE');


-- ============================================
-- 7. 产品初始化
-- ============================================

INSERT INTO mes_product (
    id,
    product_code,
    product_name,
    specification,
    status
)
VALUES
    (1, 'GEAR-A01', '齿轮 A01', 'M20', 'ACTIVE'),
    (2, 'SHAFT-B01', '轴 B01', 'D40', 'ACTIVE');


-- ============================================
-- 8. 设备初始化
-- ============================================

INSERT INTO mes_device (
    id,
    device_code,
    device_name,
    device_type,
    workshop_id,
    ip_address,
    port,
    protocol,
    status
)
VALUES
    (
        1,
        'CNC-001',
        '数控车床 1',
        'CNC',
        1,
        '127.0.0.1',
        9101,
        'SIMULATOR',
        'ONLINE'
    ),
    (
        2,
        'CNC-002',
        '数控车床 2',
        'CNC',
        1,
        '127.0.0.1',
        9102,
        'SIMULATOR',
        'ONLINE'
    ),
    (
        3,
        'ROBOT-001',
        '装配机器人',
        'ROBOT',
        2,
        '127.0.0.1',
        9103,
        'SIMULATOR',
        'ONLINE'
    );


-- ============================================
-- 9. 工位初始化
-- ============================================

INSERT INTO mes_workstation (
    id,
    workstation_code,
    workstation_name,
    workshop_id,
    device_id,
    status
)
VALUES
    (1, 'ST-10', '车削工位', 1, 1, 'ACTIVE'),
    (2, 'ST-20', '热处理工位', 1, 2, 'ACTIVE'),
    (3, 'ST-30', '打磨工位', 1, 2, 'ACTIVE'),
    (4, 'ST-40', '检测工位', 2, 3, 'ACTIVE');


-- ============================================
-- 10. 工艺路线初始化
-- ============================================

INSERT INTO mes_process_route (
    id,
    route_code,
    route_name,
    product_id,
    version,
    status
)
VALUES
    (
        1,
        'ROUTE-GEAR-A01',
        '齿轮标准路线',
        1,
        1,
        'ACTIVE'
    ),
    (
        2,
        'ROUTE-SHAFT-B01',
        '轴标准路线',
        2,
        1,
        'ACTIVE'
    );


-- ============================================
-- 11. 工艺步骤初始化
-- ============================================

INSERT INTO mes_process_step (
    id,
    route_id,
    step_no,
    process_code,
    process_name,
    workstation_id,
    standard_duration,
    parameter_json
)
VALUES
    (
        1,
        1,
        10,
        'TURN',
        '车削',
        1,
        60,
        '{"speed":1500,"temperature":850,"duration":120}'
    ),
    (
        2,
        1,
        20,
        'HEAT',
        '热处理',
        2,
        120,
        '{"temperature":850,"duration":120}'
    ),
    (
        3,
        1,
        30,
        'GRIND',
        '打磨',
        3,
        50,
        '{"speed":1200}'
    ),
    (
        4,
        1,
        40,
        'INSPECT',
        '尺寸检测',
        4,
        30,
        '{}'
    ),
    (
        5,
        2,
        10,
        'TURN',
        '车削',
        1,
        55,
        '{"speed":1400}'
    );


-- ============================================
-- 12. 门禁初始化
-- ============================================

INSERT INTO mes_door (
    id,
    door_code,
    door_name,
    workshop_id,
    device_ip,
    device_port,
    status
)
VALUES
    (
        1,
        'DOOR-A',
        '机加车间门',
        1,
        '127.0.0.1',
        9201,
        'ONLINE'
    ),
    (
        2,
        'DOOR-B',
        '装配车间门',
        2,
        '127.0.0.1',
        9202,
        'ONLINE'
    );


-- ============================================
-- 13. 门禁权限初始化
-- ============================================

INSERT INTO mes_door_permission (
    id,
    user_id,
    door_id,
    start_time,
    end_time,
    enabled
)
VALUES
    (
        1,
        1,
        1,
        '2020-01-01 00:00:00',
        '2035-01-01 00:00:00',
        TRUE
    ),
    (
        2,
        1,
        2,
        '2020-01-01 00:00:00',
        '2035-01-01 00:00:00',
        TRUE
    );


-- ============================================
-- 14. 物料初始化
-- ============================================

INSERT INTO mes_material (
    id,
    material_code,
    material_name,
    specification,
    unit
)
VALUES
    (
        1,
        'MAT-001',
        '合金钢',
        '40Cr',
        'KG'
    );


-- ============================================
-- 15. 供应商初始化
-- ============================================

INSERT INTO mes_supplier (
    id,
    supplier_code,
    supplier_name,
    status
)
VALUES
    (
        1,
        'SUP-001',
        '示例供应商',
        'ACTIVE'
    );