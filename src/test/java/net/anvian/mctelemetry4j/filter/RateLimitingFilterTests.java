package net.anvian.mctelemetry4j.filter;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitingFilterTests {

    @Test
    void directClientCannotBypassLimitWithForwardedFor() throws Exception {
        RateLimitingFilter filter = newFilter(List.of("172.30.0.2"));
        AtomicInteger forwarded = new AtomicInteger();

        for (int i = 0; i < 20; i++) {
            assertEquals(200, filter(filter, "127.0.0.1", "198.51.100." + i, forwarded).getStatus());
        }

        MockHttpServletResponse limited = filter(filter, "127.0.0.1", "203.0.113.1", forwarded);
        assertEquals(429, limited.getStatus());
        assertEquals("{\"error\":\"Too many requests\"}", limited.getContentAsString());
        assertEquals(20, forwarded.get());
    }

    @Test
    void trustedProxySeparatesClientsByForwardedFor() throws Exception {
        RateLimitingFilter filter = newFilter(List.of("172.30.0.2"));
        AtomicInteger forwarded = new AtomicInteger();

        for (int i = 0; i < 20; i++) {
            filter(filter, "172.30.0.2", "198.51.100.1", forwarded);
        }

        MockHttpServletResponse firstClientLimited = filter(filter, "172.30.0.2", "198.51.100.1", forwarded);
        MockHttpServletResponse secondClientAllowed = filter(filter, "172.30.0.2", "198.51.100.2", forwarded);

        assertEquals(429, firstClientLimited.getStatus());
        assertEquals(200, secondClientAllowed.getStatus());
        assertEquals(21, forwarded.get());
    }

    @Test
    void cloudflareConnectingIpSeparatesClientsBehindSameProxy() throws Exception {
        RateLimitingFilter filter = newFilter(List.of("172.30.0.2"));
        AtomicInteger forwarded = new AtomicInteger();

        for (int i = 0; i < 20; i++) {
            filter(filter, "10.0.1.170", "10.0.1.69", "198.51.100.1", forwarded);
        }

        MockHttpServletResponse firstClientLimited = filter(
                filter, "10.0.1.170", "10.0.1.69", "198.51.100.1", forwarded);
        MockHttpServletResponse secondClientAllowed = filter(
                filter, "10.0.1.170", "10.0.1.69", "198.51.100.2", forwarded);

        assertEquals(429, firstClientLimited.getStatus());
        assertEquals(200, secondClientAllowed.getStatus());
        assertEquals(21, forwarded.get());
    }

    @Test
    void blankCloudflareConnectingIpFallsBackToTrustedProxyForwardedFor() throws Exception {
        RateLimitingFilter filter = newFilter(List.of("172.30.0.2"));
        AtomicInteger forwarded = new AtomicInteger();

        for (int i = 0; i < 20; i++) {
            filter(filter, "172.30.0.2", "198.51.100.1", "", forwarded);
        }

        MockHttpServletResponse firstClientLimited = filter(
                filter, "172.30.0.2", "198.51.100.1", "", forwarded);
        MockHttpServletResponse secondClientAllowed = filter(
                filter, "172.30.0.2", "198.51.100.2", "", forwarded);

        assertEquals(429, firstClientLimited.getStatus());
        assertEquals(200, secondClientAllowed.getStatus());
        assertEquals(21, forwarded.get());
    }

    private RateLimitingFilter newFilter(List<String> trustedProxies) {
        RateLimitingFilter filter = new RateLimitingFilter(Caffeine.newBuilder().build());
        ReflectionTestUtils.setField(filter, "trustedProxyAddresses", trustedProxies);
        return filter;
    }

    private MockHttpServletResponse filter(RateLimitingFilter filter, String remoteAddress, String forwardedFor, AtomicInteger forwarded) throws Exception {
        return filter(filter, remoteAddress, forwardedFor, null, forwarded);
    }

    private MockHttpServletResponse filter(
            RateLimitingFilter filter,
            String remoteAddress,
            String forwardedFor,
            String cfConnectingIp,
            AtomicInteger forwarded) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/data");
        request.setRemoteAddr(remoteAddress);
        request.addHeader("X-Forwarded-For", forwardedFor);
        if (cfConnectingIp != null) {
            request.addHeader("CF-Connecting-IP", cfConnectingIp);
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, (ignoredRequest, ignoredResponse) -> forwarded.incrementAndGet());
        return response;
    }
}
