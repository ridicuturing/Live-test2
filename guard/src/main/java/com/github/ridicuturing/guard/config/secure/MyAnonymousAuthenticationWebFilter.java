package com.github.ridicuturing.guard.config.secure;

/*
@Slf4j
//@Component
public class MyAnonymousAuthenticationWebFilter implements WebFilter {

    private String key;

    private Object principal;

    private List<GrantedAuthority> authorities;

    public static final String COOKIE_SESSION_NAME = "user_session_id";

    @Lazy
    private ChatUserMapper chatUserMapper;

    public MyAnonymousAuthenticationWebFilter() {
        this("nonono", "anonymousUser", AuthorityUtils.createAuthorityList("CHAT"));
    }

    */
/**
     * @param key         key the key to identify tokens created by this filter
     * @param principal   the principal which will be used to represent anonymous users
     * @param authorities the authority list for anonymous users
     *//*

    public MyAnonymousAuthenticationWebFilter(String key, Object principal, List<GrantedAuthority> authorities) {
        Assert.hasLength(key, "key cannot be null or empty");
        Assert.notNull(principal, "Anonymous authentication principal must be set");
        Assert.notNull(authorities, "Anonymous authorities must be set");
        this.key = key;
        this.principal = principal;
        this.authorities = authorities;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, HttpCookie> cookies1 = request.getCookies();
        HttpCookie loginId = cookies1.getFirst(COOKIE_SESSION_NAME);
        */
/*ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        List<String> cookies = headers.get(HttpHeaders.COOKIE);*//*

        String loginSessionId = loginId != null ? loginId.getValue() : null;
        boolean hasLoginSession = StringUtils.hasText(loginSessionId);
        return ReactiveSecurityContextHolder.getContext().switchIfEmpty(Mono.defer(() -> {
            //had login before
            if (hasLoginSession) {
                return chatUserMapper.findOneBySessionId(loginSessionId)
                        //cannot find user in DB,so has to create new user
                        .switchIfEmpty(Mono.defer(() -> {
                            ChatUser newUser = ChatUser.builder()
                                    .sessionId(loginSessionId)
                                    .updateTime(LocalDateTime.now())
                                    .createTime(LocalDateTime.now())
                                    .userSn(IdUtil.fastSimpleUUID())
                                    .build();
                            return chatUserMapper.save(newUser);
                        }))
                        .flatMap(u -> exchange.getSession()
                                .doOnNext(webSession -> webSession.getAttributes().put("chatUser", u))
                                .thenReturn(u))
                        .map(chatUser -> {
                            Authentication authentication = createAuthentication(SecurityConstant.AuthorityEnum.CHAT.name());
                            return new MySecurityContextImpl(chatUser, authentication);
                        })
                        .flatMap(securityContext -> chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                                .thenReturn(securityContext));

            } else {
                ChatUser newUser = ChatUser.builder()
                        .sessionId(IdUtil.fastSimpleUUID())
                        .updateTime(LocalDateTime.now())
                        .createTime(LocalDateTime.now())
                        .userSn(IdUtil.fastSimpleUUID())
                        .build();
                exchange.getResponse().addCookie(ResponseCookie.from(COOKIE_SESSION_NAME, newUser.getSessionId())
                        .path("/")
                        .maxAge(Duration.ofDays(3650)).build());
                return chatUserMapper.save(newUser)
                        .flatMap(u -> exchange.getSession()
                                .doOnNext(webSession -> webSession.getAttributes().put("chatUser", u))
                                .thenReturn(u))
                        .map(w -> {
                            Authentication authentication = createAuthentication(SecurityConstant.AuthorityEnum.CHAT.name());
                            return new MySecurityContextImpl(w, authentication);
                        }).flatMap(securityContext -> chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
                                .thenReturn(securityContext));
            }
        })).flatMap(s -> chain.filter(exchange));

    }

    protected Authentication createAuthentication(String... authorities) {
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken(this.key, this.principal, AuthorityUtils.createAuthorityList(authorities));
        return anonymousAuthenticationToken;
    }

}
*/
