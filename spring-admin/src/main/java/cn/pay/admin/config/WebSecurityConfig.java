package cn.pay.admin.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import cn.pay.admin.security.AdminAuthenticationProvider;
import cn.pay.admin.security.AdminLoginFailureHandler;
import cn.pay.admin.security.AdminLoginSuccessHandler;
import cn.pay.core.consts.SysConst;

/**
 * SpringSecurity 配置类
 * 
 * @author Qiujian
 *
 */
@Configuration
//@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	//@Autowired
	//private AdminFilterSecurityInterceptor adminFilterSecurityInterceptor;

	//@Autowired
	//private UserDetailsService userDetailsService;

	@Resource
	private AdminAuthenticationProvider authenticationProvider;

	@Autowired
	private AdminLoginSuccessHandler successHandler;
	@Autowired
	private AdminLoginFailureHandler failureHandler;

	/**
	 * 认证关系构建
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
		auth.authenticationProvider(authenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();// 禁用csrf防护

		http.authorizeRequests()
				// 静态资源可以完全访问
				//.antMatchers("/css/**", "/images/**", "/js/**", "/tags/**", "/login.html").permitAll()
				// 登录登出url可以完全访问
				//.antMatchers("/loginInfo/ajax.do").permitAll()
				// .antMatchers("/index.do").access("hasRole('后台首页')")
				// 所有以.do 结尾的请求需要登录之后才能访问
				//.antMatchers("*.html").authenticated()

				.and()//
				// 登录配置
				.formLogin()
				// 登录页面
				.loginPage(SysConst.LOGIN_HTML)
				// 登录处理URL
				.loginProcessingUrl(SysConst.LOGIN_INFO_LOGIN_DO).permitAll()
				// 登录失败跳转的页面
				//.failureForwardUrl("/login.html")
				// 登录成功之后跳转的url
				//.defaultSuccessUrl("/index.do")
				.successHandler(successHandler)
				.failureHandler(failureHandler)
				
				.and()//
				// 登出配置
				.logout()
				// 登出的URL
				.logoutUrl(SysConst.LOGIN_INFO_LOGOUT_DO).permitAll()
				// 登出成功的URL
				.logoutSuccessUrl(SysConst.LOGIN_HTML)
				// 登出时让HttpSession无效
				.invalidateHttpSession(true)
				;

		//http.addFilterBefore(adminFilterSecurityInterceptor, FilterSecurityInterceptor.class);
	}

}
