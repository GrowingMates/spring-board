package com.board.config.filter;

/*@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(FilterConfig.class);
    private final JwtUtil jwtUtil;

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil); // 수동으로 필터 Bean 등록
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter jwtAuthFilter) {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(jwtAuthFilter);
        registrationBean.addUrlPatterns("/api/*"); // 특정 URL 패턴에만 필터 적용
        registrationBean.setOrder(1); // 우선순위 설정 (낮을수록 먼저 실행)
        logger.info("222222222222222222222222222222 " + registrationBean + " " + jwtAuthFilter);
        return registrationBean;
    }
}*/
