package com.example.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	// need this to access database
	// since h2 is in the classpath it auto configures datasource, it can be configured via proeprties file
	@Autowired
	private DataSource datasource;
	
	/**
	 * Authentication - check user exists and is valid
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {		
		auth.jdbcAuthentication()
			.dataSource(datasource)
			.withDefaultSchema()
			// we can use the schema.sql and data.sql file to set data
			// using the following:
			// https://docs.spring.io/spring-security/site/docs/current/reference/html5/#user-schema				
			.withUser(
					User.withUsername("user")
					.password("pass")
					.roles("USER")
					
					)
			.withUser(					User.withUsername("admin")
					.password("pass")
					.roles("ADMIN")
					);
		
		// if you have a database that is structured differently
		// you can use spring security to let it know how to query custom tables
		//
		//  .usersByUsernameQuery("select username, password, enabled form users where username = ?")
		//  .auhtoritiesByUsernameQuery("select username, authority from authorities where username = ?")
			
	}

	/**
	 * Authorisation - whats users can access
	 * - starts most restrictive to least...
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/admin").hasRole("ADMIN")
			.antMatchers("/user").hasAnyRole("USER","ADMIN")
			.antMatchers("/").permitAll()
			.and().formLogin();
	}
	
	
	@Bean
	public PasswordEncoder getEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	

}
