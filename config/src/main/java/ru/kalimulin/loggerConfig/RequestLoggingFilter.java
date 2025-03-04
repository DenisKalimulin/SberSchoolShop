package ru.kalimulin.loggerConfig;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Управление порядка выполнения компонентов (HIGHEST_PRECEDENCE - приоритетность)
public class RequestLoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private static final Map<String, String> OPERATION_CODES = Map.ofEntries(
            Map.entry("POST:/shop/address/create", "ADDRESS_CREATE"),
            Map.entry("PUT:/shop/address/update/{addressId}", "ADDRESS_UPDATE"),
            Map.entry("DELETE:/shop/address/{id}", "ADDRESS_DELETE"),
            Map.entry("POST:/shop/admin/users/{userEmail}/roles", "ADD_ADMIN_ROLE"),
            Map.entry("POST:/shop/admin/categories", "CREATE_CATEGORY"),
            Map.entry("PUT:/shop/admin/categories/update", "UPDATE_CATEGORY"),
            Map.entry("DELETE:/shop/admin/categories/{id}", "DELETE_CATEGORY"),
            Map.entry("DELETE:/shop/admin/review/{id}", "DELETE_REVIEW"),
            Map.entry("POST:/shop/auth/register", "REGISTER_USER"),
            Map.entry("POST:/shop/auth/login", "LOGIN_USER"),
            Map.entry("POST:/shop/auth/logout", "LOGOUT_USER"),
            Map.entry("DELETE:/shop/cart/clear", "CLEAR_CART"),
            Map.entry("DELETE:/shop/cart/remove/{productId}", "DELETE_PRODUCT_FROM_CART"),
            Map.entry("POST:/shop/cart", "ADD_ITEM_TO_CART"),
            Map.entry("POST:/shop/favorites/{productId}", "ADD_TO_FAVORITES"),
            Map.entry("DELETE:/shop/favorites/{productId}", "REMOVE_FROM_FAVORITES"),
            Map.entry("POST:/shop/images/add/{productId}", "ADD_IMAGE"),
            Map.entry("DELETE:/shop/images/delete/{imageId}", "DELETE_IMAGE"),
            Map.entry("POST:/shop/orders/create", "ORDER_CREATE"),
            Map.entry("POST:/shop/orders/payment/{orderId}", "PAYMENT_ORDER"),
            Map.entry("DELETE:/shop/orders/{id}", "DELETE_ORDERS"),
            Map.entry("POST:/shop/products", "ADD_PRODUCT"),
            Map.entry("PUT:/shop/products/{id}", "UPDATE_PRODUCT"),
            Map.entry("PATCH:/shop/products/{id}", "PRODUCT_CHANGE_STATUS"),
            Map.entry("DELETE:/shop/product/{id}", "DELETE_PRODUCT"),
            Map.entry("POST:/shop/review/leave/{sellerId}", "LEAVE_REVIEW"),
            Map.entry("POST:/shop/role/seller", "PURCHASE_SELLER_ROLE"),
            Map.entry("PUT:/shop/users/me/update", "UPDATE_USER"),
            Map.entry("DELETE:/shop/users/me/delete", "DELETE_USER"),
            Map.entry("POST:/shop/wallet/create", "CREATE_WALLET"),
            Map.entry("POST:/shop/wallet/deposit", "DEPOSIT_BALANCE"),
            Map.entry("POST:/shop/wallet/change-pin", "CHANGE_PIN"),
            Map.entry("POST:/shop/wallet/transfer", "TRANSFER_MONEY")
            );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestId = UUID.randomUUID().toString();
        Instant requestTime = Instant.now();

        String path = httpRequest.getRequestURI();
        String params = (httpRequest.getQueryString() != null) ? "?" + httpRequest.getQueryString() : "";
        String operationCode = getOperationCode(httpRequest);

        logger.info("Запрос: {} {}{} | RequestID={} | Время={} | Операция={}",
                httpRequest.getMethod(), path, params, requestId, requestTime, operationCode);

        chain.doFilter(request, response);

        long duration = Duration.between(requestTime, Instant.now()).toMillis();
        int status = httpResponse.getStatus();

        logger.info("Ответ: {} {}{} | RequestID={} | Время={} | Длительность={}мс | Операция={} | Статус={}",
                httpRequest.getMethod(), path, params, requestId, requestTime, duration, operationCode, status);
    }

    private String getOperationCode(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String key = method + ":" + path;

        // Проверяем полное совпадение (статические пути)
        if (OPERATION_CODES.containsKey(key)) {
            return OPERATION_CODES.get(key);
        }

        // Проверяем совпадение с шаблонами (динамические пути)
        for (Map.Entry<String, String> entry : OPERATION_CODES.entrySet()) {
            if (isPathMatching(entry.getKey(), key)) {
                return entry.getValue();
            }
        }

        return "UNKNOWN_OPERATION";
    }


    private boolean isPathMatching(String template, String path) {
        String regex = template.replaceAll("\\{[^/]+}", "[^/]+"); // Заменяем {param} на regex
        return path.matches(regex);
    }
}