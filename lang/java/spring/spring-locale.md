spring-locale



初始化

```java
private void initLocaleResolver(ApplicationContext context) {
   try {
      this.localeResolver = context.getBean(LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);
      if (logger.isTraceEnabled()) {
         logger.trace("Detected " + this.localeResolver);
      }
      else if (logger.isDebugEnabled()) {
         logger.debug("Detected " + this.localeResolver.getClass().getSimpleName());
      }
   }
   catch (NoSuchBeanDefinitionException ex) {
      // We need to use the default.
      this.localeResolver = getDefaultStrategy(context, LocaleResolver.class);
      if (logger.isTraceEnabled()) {
         logger.trace("No LocaleResolver '" + LOCALE_RESOLVER_BEAN_NAME +
               "': using default [" + this.localeResolver.getClass().getSimpleName() + "]");
      }
   }
}
```



实现该接口的类，被纳入 spring 的管理中

```java
public interface LocaleResolver {

	/**
	 * Resolve the current locale via the given request.
	 * Can return a default locale as fallback in any case.
	 * @param request the request to resolve the locale for
	 * @return the current locale (never {@code null})
	 */
	Locale resolveLocale(HttpServletRequest request);

	/**
	 * Set the current locale to the given one.
	 * @param request the request to be used for locale modification
	 * @param response the response to be used for locale modification
	 * @param locale the new locale, or {@code null} to clear the locale
	 * @throws UnsupportedOperationException if the LocaleResolver
	 * implementation does not support dynamic changing of the locale
	 */
	void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale);

}
```



根据 Header 进行 Locale 解析

```java
public class AcceptHeaderLocaleResolver implements LocaleResolver {
	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Locale defaultLocale = getDefaultLocale();
		if (defaultLocale != null && request.getHeader("Accept-Language") == null) {
			return defaultLocale;
		}
		Locale requestLocale = request.getLocale();
		List<Locale> supportedLocales = getSupportedLocales();
		if (supportedLocales.isEmpty() || supportedLocales.contains(requestLocale)) {
			return requestLocale;
		}
		Locale supportedLocale = findSupportedLocale(request, supportedLocales);
		if (supportedLocale != null) {
			return supportedLocale;
		}
		return (defaultLocale != null ? defaultLocale : requestLocale);
	}

	@Nullable
	private Locale findSupportedLocale(HttpServletRequest request, List<Locale> supportedLocales) {
		Enumeration<Locale> requestLocales = request.getLocales();
		Locale languageMatch = null;
		while (requestLocales.hasMoreElements()) {
			Locale locale = requestLocales.nextElement();
			if (supportedLocales.contains(locale)) {
				if (languageMatch == null || languageMatch.getLanguage().equals(locale.getLanguage())) {
					// Full match: language + country, possibly narrowed from earlier language-only match
					return locale;
				}
			}
			else if (languageMatch == null) {
				// Let's try to find a language-only match as a fallback
				for (Locale candidate : supportedLocales) {
					if (!StringUtils.hasLength(candidate.getCountry()) &&
							candidate.getLanguage().equals(locale.getLanguage())) {
						languageMatch = candidate;
						break;
					}
				}
			}
		}
		return languageMatch;
	}

}
```



根据 Session 进行 Locale 解析

```java
public class SessionLocaleResolver extends AbstractLocaleContextResolver {
	public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
		return new TimeZoneAwareLocaleContext() {
			@Override
			public Locale getLocale() {
				Locale locale = (Locale) WebUtils.getSessionAttribute(request, localeAttributeName);
				if (locale == null) {
					locale = determineDefaultLocale(request);
				}
				return locale;
			}
			@Override
			@Nullable
			public TimeZone getTimeZone() {
				TimeZone timeZone = (TimeZone) WebUtils.getSessionAttribute(request, timeZoneAttributeName);
				if (timeZone == null) {
					timeZone = determineDefaultTimeZone(request);
				}
				return timeZone;
			}
		};
	}
}
```



根据 Cookie 进行 Locale 解析

```java
public class CookieLocaleResolver extends CookieGenerator implements LocaleContextResolver {
	private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
		if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
			Locale locale = null;
			TimeZone timeZone = null;

			// Retrieve and parse cookie value.
			String cookieName = getCookieName();
			if (cookieName != null) {
				Cookie cookie = WebUtils.getCookie(request, cookieName);
				if (cookie != null) {
					String value = cookie.getValue();
					String localePart = value;
					String timeZonePart = null;
					int separatorIndex = localePart.indexOf('/');
					if (separatorIndex == -1) {
						// Leniently accept older cookies separated by a space...
						separatorIndex = localePart.indexOf(' ');
					}
					if (separatorIndex >= 0) {
						localePart = value.substring(0, separatorIndex);
						timeZonePart = value.substring(separatorIndex + 1);
					}
					try {
						locale = (!"-".equals(localePart) ? parseLocaleValue(localePart) : null);
						if (timeZonePart != null) {
							timeZone = StringUtils.parseTimeZoneString(timeZonePart);
						}
					}
					catch (IllegalArgumentException ex) {
						String cookieDescription = "invalid locale cookie '" + cookieName +
								"': [" + value + "] due to: " + ex.getMessage();
						if (request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) != null) {
							// Error dispatch: ignore locale/timezone parse exceptions
							if (logger.isDebugEnabled()) {
								logger.debug("Ignoring " + cookieDescription);
							}
						}
						else {
							throw new IllegalStateException("Encountered " + cookieDescription);
						}
					}
					if (logger.isTraceEnabled()) {
						logger.trace("Parsed cookie value [" + cookie.getValue() + "] into locale '" + locale +
								"'" + (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : ""));
					}
				}
			}

			request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME,
					(locale != null ? locale : determineDefaultLocale(request)));
			request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME,
					(timeZone != null ? timeZone : determineDefaultTimeZone(request)));
		}
	}
}
```

