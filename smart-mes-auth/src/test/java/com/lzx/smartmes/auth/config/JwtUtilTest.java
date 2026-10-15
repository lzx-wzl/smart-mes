package com.lzx.smartmes.auth.config;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class JwtUtilTest { @Test void createsAndParsesToken(){JwtUtil util=new JwtUtil("012345678901234567890123456789012345",10000);assertEquals("admin",util.username(util.create("admin")));} }
